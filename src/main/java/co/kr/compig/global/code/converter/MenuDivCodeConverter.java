package co.kr.compig.global.code.converter;

import co.kr.compig.global.code.MenuDivCode;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MenuDivCodeConverter extends AbstractBaseEnumConverter<MenuDivCode, String> {

	@Override
	protected String getEnumName() {
		return "MenuDivCode";
	}

	@Override
	protected MenuDivCode[] getValueList() {
		return MenuDivCode.values();
	}
}
