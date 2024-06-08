package co.kr.compig.global.utils;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext ctx;

	/**
	 * 디자인 패턴 중 팩토리 패턴과 같음<br> 팩토리 패턴의 일부 측면을 스프링이 대신 처리하여 객체 생성과 관리에 신경쓰지 않고 비즈니스 로직에 집중할 수 있음<br>
	 * 주어진 이름과 타입의 객체 리턴<br> 사용권장 이유 서브클래스와 슈퍼클래스 간의 상속 관계에 따라 여러 빈이 등록되어 있을 수 있음<br>
	 */
	public static <T> Optional<T> getBean(String name, Class<T> requiredType) {
		if (containsBean(name)) {
			return Optional.of(ctx.getBean(name, requiredType));
		}

		return Optional.empty();
	}

	/**
	 * 주어진 이름의 빈이 있는지 여부
	 */
	public static boolean containsBean(String beanName) {
		return ctx.containsBean(beanName);
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		ApplicationContextUtil.ctx = ctx;
	}

	/**
	 * (로컬 환경에서 jvm 옵션에 profile 설정을 별도로 하지 않으면 spring이 기본 default profile 사용)
	 * 현재 활성화된 프로필 정보
	 */
	public static String getActiveProfile() {
		String[] profiles = getEnvironment().getActiveProfiles();

		if (ArrayUtils.isEmpty(profiles)) {
			profiles = getEnvironment().getDefaultProfiles();
		}

		return Arrays.stream(profiles).collect(joining(",", "[", "]"));
	}

	public static Environment getEnvironment() {
		return ctx.getEnvironment();
	}
}
