package co.kr.compig.global.error.exception;

import co.kr.compig.global.error.model.ErrorCode;

public class BizException extends RuntimeException {

	private final ErrorCode errorCode;

	public BizException(String clientMessage) {
		super(clientMessage);
		this.errorCode = ErrorCode.ERROR;
		this.errorCode.setClientMessage(clientMessage);
	}

	public BizException(String clientMessage, Throwable cause) {
		super(clientMessage, cause);
		this.errorCode = ErrorCode.ERROR;
		this.errorCode.setClientMessage(clientMessage);
	}

	public BizException(ErrorCode ee, Throwable cause) {
		super(ee.getClientMessage(), cause);
		this.errorCode = ee;
	}

	public BizException(ErrorCode ee, String customClientMessage) {
		super(customClientMessage);
		this.errorCode = ee;
		this.errorCode.setClientMessage(customClientMessage);
	}

	public BizException(ErrorCode errorCode) {
		super(errorCode.getClientMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
