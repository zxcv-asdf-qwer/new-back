package co.kr.compig.api.presentation.member.response;

import java.util.Set;

import co.kr.compig.api.domain.member.model.GroupDto;
import co.kr.compig.global.code.DeptCode;
import co.kr.compig.global.code.GenderCode;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserType;
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
public class MemberResponse extends BaseAudit {

	private String memberId; //keycloak ID
	private String userId; //user ID
	private String userNm; // 사용자 명
	private String email; // 이메일
	private String telNo; // 연락처
	private GenderCode gender; // 성별
	private UseYn useYn; // 사용유무
	private UserType userType; //사용자 타입
	private DeptCode deptCode; // 부서 구분
	private MemberRegisterType memberRegisterType; // 회원가입 유형
	private String address1; //주소
	private String address2; //주소
	private String introduce; //자기소개
	private boolean marketingAppPush; // 앱 푸시알림 수신동의

	private Set<GroupDto> groups;
}
