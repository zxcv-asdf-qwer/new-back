package co.kr.compig.global.keycloak;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import co.kr.compig.api.domain.member.MemberGroup;
import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.error.exception.KeyCloakRequestException;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Keycloak ADMIN API Handler
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakHandler {

	private final KeycloakProperties keycloakProperties;
	@Getter
	private final PasswordEncoder passwordEncoder;

	private Keycloak keycloak;

	@PostConstruct
	public void initialize() {
		log.info("### Initializing Keycloak");
		keycloak = KeycloakBuilder.builder()
			.serverUrl(keycloakProperties.getServerUrl())
			.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
			.realm(keycloakProperties.getRealm())
			.clientId(keycloakProperties.getClientId())
			.clientSecret(keycloakProperties.getClientSecret())
			.resteasyClient(
				((ResteasyClientBuilder)ClientBuilder.newBuilder()).connectionPoolSize(
					keycloakProperties.getPoolSize()).build()
			)
			.build();

		//    keycloak.tokenManager().getAccessToken();
		KeycloakHolder.set(this);

	}

	public RealmResource getRealmResource() {
		return this.getRealmResource(this.keycloak);
	}

	private RealmResource getRealmResource(Keycloak keycloak) {
		return keycloak.realm(keycloakProperties.getRealm());
	}

	public UsersResource getUsers() {
		log.debug("### Get users");
		return getRealmResource().users();
	}

	public GroupsResource getGroups() {
		log.debug("### Get groups");
		return getRealmResource().groups();
	}

	public Optional<UserRepresentation> getUser(String username) {
		log.debug("### Get user - {}", username);
		List<UserRepresentation> userRepresentationList = getRealmResource().users().search(username);
		if (userRepresentationList.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(userRepresentationList.get(0));
	}

	/**
	 * Keycloak 사용자 생성
	 */
	public UserRepresentation createUser(UserRepresentation userRepresentation)
		throws KeyCloakRequestException {
		Response response = getUsers().create(userRepresentation);
		int status = response.getStatus();
		if (status != HttpStatus.CREATED.value()) {
			if (status == HttpStatus.CONFLICT.value()) {
				//키클락 계정 충돌시
				throw new BizException("이미 가입한 이메일 입니다. 다른 이메일 주소를 사용해주세요.");
			}

			String reasonPhrase = ((ClientResponse)response).getReasonPhrase();
			log.error("Http status : {}, reason : {}", status, reasonPhrase);
			throw new KeyCloakRequestException("인증서버 등록중 에러가 발생 하였습니다.["
				+ status + " - " + reasonPhrase + "]"
			);
		}
		return getUser(userRepresentation.getUsername()).orElseThrow(KeyCloakRequestException::new);
	}

	public void usersJoinGroups(String id, Set<MemberGroup> groups) {
		UserResource userResource = getUsers().get(id);
		groups.forEach(group -> {
			try {
				userResource.joinGroup(group.getGroupKey());
			} catch (NotFoundException e) {
				throw new BizException("그룹이 없습니다.");
			}
		});
		List<GroupRepresentation> groupRepresentations = userResource.groups();
		for (MemberGroup memberGroup : groups) {
			Optional<GroupRepresentation> optionalGroupRepresentation = groupRepresentations.stream()
				.filter(
					groupRepresentation -> groupRepresentation.getId().equals(memberGroup.getGroupKey()))
				.findFirst();

			if (optionalGroupRepresentation.isEmpty()) {
				throw new KeyCloakRequestException("Not found Group : " + memberGroup.getGroupKey());
			}

			GroupRepresentation groupRepresentation = optionalGroupRepresentation.get();
			String groupId = groupRepresentation.getId();
			String groupNm = groupRepresentation.getName();
			String groupPath = groupRepresentation.getPath();
			memberGroup.updateGroupInfo(groupId, groupNm, groupPath);
		}
	}

	/**
	 * Keyclaok 사용자 삭제
	 */
	public void deleteUser(String id) {
		Response response = getUsers().delete(id);
		int status = response.getStatus();
		//204
		if (status != HttpStatus.NO_CONTENT.value()) {
			//404
			if (status == HttpStatus.NOT_FOUND.value()) {
				throw new BizException("이미 탈퇴한 회원입니다.");
			}
			String reasonPhrase = ((ClientResponse)response).getReasonPhrase();
			log.error("Http status : {}, reason : {}", status, reasonPhrase);
			throw new KeyCloakRequestException("인증서버 탈퇴중 에러가 발생 하였습니다.["
				+ status + " - " + reasonPhrase + "]"
			);
		}
	}

	/**
	 * Keycloak 사용자 갱신
	 */
	public UserRepresentation updateUser(UserRepresentation userRepresentation) throws KeyCloakRequestException {
		UserResource userResource = getUsers().get(userRepresentation.getId());

		// 기존 그룹 삭제
		userResource.groups().forEach(group -> userResource.leaveGroup(group.getId()));

		//        userRepresentation.setCredentials(null);

		// Users Details update
		userResource.update(userRepresentation);
		return getUser(userRepresentation.getUsername())
			.orElseThrow(KeyCloakRequestException::new);
	}
}
