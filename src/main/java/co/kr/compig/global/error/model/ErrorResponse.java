package co.kr.compig.global.error.model;

import static lombok.AccessLevel.*;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ErrorResponse {

	private OffsetDateTime timestamp;
	private String message;
	private String path;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<FieldError> errors;

	private ErrorResponse(final String message, final String path) {
		this.timestamp = OffsetDateTime.now();
		this.message = message;
		this.path = path;
	}

	public static ErrorResponse of(ErrorCode errorCode, WebRequest er) {
		return new ErrorResponse(errorCode.getClientMessage(),
			((ServletWebRequest)er).getRequest().getRequestURI());
	}

	public static ErrorResponse of(Exception e, WebRequest er) {
		return new ErrorResponse(e.getMessage(), ((ServletWebRequest)er).getRequest().getRequestURI());
	}

	public void addFieldError(List<FieldError> errors) {
		this.errors = errors;
	}

	@Getter
	@AllArgsConstructor
	public static class FieldError {

		private final String field;
		private final String message;
	}

}
