package co.kr.compig.global.code;

public interface BaseEnumCode<T> {

	T getCode();

	String getDesc();

	/**
	 * Enum 데이터를 desc(code) 형태의 스트링으로 리턴
	 */
	default String convertString() {
		String builder = getDesc()
			+ "("
			+ getCode()
			+ ")";

		return builder;
	}
}
