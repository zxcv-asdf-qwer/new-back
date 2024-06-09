package co.kr.compig.api.application.social;

import org.springframework.stereotype.Service;

import co.kr.compig.global.code.MemberRegisterType;

@Service
public interface SocialLoginService {

	MemberRegisterType getServiceName();

}
