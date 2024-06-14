package co.kr.compig.api.application.social;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import co.kr.compig.api.presentation.member.request.SocialLoginRequest;
import co.kr.compig.api.presentation.member.response.SocialUserResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component
public class DefaultLoginServiceImpl implements SocialLoginService {

	@Override
	public SocialUserResponse tokenSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		return null;
	}

	@Override
	public SocialUserResponse codeSocialUserResponse(SocialLoginRequest socialLoginRequest) {
		return null;
	}
}
