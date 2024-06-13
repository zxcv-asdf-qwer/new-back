package co.kr.compig.api.infra.auth.keycloak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogoutRequest {

	private String client_id;
	private String client_secret;
	private String refresh_token;

}
