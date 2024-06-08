package co.kr.compig.global.code.converter;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import co.kr.compig.global.code.UserType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
@MappedTypes(UserType.class)
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class UserTypeConverter extends AbstractBaseEnumConverter<UserType, String> {

	@Override
	protected String getEnumName() {
		return "UserType";
	}

	@Override
	protected UserType[] getValueList() {
		return UserType.values();
	}
}
