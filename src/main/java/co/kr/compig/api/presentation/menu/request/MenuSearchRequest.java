package co.kr.compig.api.presentation.menu.request;

import co.kr.compig.global.code.MenuDivCode;
import co.kr.compig.global.code.MenuTypeCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@SuperBuilder(toBuilder = true)
public class MenuSearchRequest {

	private MenuDivCode menuDiv; // 메뉴 구분
	private String menuNm; // 메뉴명
	private MenuTypeCode menuType; // 메뉴 타입
}
