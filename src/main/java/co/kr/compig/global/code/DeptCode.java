package co.kr.compig.global.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DeptCode implements BaseEnumCode<String> {

	DEVELOPER("DEVELOPER", "개발"),
	OPERATION("OPERATION", "운영");

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
