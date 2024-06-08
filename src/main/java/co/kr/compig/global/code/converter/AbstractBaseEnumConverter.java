package co.kr.compig.global.code.converter;

import java.util.Arrays;

import co.kr.compig.global.code.BaseEnumCode;
import jakarta.persistence.AttributeConverter;

public abstract class AbstractBaseEnumConverter<X extends Enum<X> & BaseEnumCode<Y>, Y> implements
	AttributeConverter<X, Y> {

	protected abstract String getEnumName();

	protected abstract X[] getValueList();

	@Override
	public Y convertToDatabaseColumn(X attribute) {
		return attribute == null ? null : attribute.getCode();
	}

	@Override
	public X convertToEntityAttribute(Y dbData) {
		return dbData == null ? null : Arrays.stream(getValueList())
			.filter(e -> e.getCode().equals(dbData))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(
				String.format("Enum %s에 Code %s가 없습니다.", getEnumName(), dbData)));
	}

}
