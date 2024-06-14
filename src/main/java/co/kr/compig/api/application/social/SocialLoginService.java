package co.kr.compig.api.application.social;

import org.springframework.stereotype.Service;

import co.kr.compig.api.presentation.member.request.LeaveRequest;
import co.kr.compig.api.presentation.member.request.SocialLoginRequest;
import co.kr.compig.api.presentation.member.response.SocialUserResponse;

@Service
public interface SocialLoginService {

	//token to userInfo
	SocialUserResponse tokenSocialUserResponse(SocialLoginRequest socialLoginRequest);

	SocialUserResponse codeSocialUserResponse(SocialLoginRequest socialLoginRequest);

	void revoke(LeaveRequest leaveRequest);
}
