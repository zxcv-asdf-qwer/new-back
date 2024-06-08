package co.kr.compig.api.presentation.member.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import co.kr.compig.global.code.GenderCode;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
	@NotBlank
	@Length(min = 2, max = 100)
	@Pattern(regexp = "^[\\sㄱ-ㅎ가-힣A-Za-z0-9_-]{2,100}$")
	private String userNm; // 사용자 명

	@NotBlank
	private String telNo; // 연락처

	@NotNull
	private GenderCode gender; // 성별
	@NotNull
	private UseYn useYn; // 사용유무
	@NotNull
	private UserType userType; //사용자 타입
	@NotBlank
	private String address1; //주소
	@NotBlank
	private String address2; //주소

	private String introduce; //자기소개

	private boolean marketingAppPush; // 앱 푸시알림 수신동의

	@NotNull
	private List<String> groupKeys;
}
