package co.kr.compig.api.presentation.menu.response;

import co.kr.compig.global.dto.BaseAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class MenuResponse extends BaseAudit {
	private Long menuId; // 메뉴 ID
	private String menuNm; // 메뉴명
}
