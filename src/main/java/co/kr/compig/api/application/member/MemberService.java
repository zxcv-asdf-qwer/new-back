package co.kr.compig.api.application.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.compig.api.domain.member.Member;
import co.kr.compig.api.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Member getMemberByIdSecurity(String memberId) {
		return memberRepository.findById(memberId).orElse(null);
	}

}
