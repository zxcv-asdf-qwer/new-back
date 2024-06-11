package co.kr.compig.api.application.social;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import co.kr.compig.api.presentation.social.request.SocialLoginRequest;
import co.kr.compig.api.presentation.social.response.SocialUserResponse;
import co.kr.compig.global.code.MemberRegisterType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component
@Qualifier("defaultLoginService")
public class LoginServiceImpl implements SocialLoginService {

	@Override
	public MemberRegisterType getServiceName() {
		return MemberRegisterType.GENERAL;
	}

	@Override
	public SocialUserResponse tokenSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		return null;
	}

	@Override
	public SocialUserResponse codeSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		return null;
	}
}
