package co.kr.compig.global.code.converter;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import co.kr.compig.global.code.UserGroup;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
@MappedTypes(UserGroup.class)
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class UserTypeConverter extends AbstractBaseEnumConverter<UserGroup, String> {

	@Override
	protected String getEnumName() {
		return "UserType";
	}

	@Override
	protected UserGroup[] getValueList() {
		return UserGroup.values();
	}
}
