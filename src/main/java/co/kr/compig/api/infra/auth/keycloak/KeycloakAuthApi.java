package co.kr.compig.api.infra.auth.keycloak;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import co.kr.compig.api.infra.auth.keycloak.model.KeycloakAccessTokenRequest;
import co.kr.compig.api.infra.auth.keycloak.model.KeycloakRefreshTokenRequest;
import co.kr.compig.api.infra.auth.keycloak.model.LogoutRequest;
import jakarta.ws.rs.core.MediaType;

@FeignClient(name = "keycloakClient", url = "${auth.build-info.server-url}")
public interface KeycloakAuthApi {

	@PostMapping(value = "/realms/${auth.build-info.realm}/protocol/openid-connect/token",
		consumes = MediaType.APPLICATION_FORM_URLENCODED)
	ResponseEntity<String> getAccessToken(
		@RequestBody KeycloakAccessTokenRequest keycloakAccessTokenRequest);

	@PostMapping(value = "/realms/${auth.build-info.realm}/protocol/openid-connect/token",
		consumes = MediaType.APPLICATION_FORM_URLENCODED)
	ResponseEntity<String> getRefreshToken(
		@RequestBody KeycloakRefreshTokenRequest keycloakRefreshTokenRequest);

	@PostMapping(value = "/realms/${auth.build-info.realm}/protocol/openid-connect/logout",
		consumes = MediaType.APPLICATION_FORM_URLENCODED)
	ResponseEntity<String> logout(
		@RequestBody LogoutRequest logoutRequest
	);
}
