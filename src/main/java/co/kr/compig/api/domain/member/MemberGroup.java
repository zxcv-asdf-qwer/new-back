package co.kr.compig.api.domain.member;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
	uniqueConstraints = {
		@UniqueConstraint(name = "uk01_member_group", columnNames = {"groupKey", "member_id"})
	}
)
@SequenceGenerator(
	name = "member_group_seq_gen", //시퀀스 제너레이터 이름
	sequenceName = "member_group_seq", //시퀀스 이름
	initialValue = 1, //시작값
	allocationSize = 1 //메모리를 통해 할당 할 범위 사이즈
)
public class MemberGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_group_seq_gen")
	@Column(name = "member_group_id")
	private Long id;

	@Column(length = 50, nullable = false)
	private String groupKey;

	@Column(length = 150, nullable = false)
	private String groupNm;

	@Column(length = 250, nullable = false)
	private String groupPath;

	/* =================================================================
	 * Domain mapping
	   ================================================================= */
	@Builder.Default
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk01_member_group"))
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference//연관관계의 주인 Entity 에 선언, 직렬화가 되지 않도록 수행
	private Member member = new Member();

	/* =================================================================
	 * Relation method
	   ================================================================= */
	public void setMember(Member member) {
		this.member = member;
	}

	/* =================================================================
	 * Business
	   ================================================================= */

	public void updateGroupInfo(String groupKey, String groupNm, String groupPath) {
		this.groupKey = groupKey;
		this.groupNm = groupNm;
		this.groupPath = groupPath;
	}

}
