package co.kr.compig.global.error.handler;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	/**
	 * @Async void 메소드 예외 전파
	 */

	@Override
	public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
		log.info("Exception message - " + throwable.getMessage());
		log.info("Method name - " + method.getName());
		for (Object param : params) {
			log.info("Parameter value - " + param);
		}
	}
}
