package co.kr.compig.global.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UseYn implements BaseEnumCode<String> {
	Y("Y", "use.yn.yes"),
	N("N", "use.yn.no");

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
