package co.kr.compig.api.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {
	Optional<MemberGroup> findByMember_id(String id);

}
