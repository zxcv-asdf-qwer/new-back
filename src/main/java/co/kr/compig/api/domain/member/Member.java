package co.kr.compig.api.domain.member;

import static co.kr.compig.global.utils.KeyGen.*;
import static co.kr.compig.global.utils.PasswordValidation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import co.kr.compig.api.domain.permission.MenuPermission;
import co.kr.compig.api.presentation.member.request.AdminMemberUpdate;
import co.kr.compig.api.presentation.member.response.AdminMemberResponse;
import co.kr.compig.api.presentation.member.response.MemberResponse;
import co.kr.compig.global.code.DeptCode;
import co.kr.compig.global.code.GenderCode;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserType;
import co.kr.compig.global.code.converter.DeptCodeConverter;
import co.kr.compig.global.code.converter.UserTypeConverter;
import co.kr.compig.global.embedded.CreatedAndUpdated;
import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.error.exception.KeyCloakRequestException;
import co.kr.compig.global.keycloak.KeycloakHandler;
import co.kr.compig.global.keycloak.KeycloakHolder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
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

	public void addGroups(final MemberGroup group) {
		this.groups.add(group);
		group.setMember(this);
	}

	private void removeAllGroups(Set<MemberGroup> MemberGroups) {
		if (MemberGroups == null) {
			throw new BizException("group key 가 없습니다.");
		}
		this.groups.removeAll(MemberGroups);
	}

	/* =================================================================
	 * Default columns
	   ================================================================= */
	@Embedded
	@Builder.Default
	private CreatedAndUpdated createdAndModified = new CreatedAndUpdated();

  /* =================================================================
   * Business
     ================================================================= */

	private boolean isExistGroups() {
		return CollectionUtils.isNotEmpty(this.groups);
	}

	/**
	 * Keycloak 사용자 생성
	 */
	public void createUserKeyCloak(String providerId, String providerUsername)
		throws KeyCloakRequestException {
		KeycloakHandler keycloakHandler = KeycloakHolder.get();
		UserRepresentation userRepresentation =
			keycloakHandler.createUser(this.getUserRepresentation(providerId, providerUsername));
		this.id = userRepresentation.getId();
		if (isExistGroups()) {
			keycloakHandler.usersJoinGroups(this.id, this.getGroups());
		}
	}

	/**
	 * Keycloak UserRepresentation
	 */
	public UserRepresentation getUserRepresentation(String providerId, String providerUsername) {
		UserRepresentation userRepresentation = new UserRepresentation();
		String userNm = this.userNm;
		if (userNm != null) {
			String[] userNmSplit = userNm.split(" ");
			String firstName = userNmSplit[0];
			String lastName = userNmSplit.length > 1 ? userNmSplit[1] : userNmSplit[0];
			userRepresentation.setFirstName(firstName);
			userRepresentation.setLastName(lastName);
		}

		userRepresentation.setId(this.id);
		userRepresentation.setUsername(Optional.ofNullable(this.userId).orElseGet(() -> this.email));
		userRepresentation.setEmail(this.email);
		userRepresentation.setEnabled(true);

		if (!MemberRegisterType.GENERAL.equals(this.memberRegisterType) && StringUtils.isNotBlank(
			providerUsername)) {
			String socialProvider = this.memberRegisterType.getCode().toLowerCase();

			FederatedIdentityRepresentation federatedIdentityRepresentation = new FederatedIdentityRepresentation();
			federatedIdentityRepresentation.setUserId(providerId);
			federatedIdentityRepresentation.setUserName(providerUsername);
			federatedIdentityRepresentation.setIdentityProvider(socialProvider);
			userRepresentation.setFederatedIdentities(List.of(federatedIdentityRepresentation));
			userRepresentation.setEmailVerified(true);
		}

		if (!isPasswordEncoded()) {
			CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
			credentialRepresentation.setType("password");
			credentialRepresentation.setValue(this.userPw);
			userRepresentation.setCredentials(List.of(credentialRepresentation));
		}

		return userRepresentation;
	}

	public void updateUserKeyCloak() {
		KeycloakHandler keycloakHandler = KeycloakHolder.get();
		keycloakHandler.updateUser(this.getUserRepresentation(null, null));
		if (isExistGroups()) {
			keycloakHandler.usersJoinGroups(this.id, this.getGroups());
		}
	}

	public boolean isPasswordEncoded() {
		return StringUtils.defaultString(this.userPw).startsWith("{bcrypt}");
	}

	public void passwordEncode() {
		if (!isPasswordEncoded()) {
			this.userPw = KeycloakHolder.get().getPasswordEncoder().encode(this.userPw);
		}
	}

	public MemberResponse toMemberResponse() {
		MemberResponse memberResponse = MemberResponse.builder()
			.memberId(this.id)
			.userId(this.userId)
			.userNm(this.userNm)
			.telNo(this.telNo)
			.email(this.email)
			.gender(this.gender)
			.useYn(this.useYn)
			.userType(this.userType)
			.deptCode(this.deptCode)
			.memberRegisterType(this.memberRegisterType)
			.address1(this.address1)
			.address2(this.address2)
			.introduce(this.introduce)
			.marketingAppPush(this.marketingAppPushDate != null)
			.build();

		memberResponse.setGroups(
			this.groups.stream().map(MemberGroup::converterDto).collect(Collectors.toSet()));
		memberResponse.setCreatedAndUpdated(this.createdAndModified);
		return memberResponse;
	}

	public AdminMemberResponse toAdminMemberResponse() {
		AdminMemberResponse memberResponse = AdminMemberResponse.builder()
			.memberId(this.id)
			.userId(this.userId)
			.userNm(this.userNm)
			.telNo(this.telNo)
			.email(this.email)
			.deptCode(this.deptCode)
			.build();

		memberResponse.setGroups(
			this.groups.stream().map(MemberGroup::converterDto).collect(Collectors.toSet()));
		memberResponse.setCreatedAndUpdated(this.createdAndModified);
		return memberResponse;
	}

	public void updateAdminMember(AdminMemberUpdate adminMemberUpdate) {
		if (isUpdateUserPw(adminMemberUpdate.getNewUserPw(), adminMemberUpdate.getChkUserPw())) { // 비밀번호 변경의사 있음
			if (!isUpdatableUserPw(adminMemberUpdate.getNewUserPw(),
				adminMemberUpdate.getChkUserPw())) { // 모든 비밀번호 영역 값 입력 확인
				throw new BizException("모든 비밀번호를 입력해주세요.");
			}
			if (!isEqualsNewUserPw(adminMemberUpdate.getNewUserPw(),
				adminMemberUpdate.getChkUserPw())) { // 새 비밀번호와 확인의 동일 확인
				throw new BizException("새 비밀번호와 비밀번호 확인의 내용이 다릅니다.");
			}
			this.userPw = adminMemberUpdate.getNewUserPw();
		}
		if (StringUtils.isNotEmpty(adminMemberUpdate.getUserNm())) {
			this.userNm = adminMemberUpdate.getUserNm();
		}
		if (StringUtils.isNotEmpty(adminMemberUpdate.getEmail())) {
			this.email = adminMemberUpdate.getEmail();
		}
		if (StringUtils.isNotEmpty(adminMemberUpdate.getTelNo())) {
			this.telNo = adminMemberUpdate.getTelNo();
		}
		if (adminMemberUpdate.getDeptCode() != null) {
			this.userType =
				adminMemberUpdate.getDeptCode().equals(DeptCode.DEVELOPER) ? UserType.SYS_ADMIN : UserType.SYS_USER;
			this.deptCode = adminMemberUpdate.getDeptCode();
		}

		this.removeAllGroups(
			this.groups.stream()
				.filter(
					memberGroup ->
						adminMemberUpdate.getGroupKeys().stream()
							.filter(memberGroup::equalsGroupKey)
							.findAny()
							.isEmpty())
				.collect(Collectors.toSet()));

		for (String groupKey : adminMemberUpdate.getGroupKeys()) {
			Optional<MemberGroup> optional =
				this.groups.stream().filter(g -> g.getGroupKey().equals(groupKey)).findFirst();

			if (optional.isEmpty()) {
				this.addGroups(MemberGroup.builder().groupKey(groupKey).build());
			}
		}
	}

	public void setLeaveMember(String leaveReason) {
		String del = getRandomKey("del");
		this.userId = del.concat(this.userId);
		this.email = this.email != null ? del.concat(this.email) : null;
		if (StringUtils.isNotEmpty(leaveReason)) {
			this.leaveReason = leaveReason;
		}
		this.leaveDate = LocalDate.now();
		this.useYn = UseYn.N;
	}
}
