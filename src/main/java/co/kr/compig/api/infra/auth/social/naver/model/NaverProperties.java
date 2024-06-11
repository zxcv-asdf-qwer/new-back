package co.kr.compig.api.infra.auth.social.naver.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.naver")
public class NaverProperties {

	private String clientId;
	private String clientSecret;
	private String authorizationGrantType;
	private String redirectUri;
}
