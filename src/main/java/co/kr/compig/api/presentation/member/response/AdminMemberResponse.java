package co.kr.compig.api.presentation.member.response;

import java.util.Set;

import co.kr.compig.api.domain.member.model.GroupDto;
import co.kr.compig.global.code.DeptCode;
import co.kr.compig.global.dto.BaseAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AdminMemberResponse extends BaseAudit {

	private String memberId; //keycloak ID
	private String userNm; // 사용자 명
	private String userId; //user ID
	private DeptCode deptCode; //부서 코드
	private String email; // 이메일
	private String telNo; // 연락처

	private Set<GroupDto> groups;
}
