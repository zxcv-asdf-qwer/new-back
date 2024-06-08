package co.kr.compig.global.error.exception;

import co.kr.compig.global.error.model.ErrorCode;

public class NotExistDataException extends BizException {

	public NotExistDataException() {
		this(ErrorCode.INVALID_NOT_EXIST_DATA);
	}

	public NotExistDataException(ErrorCode errorCode) {
		super(errorCode);
	}

	public NotExistDataException(String message) {
		super(ErrorCode.INVALID_NOT_EXIST_DATA, message);
	}
}
