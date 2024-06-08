package co.kr.compig.api.presentation.member.request;

import java.util.List;

import co.kr.compig.global.code.DeptCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMemberUpdate {

	@Schema(description = "형식: 홍 길동")
	private String userNm; //이름
	@Schema(description = "새 비밀번호", example = "password")
	private String newUserPw;   // 새 비밀번호
	@Schema(description = "새 비밀번호 확인", example = "password")
	private String chkUserPw;   // 새 비밀번호 확인
	@Schema(description = "사용자 이메일", example = "email@email.com")
	private String email; // 사용자 이메일
	@Schema(description = "휴대폰 번호", example = "01011111111")
	private String telNo; // 휴대폰번호
	@Schema(description = "관리자 DEVELOPER, OPERATION")
	private DeptCode deptCode; //부서코드
	@Schema(description = "현재 groupKeys")
	private List<String> groupKeys;
}
