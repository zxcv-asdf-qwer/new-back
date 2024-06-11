package co.kr.compig.api.infra.auth.social.naver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "naverAuth", url = "https://nid.naver.com")
public interface NaverAuthApi {
	@GetMapping("/oauth2.0/token")
	ResponseEntity<String> getAccessToken(
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("grant_type") String grantType,
		@RequestParam("redirect_uri") String redirectUri,
		@RequestParam("code") String code
	);

	@GetMapping("/oauth2.0/token")
	ResponseEntity<String> revokeAccessToken(
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("grant_type") String grantType, //delete
		@RequestParam("access_token") String accessToken,
		@RequestParam("service_provider") String serviceProvider //NAVER
	);
}
