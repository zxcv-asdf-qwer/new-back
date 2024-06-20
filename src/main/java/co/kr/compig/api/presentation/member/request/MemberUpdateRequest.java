package co.kr.compig.api.presentation.member.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import co.kr.compig.global.code.GenderCode;
import co.kr.compig.global.code.UserGroup;
import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "형식: 홍 길동")
	private String userNm; // 사용자 명
	@NotBlank
	@Schema(description = "휴대폰 번호", example = "01011111111")
	private String telNo; // 연락처
	@Schema(description = "성별", example = "F, M, N")
	private GenderCode gender; // 성별
	@NotNull
	@Schema(description = "사용자 타입", example = "SYS_ADMIN")
	private UserGroup userGroup; //사용자 타입
	@Schema(description = "주소")
	private String address1; //주소
	@Schema(description = "주소")
	private String address2; //주소
	@Schema(description = "자기소개")
	private String introduce; //자기소개
	@Schema(description = "앱 푸시알림 수신동의")
	private boolean marketingAppPush; // 앱 푸시알림 수신동의
	@Schema(description = "현재 groupKeys")
	@NotNull
	private List<String> groupKeys;
}
