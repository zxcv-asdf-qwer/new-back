package co.kr.compig.global.error.model;

import org.springframework.http.HttpStatus;

//TODO clientMessage Spring-Cloud-Bus로 변경
public enum ErrorCode {
	//common
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 정보로 요청하였습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"),
	ERROR(HttpStatus.valueOf(420), "오류가 발생하였습니다."),
	INVALID_NOT_EXIST_DATA(HttpStatus.valueOf(422), "데이터가 없습니다."),
	;

	private final HttpStatus httpStatus;
	private String clientMessage;

	ErrorCode(final HttpStatus httpStatus, final String clientMessage) {
		this.httpStatus = httpStatus;
		this.clientMessage = clientMessage;
	}

	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	public String getClientMessage() {
		return this.clientMessage;
	}

	public void setClientMessage(String clientMessage) {
		this.clientMessage = clientMessage;
	}
}
