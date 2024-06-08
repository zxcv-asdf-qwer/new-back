package co.kr.compig.global.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("auth.build-info")
public class KeycloakProperties {

	private String serverUrl;
	private String realm;
	private String clientId;
	private String clientSecret;
	private int poolSize;

}
