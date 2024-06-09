package co.kr.compig.api.application.social;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import co.kr.compig.api.infra.auth.social.google.GoogleAuthApi;
import co.kr.compig.api.infra.auth.social.google.model.GoogleLoginResponse;
import co.kr.compig.api.infra.auth.social.google.model.GoogleProperties;
import co.kr.compig.api.presentation.social.request.SocialLoginRequest;
import co.kr.compig.api.presentation.social.response.SocialUserResponse;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.utils.GsonLocalDateTimeAdapter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("googleLogin")
public class GoogleLoginServiceImpl implements SocialLoginService {

	private final GoogleAuthApi googleAuthApi;
	private final GoogleProperties googleProperties;
	private GoogleIdTokenVerifier googleIdTokenVerifier;

	@PostConstruct
	public void initialize() {
		log.info("### Initializing googleIdTokenVerifier");
		googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
			new GsonFactory())
			.setAudience(Collections.singletonList(googleProperties.getRestApiKey()))
			.build();
	}

	@Override
	public MemberRegisterType getServiceName() {
		return MemberRegisterType.GOOGLE;
	}

	@Override //token
	public SocialUserResponse appSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		log.info(getServiceName().getCode() + " appSocialUserResponse");
		try {
			ResponseEntity<?> response = googleAuthApi.accessTokenToTokenInfo(
				socialLoginRequest.getToken());

			log.info(response.toString());

			Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
				.create();

			GoogleLoginResponse googleLoginResponse = gson.fromJson(response.getBody().toString(),
				GoogleLoginResponse.class);

			return SocialUserResponse.builder()
				.socialId(googleLoginResponse.getSub())
				.memberRegisterType(getServiceName())
				.email(googleLoginResponse.getEmail())
				.build();
		} catch (HttpServerErrorException e) {
			log.error("Google getUserInfo HttpServerErrorException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		} catch (UnknownHttpStatusCodeException e) {
			log.error("Google getUserInfo UnknownHttpStatusCodeException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		}

		return null;
	}

}
