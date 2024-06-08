package co.kr.compig.global.error.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.error.model.ErrorCode;
import co.kr.compig.global.error.model.ErrorResponse;
import co.kr.compig.global.notify.NotifyMessage;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler {
	private final Optional<NotifyMessage> notifyMessage;
	private final Tracer tracer;

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleGlobalException(final Exception e,
		final WebRequest er) {
		log.error(e.getMessage(), e);
		getSimpleExceptionMsg(e);

		this.doSendSlackError(e);

		return new ResponseEntity<>(ErrorResponse.of(e, er), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BizException.class)
	protected ResponseEntity<ErrorResponse> handleBizException(final BizException e,
		final WebRequest er) {
		log.error(e.getMessage(), e);
		getSimpleExceptionMsg(e);

		return new ResponseEntity<>(ErrorResponse.of(e, er), e.getErrorCode().getHttpStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> customHandleMethodArgumentNotValid(
		MethodArgumentNotValidException ex, WebRequest request) {
		ErrorCode invalidInputValue = ErrorCode.INVALID_INPUT_VALUE;
		ErrorResponse errorResponse = ErrorResponse.of(invalidInputValue, request);
		List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
			.stream()
			.map(objectError -> {
				ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError(objectError.getField(),
					objectError.getDefaultMessage());
				fieldErrorLog(objectError);
				return fieldError;
			}).toList();
		errorResponse.addFieldError(fieldErrors);

		this.doSendSlackError(ex);
		return new ResponseEntity<>(errorResponse, invalidInputValue.getHttpStatus());
	}

	@ExceptionHandler(TypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleTypeMismatch(TypeMismatchException ex,
		WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.of(ex, request);
		List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();

		if (ex.getValue() != null) {
			fieldErrors.add(new ErrorResponse.FieldError((String)ex.getValue(), "잘못된 형식의 값을 입력하였습니다."));
		} else {
			fieldErrors.add(new ErrorResponse.FieldError("", "잘못된 형식의 값을 입력하였습니다."));
		}
		errorResponse.addFieldError(fieldErrors);

		this.doSendSlackError(ex);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	private void fieldErrorLog(org.springframework.validation.FieldError fieldErrors) {
		log.error(String.format("objectName -> %s\nmessage -> %s", fieldErrors.getObjectName(),
			fieldErrors.getDefaultMessage()));
	}

	private void getSimpleExceptionMsg(Exception e) {
		Arrays.stream(e.getStackTrace())
			.filter(ex -> ex.getClassName().contains("compig") && e.getMessage() != null)
			.findFirst()
			.ifPresent(err -> log.error((e.getMessage().equals("null") ? "" : e.getMessage() + "\n")
				+ err.getClassName() + "." + err.getMethodName() + " - " + err.getFileName() + " : "
				+ err.getLineNumber() + " line"));
	}

	private void doSendSlackError(Exception e) {
		Map<String, Object> tracerMap = new HashMap<>();
		try {
			tracerMap.put("traceId", tracer.currentSpan().context().traceId());
			tracerMap.put("spanId", tracer.currentSpan().context().spanId());
		} catch (Exception te) {
			log.error(te.getMessage(), te);
		}

		notifyMessage.ifPresent(service -> service.sendErrorMessage(e, tracerMap));
	}
}
