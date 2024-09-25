package co.kr.compig.api.domain.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import co.kr.compig.api.domain.permission.MenuPermission;
import co.kr.compig.api.presentation.menu.request.MenuUpdateRequest;
import co.kr.compig.api.presentation.menu.response.MenuDetailResponse;
import co.kr.compig.global.code.MenuDivCode;
import co.kr.compig.global.code.MenuTypeCode;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.converter.MenuDivCodeConverter;
import co.kr.compig.global.code.converter.MenuTypeCodeConverter;
import co.kr.compig.global.embedded.CreatedAndUpdated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Menu {

	@Id
	@Column(name = "menu_id")
	private Long id; // 메뉴코드

	@Column(nullable = false, length = 10)
	@Convert(converter = MenuDivCodeConverter.class)
	private MenuDivCode menuDiv; // 메뉴 구분

	@Column(nullable = false, length = 100)
	private String menuNm; // 메뉴명

	@Column(length = 150)
	private String menuUrl; // 메뉴 URL

	@Column(length = 3)
	private Integer seq; // 메뉴 순서

	@Column(nullable = false, length = 6)
	@Convert(converter = MenuTypeCodeConverter.class)
	private MenuTypeCode menuType; // 메뉴 타입

	@Column(length = 1)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private UseYn useYn = UseYn.Y; // 사용유무

	/* =================================================================
	 * Domain mapping
	 ================================================================= */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk01_menu"))
	private Menu parent;

	@Builder.Default
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Menu> child = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "menu", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<MenuPermission> roles = new HashSet<>();

	/* =================================================================
	 * Relation method
	   ================================================================= */
	public MenuDetailResponse toMenuDetailResponse() {
		return MenuDetailResponse.builder()
			.menuId(this.id)
			.menuDiv(this.menuDiv)
			.menuNm(this.menuNm)
			.menuUrl(this.menuUrl)
			.seq(this.seq)
			.menuType(this.menuType)
			.useYn(this.useYn)
			.child(this.child.stream()
				.map(Menu::toMenuDetailResponse)
				.collect(Collectors.toList())) // Convert child Menu list
			.build();
	}
	/* =================================================================
	 * Default columns
	   ================================================================= */
	@Embedded
	@Builder.Default
	private CreatedAndUpdated createdAndModified = new CreatedAndUpdated();

	public void update(MenuUpdateRequest menuUpdateRequest, Menu parent) {
		this.menuDiv = menuUpdateRequest.getMenuDiv();
		this.menuNm = menuUpdateRequest.getMenuNm();
		this.menuUrl = menuUpdateRequest.getMenuUrl();
		this.seq = menuUpdateRequest.getSeq();
		this.menuType = menuUpdateRequest.getMenuType();
		this.useYn = menuUpdateRequest.getUseYn();
		this.parent = parent;
	}

  /* =================================================================
   * Business
     ================================================================= */

	public void setParent(final Menu parent) {
		this.parent = parent;
	}
}
