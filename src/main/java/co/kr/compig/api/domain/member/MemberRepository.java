package co.kr.compig.api.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.compig.global.code.UseYn;

public interface MemberRepository extends JpaRepository<Member, String> {
	Optional<Member> findByEmailAndUseYn(String email, UseYn useYn);

}
