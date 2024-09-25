package co.kr.compig.api.presentation.menu.model;

import org.apache.ibatis.type.Alias;

import co.kr.compig.global.code.MenuDivCode;
import co.kr.compig.global.code.MenuTypeCode;
import co.kr.compig.global.code.UseYn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Alias("menuTree")
public class MenuTree {
	private Long id;
	private Long parentId;
	private String menuNm;
	private String menuNmEn;
	private String menuPath;
	private String menuEnPath;
	private String menuUrl;
	private Integer seq;
	private Integer menuLvl;
	private String isLeaf;
	private String iconCls;
	private Boolean isOpen;
	private MenuDivCode menuDiv;
	private MenuTypeCode menuType;
	private String path;
	private UseYn useYn;

	public MenuTreeDto converterDto() {
		MenuTreeDto menuTreeDto = new MenuTreeDto();
		menuTreeDto.setId(this.id);
		menuTreeDto.setParentId(this.parentId);
		menuTreeDto.setMenuNm(this.menuNm);
		menuTreeDto.setMenuNmEn(this.menuNmEn);
		menuTreeDto.setMenuPath(this.menuPath);
		menuTreeDto.setMenuEnPath(this.menuEnPath);
		menuTreeDto.setMenuUrl(this.menuUrl);
		menuTreeDto.setSeq(this.seq);
		menuTreeDto.setMenuLvl(this.menuLvl);
		menuTreeDto.setIsLeaf(this.isLeaf);
		menuTreeDto.setIconCls(this.iconCls);
		menuTreeDto.setIsOpen(this.isOpen);
		menuTreeDto.setIsOpen(this.isOpen);
		menuTreeDto.setMenuDiv(this.menuDiv);
		menuTreeDto.setMenuType(this.menuType);
		menuTreeDto.setUseYn(this.useYn);

		return menuTreeDto;
	}
}
