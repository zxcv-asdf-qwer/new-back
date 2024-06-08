package co.kr.compig.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig extends Oauth2RestTemplateConfigurer {

	private static final int CONNECT_TIMEOUT = 3; // 초 단위
	private static final int READ_TIMEOUT = 20; // 초 단위

	@Bean(name = "restTemplate")
	RestTemplate restTemplate() {
		return restTemplate(CONNECT_TIMEOUT, READ_TIMEOUT);
	}

}
