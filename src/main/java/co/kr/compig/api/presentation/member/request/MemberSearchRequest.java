package co.kr.compig.api.presentation.member.request;

import java.util.List;

import co.kr.compig.global.code.UserType;
import co.kr.compig.global.dto.pagination.PageableRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MemberSearchRequest extends PageableRequest {
	@Schema(description = "사용자명", example = "홍길동")
	private String userNm;
	@Schema(description = "휴대전화 번호", example = "01011111111")
	private String telNo;
	@Schema(description = "사용자 타입", example = "SYS_ADMIN")
	private List<UserType> userType;

}