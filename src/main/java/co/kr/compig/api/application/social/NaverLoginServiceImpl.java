package co.kr.compig.api.application.social;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import co.kr.compig.api.infra.auth.social.naver.NaverAuthApi;
import co.kr.compig.api.infra.auth.social.naver.NaverUserApi;
import co.kr.compig.api.infra.auth.social.naver.model.NaverLoginResponse;
import co.kr.compig.api.infra.auth.social.naver.model.NaverProperties;
import co.kr.compig.api.presentation.member.request.SocialLoginRequest;
import co.kr.compig.api.presentation.member.response.SocialAuthResponse;
import co.kr.compig.api.presentation.member.response.SocialUserResponse;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.utils.GsonLocalDateTimeAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("naverLogin")
public class NaverLoginServiceImpl implements SocialLoginService {

	private final NaverUserApi naverUserApi;
	private final NaverAuthApi naverAuthApi;
	private final NaverProperties naverProperties;

	@Override
	public MemberRegisterType getServiceName() {
		return MemberRegisterType.NAVER;
	}

	@Override //accessToken
	public SocialUserResponse tokenSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		log.info(getServiceName().getCode() + " appSocialUserResponse");
		return this.accessTokenToUserInfo(socialLoginRequest.getToken());

	}

	@Override //code, state
	public SocialUserResponse codeSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		log.info(getServiceName().getCode() + " webSocialUserResponse");
		SocialAuthResponse socialAuthResponse = this.getAccessToken(socialLoginRequest.getCode());
		return this.accessTokenToUserInfo(socialAuthResponse.getAccess_token());
	}

	private SocialUserResponse accessTokenToUserInfo(String accessToken) {
		try {
			ResponseEntity<?> response = naverUserApi.accessTokenToUserInfo(
				"Bearer " + accessToken);

			log.info(response.toString());

			Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
				.create();

			NaverLoginResponse naverLoginResponse = gson.fromJson(
				response.getBody().toString(),
				NaverLoginResponse.class
			);

			return SocialUserResponse.builder()
				.socialId(naverLoginResponse.getResponse().getId())
				.memberRegisterType(getServiceName())
				.email(naverLoginResponse.getResponse().getEmail())
				.name(naverLoginResponse.getResponse().getName())
				.build();
		} catch (HttpServerErrorException e) {
			log.error("Naver accessTokenToUserInfo HttpServerErrorException - Status : {}, Message : {}",
				e.getStatusCode(),
				e.getMessage());
		} catch (UnknownHttpStatusCodeException e) {
			log.error("Naver accessTokenToUserInfo UnknownHttpStatusCodeException - Status : {}, Message : {}",
				e.getStatusCode(),
				e.getMessage());
		}
		return null;
	}

	private SocialAuthResponse getAccessToken(String code) {
		try {
			ResponseEntity<?> response = naverAuthApi.getAccessToken(
				naverProperties.getClientId(),
				naverProperties.getClientSecret(),
				naverProperties.getAuthorizationGrantType(),
				naverProperties.getRedirectUri(),
				code);

			log.info(response.toString());

			Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
				.create();

			SocialAuthResponse socialAuthResponse = gson.fromJson(
				response.getBody().toString(),
				SocialAuthResponse.class
			);

			return socialAuthResponse;
		} catch (HttpServerErrorException e) {
			log.error("Naver getAccessToken HttpServerErrorException - Status : {}, Message : {}",
				e.getStatusCode(),
				e.getMessage());
		} catch (UnknownHttpStatusCodeException e) {
			log.error("Naver getAccessToken UnknownHttpStatusCodeException - Status : {}, Message : {}",
				e.getStatusCode(),
				e.getMessage());
		}
		return null;
	}
}
