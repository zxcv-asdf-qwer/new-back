package co.kr.compig.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.kr.compig.global.interceptor.PagingInterceptor;

@Configuration
public class MybatisConfig {

	@Bean
	public PagingInterceptor pagingInterceptor() {
		return new PagingInterceptor();
	}
}
