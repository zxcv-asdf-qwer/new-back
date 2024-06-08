package co.kr.compig.global.error.exception;

import co.kr.compig.global.error.model.ErrorCode;
import lombok.Getter;

@Getter
public class KeyCloakRequestException extends BizException {

	public KeyCloakRequestException() {
		this(ErrorCode.INVALID_NOT_EXIST_DATA);
	}

	public KeyCloakRequestException(ErrorCode errorCode) {
		super(errorCode);
	}

	public KeyCloakRequestException(String message) {
		super(ErrorCode.INVALID_NOT_EXIST_DATA, message);
	}
}
