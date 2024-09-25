package co.kr.compig.api.presentation.menu.model;

import java.io.Serializable;

import co.kr.compig.global.code.MenuDivCode;
import co.kr.compig.global.code.MenuTypeCode;
import co.kr.compig.global.code.UseYn;
import lombok.Data;

@Data
public class MenuTreeDto implements Serializable {
	private static final long serialVersionUID = 7730243372714928589L;
	private Long id;
	private Long parentId;
	private String menuNm;
	private String menuNmEn;
	private String menuNmCn;
	private String menuPath;
	private String menuEnPath;
	private String menuCnPath;
	private String menuUrl;
	private Integer seq;
	private Integer menuLvl;
	private String isLeaf;
	private String iconCls;
	private Boolean isOpen;
	private MenuDivCode menuDiv;
	private MenuTypeCode menuType;
	private UseYn useYn;
	private String manualKoAttachUrl;
	private String manualEnAttachUrl;

}