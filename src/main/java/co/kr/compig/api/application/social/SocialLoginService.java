package co.kr.compig.api.application.social;

import org.springframework.stereotype.Service;

import co.kr.compig.api.presentation.social.request.SocialLoginRequest;
import co.kr.compig.api.presentation.social.response.SocialUserResponse;
import co.kr.compig.global.code.MemberRegisterType;

@Service
public interface SocialLoginService {

	MemberRegisterType getServiceName();

	//token to userInfo
	SocialUserResponse tokeSocialUserResponse(SocialLoginRequest socialLoginRequest);

	SocialUserResponse codeSocialUserResponse(SocialLoginRequest socialLoginRequest);
}
