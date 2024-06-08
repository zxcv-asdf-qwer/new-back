package co.kr.compig.api.domain.permission;

import com.fasterxml.jackson.annotation.JsonBackReference;

import co.kr.compig.api.domain.member.Member;
import co.kr.compig.api.domain.menu.Menu;
import co.kr.compig.global.embedded.CreatedAndUpdated;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@SequenceGenerator(
	name = "menu_permission_seq_gen", //시퀀스 제너레이터 이름
	sequenceName = "menu_permission_seq", //시퀀스 이름
	initialValue = 1, //시작값
	allocationSize = 1 //메모리를 통해 할당 할 범위 사이즈
)
public class MenuPermission {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_permission_seq_gen")
	@Column(name = "menu_permission_id")
	private Long id; // MenuPermission id

	@Column(length = 50)
	private String groupKey; // Group key

  /* =================================================================
   * Domain mapping
   ================================================================= */

	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk01_menu_permission"))
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference//연관관계의 주인 Entity 에 선언, 직렬화가 되지 않도록 수행
	private Member member; // Member id

	@JoinColumn(name = "menu_id", foreignKey = @ForeignKey(name = "fk02_menu_permission"))
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference//연관관계의 주인 Entity 에 선언, 직렬화가 되지 않도록 수행
	private Menu menu; // Member

  /* =================================================================
   * Relation method
   ================================================================= */

	public void setMember(Member member) {
		this.member = member;
	}

	/* =================================================================
	 * Default columns
	 ================================================================= */
	@Embedded
	@Builder.Default
	private CreatedAndUpdated createdAndModified = new CreatedAndUpdated();

}
