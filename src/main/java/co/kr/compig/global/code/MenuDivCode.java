package co.kr.compig.global.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MenuDivCode implements BaseEnumCode<String> {
	ROOT("ROOT", "root"),
	MATCHING_MANAGEMENT("MATCHING_MANAGEMENT", "매칭 관리"),
	MANUAL_MANAGEMENT("MANUAL_REGISTRATION", "수기 관리"),
	MESSAGE_MANAGEMENT("MESSAGE_MANAGEMENT", "메시지 관리"),
	MEMBER_MANAGEMENT("MEMBER_MANAGEMENT", "회원 관리"),
	BOARD_MANAGEMENT("BOARD_MANAGEMENT", "게시판 관리"),
	SETTLEMENT_MANAGEMENT("SETTLEMENT_MANAGEMENT", "정산 관리"),
	SYSTEM_MANAGEMENT("SYSTEM_MANAGEMENT", "시스템 관리");

	private final String code;
	private final String desc;

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDesc() {
		return desc;
	}

}
