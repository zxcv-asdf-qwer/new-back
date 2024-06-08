package co.kr.compig.global.aspect;

import static co.kr.compig.global.utils.ApplicationContextUtil.*;

import java.time.Instant;
import java.time.LocalDateTime;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import co.kr.compig.api.application.log.ApiLogService;
import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.error.model.ErrorCode;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
	private final ApplicationContext context;
	private final Tracer tracer;

	private final ApiLogService apiLogService;

	@Around(value = "execution(* co.kr.compig.api.presentation..*Controller.*(..)) && !@annotation(org.springframework.web.bind.annotation.InitBinder)")
	public Object doLogging(ProceedingJoinPoint pjp) throws Throwable {
		final ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(
			((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest());

		final LocalDateTime createdOn = LocalDateTime.now();
		final Instant start = Instant.now();

		Object responseData = null;
		String error = null;
		try {
			return responseData = pjp.proceed();
		} catch (Exception e) {
			error = ExceptionUtils.getStackTrace(e);
			if (e instanceof BizException) {
				responseData = new ResponseEntity<>(((BizException)e).getErrorCode(),
					((BizException)e).getErrorCode().getHttpStatus());
			} else {
				responseData = new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			throw e;
		} finally {
			final Instant end = Instant.now();
			try {
				if (!getActiveProfile().contains("local")) {
					apiLogService.inboundSave(tracer, context, start, end, createdOn, request, responseData, pjp,
						error);
				}
			} catch (Exception e) {
				log.error("LoggingAspect Exception: {}", e.getMessage(), e);
			}
		}
	}

}
