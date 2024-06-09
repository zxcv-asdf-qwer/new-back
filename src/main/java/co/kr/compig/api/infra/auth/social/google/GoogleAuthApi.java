package co.kr.compig.api.infra.auth.social.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "googleAuth", url = "https://oauth2.googleapis.com")
public interface GoogleAuthApi {

	@GetMapping(value = "/tokeninfo")
	ResponseEntity<String> accessTokenToTokenInfo(
		@RequestParam("access_token") String access_token);

}
