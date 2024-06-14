package co.kr.compig.api.application.social;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import co.kr.compig.api.infra.auth.social.google.GoogleAuthApi;
import co.kr.compig.api.infra.auth.social.google.model.GoogleLoginResponse;
import co.kr.compig.api.infra.auth.social.google.model.GoogleProperties;
import co.kr.compig.api.presentation.member.request.LeaveRequest;
import co.kr.compig.api.presentation.member.request.SocialLoginRequest;
import co.kr.compig.api.presentation.member.response.SocialAuthResponse;
import co.kr.compig.api.presentation.member.response.SocialUserResponse;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.utils.GsonLocalDateTimeAdapter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
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

	@Override //token
	public SocialUserResponse tokenSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		log.info(socialLoginRequest.getMemberRegisterType() + " tokenSocialUserResponse");
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
				.memberRegisterType(socialLoginRequest.getMemberRegisterType())
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

	@Override //code
	public SocialUserResponse codeSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		log.info(MemberRegisterType.GOOGLE + " codeSocialUserResponse");
		SocialAuthResponse tokens = this.getTokens(socialLoginRequest.getCode());
		return this.idTokenToUserInfo(tokens.getId_token());
	}

	public SocialAuthResponse getTokens(String authorizationCode) {
		try {
			ResponseEntity<?> response = googleAuthApi.getAccessToken(
				googleProperties.getRestApiKey(),
				googleProperties.getSecretKey(),
				googleProperties.getAuthorizationGrantType(),
				googleProperties.getRedirectUri(),
				authorizationCode
			);

			log.info("google getTokens");
			log.info(response.toString());

			return new Gson()
				.fromJson(
					response.getBody().toString(),
					SocialAuthResponse.class
				);
		} catch (HttpServerErrorException e) {
			log.error("Google getAccessToken HttpServerErrorException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		} catch (UnknownHttpStatusCodeException e) {
			log.error("Google getAccessToken UnknownHttpStatusCodeException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		}
		return null;
	}

	public SocialUserResponse idTokenToUserInfo(String idToken) {
		try {
			String jsonString = "";
			try {
				GoogleIdToken verifiedIdToken = googleIdTokenVerifier.verify(idToken);
				GoogleIdToken.Payload payload = verifiedIdToken.getPayload();
				jsonString = payload.toString();
			} catch (GeneralSecurityException | IOException e) {
				log.warn(e.getLocalizedMessage());
			}

			log.info("google idTokenToUserInfo");
			log.info(jsonString.toString());

			Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
				.create();

			GoogleLoginResponse googleLoginResponse = gson.fromJson(jsonString, GoogleLoginResponse.class);

			return SocialUserResponse.builder()
				.socialId(googleLoginResponse.getSub())
				.memberRegisterType(MemberRegisterType.GOOGLE)
				.email(googleLoginResponse.getEmail())
				.name(googleLoginResponse.getName())
				.build();
		} catch (HttpServerErrorException e) {
			log.error("Google idTokenToUserInfo HttpServerErrorException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		} catch (UnknownHttpStatusCodeException e) {
			log.error("Google idTokenToUserInfo UnknownHttpStatusCodeException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		}
		return null;
	}

	@Override
	public void revoke(LeaveRequest leaveRequest) {
		log.info(MemberRegisterType.GOOGLE + " revoke");

		try {
			if (leaveRequest.getToken() != null) {
				googleAuthApi.revokeAccessToken(leaveRequest.getToken());
			} else {
				SocialAuthResponse tokens = this.getTokens(leaveRequest.getCode());
				googleAuthApi.revokeAccessToken(tokens.getAccess_token());
			}
		} catch (HttpServerErrorException e) {
			log.error("Google revoke HttpServerErrorException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		} catch (UnknownHttpStatusCodeException e) {
			log.error("Google revoke UnknownHttpStatusCodeException - Status : {}, Message : {}",
				e.getStatusCode(), e.getMessage());
		}
	}
}
