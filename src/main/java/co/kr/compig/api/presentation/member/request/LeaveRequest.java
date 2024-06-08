package co.kr.compig.api.presentation.member.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LeaveRequest {

	private String leaveReason; //탈퇴사유
	private String code;
}
