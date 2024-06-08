package co.kr.compig.api.domain.member;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import co.kr.compig.api.domain.permission.MenuPermission;
import co.kr.compig.global.code.DeptCode;
import co.kr.compig.global.code.GenderCode;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserType;
import co.kr.compig.global.code.converter.DeptCodeConverter;
import co.kr.compig.global.code.converter.UserTypeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
	uniqueConstraints = {
		@UniqueConstraint(name = "uk01_member", columnNames = {"userId"}),
	})
public class Member {

	@Id
	@Column(name = "member_id")
	private String id; // Keycloak 의 id

	@Column(length = 150)
	private String userId; // 사용자 아이디

	@Column(length = 150)
	private String userPw; // 사용자 비밀번호

	@Column(length = 100)
	private String userNm; // 사용자 명

	@Column(length = 150)
	private String email; // 이메일

	@Column(length = 100)
	private String telNo; // 전화번호

	@Column
	@Enumerated(EnumType.STRING)
	private GenderCode gender; // 성별

	@Column(length = 1)
	@ColumnDefault("'Y'")
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private UseYn useYn = UseYn.Y; // 사용유무

	@Column(length = 10)
	@Convert(converter = UserTypeConverter.class)
	private UserType userType; // 사용자 구분

	@Column(length = 10)
	@Convert(converter = DeptCodeConverter.class)
	private DeptCode deptCode; // 부서 구분

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	private MemberRegisterType memberRegisterType;  // 회원가입 유형

	@Column(length = 200)
	private String address1; // 주소1

	@Column(length = 200)
	private String address2; // 주소2

	@Column(columnDefinition = "TEXT")
	private String introduce; //자기소개

	@Column
	private LocalDate marketingAppPushDate; // 앱 푸시알림 수신동의 날짜

	@Column
	private String leaveReason; //탈퇴 사유

	@Column
	private LocalDate leaveDate; // 회원 탈퇴 날짜

	/* =================================================================
	 * Domain mapping
	   ================================================================= */
	@Builder.Default
	@OneToMany(
		mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference //연관관계 주인 반대 Entity 에 선언, 정상적으로 직렬화 수행
	private Set<MemberGroup> groups = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference //연관관계 주인 반대 Entity 에 선언, 정상적으로 직렬화 수행
	private Set<MenuPermission> menuPermissions = new HashSet<>();

	/* =================================================================
	 * Relation method
	   ================================================================= */

}
