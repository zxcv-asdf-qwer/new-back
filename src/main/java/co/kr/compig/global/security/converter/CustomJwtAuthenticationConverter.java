package co.kr.compig.global.security.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.CollectionUtils;

import co.kr.compig.global.error.exception.NotExistDataException;
import co.kr.compig.global.security.CustomOauth2User;
import co.kr.compig.global.security.CustomOauth2UserAuthenticatedToken;

public class CustomJwtAuthenticationConverter implements
	Converter<Jwt, AbstractAuthenticationToken> {

	private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	private final String resourceClientId;

	public CustomJwtAuthenticationConverter(String resourceClientId) {
		this.resourceClientId = resourceClientId;
	}

	@Override
	public AbstractAuthenticationToken convert(final Jwt source) {

		Collection<GrantedAuthority> authorities = Stream.of(
			// Jwt 에서 scope 영역에 있는 데이터 SCOPE_XXX 형태로 가져옴
			defaultGrantedAuthoritiesConverter.convert(source),

			// Jwt 에서 realm_access 영역에 있는 데이터 ROLE_XXX 형태로 가져옴
			extractRealmRoles(source)

			// Jwt 에서 resource_access 영역에 있는 데이터 ROLE_XXX 형태로 가져옴
			//extractResourceRoles(source)
		).flatMap(Collection::stream).collect(Collectors.toSet());

		return new CustomOauth2UserAuthenticatedToken(source, authorities, getCustomOauth2User(source));
	}

	private CustomOauth2User getCustomOauth2User(Jwt jwt) {
		CustomOauth2User user = CustomOauth2User.builder().id(jwt.getSubject()).build();

		if (jwt.getClaims().containsValue("clientId")) {
			user.setUserId(jwt.getClaim("clientId"));
			user.setUserYn(false);
		} else {
			user.setId(jwt.getSubject());
			user.setUserId(jwt.getClaim("preferred_username"));
			user.setUsername(jwt.getClaim("name"));
			user.setEmail(jwt.getClaim("email"));
			user.setUserYn(true);
		}

		return user;
	}

	private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
		Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");

		if (CollectionUtils.isEmpty(realmAccess)) {
			throw new NotExistDataException("Invalid JWT token: Not found realm_access");
		}

		return getRoles(realmAccess);
	}

	private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
		Map<String, Map<String, List<String>>> resourceAccess = jwt.getClaim("resource_access");

		if (CollectionUtils.isEmpty(resourceAccess)) {
			throw new NotExistDataException("Invalid JWT token: Not found resource_access");
		}

		return getRoles(resourceAccess.get(resourceClientId));
	}

	private Collection<? extends GrantedAuthority> getRoles(Map<String, List<String>> claim) {
		if (claim != null) {
			return claim.getOrDefault("roles", Collections.emptyList()).stream()
				.map(roleName -> "ROLE_" + roleName)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
		}

		return Collections.emptySet();
	}
}
