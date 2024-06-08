package co.kr.compig.global.notify;

import org.springframework.scheduling.annotation.Async;

public interface NotifyMessage {
	/**
	 * 메시지를 특정 채널로 전송
	 */
	@Async
	void sendMessage(String channelName, String message);

	/**
	 * 오류 내용을 전송
	 * @param e 오류
	 */
	@Async
	void sendErrorMessage(Throwable e);

	/**
	 * 오류 내용과 추가 데이터를 전송
	 * @param e 오류
	 * @param additionalData 추가 데이터
	 */
	@Async
	void sendErrorMessage(Throwable e, Object additionalData);
}
