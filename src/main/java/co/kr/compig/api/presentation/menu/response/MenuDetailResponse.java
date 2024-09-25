package co.kr.compig.api.presentation.menu.response;

import java.util.List;
import java.util.Set;

import co.kr.compig.api.domain.permission.MenuPermission;
import co.kr.compig.global.code.MenuDivCode;
import co.kr.compig.global.code.MenuTypeCode;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.dto.BaseAudit;
import co.kr.compig.global.embedded.CreatedAndUpdated;
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
public class MenuDetailResponse extends BaseAudit {
	private Long menuId;
	private MenuDivCode menuDiv;
	private String menuNm;
	private String menuUrl;
	private Integer seq;
	private MenuTypeCode menuType;
	private UseYn useYn;
	private List<MenuDetailResponse> child; // 자식 메뉴 정보
	private Set<MenuPermission> menuPermissions; // 메뉴 권한 정보
	private CreatedAndUpdated createdAndModified; // 생성 및 수정 정보
}
