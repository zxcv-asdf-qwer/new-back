package co.kr.compig.global.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MenuTypeCode implements BaseEnumCode<String> {
	MENU("MENU", "메뉴"),
	DETAIL("DETAIL", "상세 페이지"),
	POPUP("POPUP", "팝업");

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
