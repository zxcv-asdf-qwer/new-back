package co.kr.compig.api.infra.auth.social.google.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.google")
public class GoogleProperties {

	private String restApiKey;
	private String secretKey;
	private String redirectUri;
	private String authorizationGrantType;
}
