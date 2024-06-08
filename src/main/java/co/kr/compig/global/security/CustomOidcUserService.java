package co.kr.compig.global.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOidcUserService extends OidcUserService {

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		OidcUser oidcUser = super.loadUser(userRequest);

		// 사용자 정보에서 "groups" 속성 가져오기
		List<String> groups = oidcUser.getAttribute("groups");

		Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

		// "groups"에 있는 각 항목을 GrantedAuthority 객체로 변환
		if (groups != null) {
			mappedAuthorities.addAll(groups.stream()
				.map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
				.collect(Collectors.toList()));
		}

		// CustomOidcUser를 사용하여 권한 설정
		return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
	}
}
