package co.kr.compig.global.error.exception;

import co.kr.compig.global.error.model.ErrorCode;

public class UploadException extends BizException {

	public UploadException(String message, Throwable cause) {
		super(message, cause);
	}

	public UploadException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public UploadException(ErrorCode errorCode) {
		super(errorCode);
	}

	public UploadException(String message) {
		super(message);
	}
}
