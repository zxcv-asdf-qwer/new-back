package co.kr.compig.global.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

public class CustomOauth2UserAuthenticatedToken extends
	AbstractOAuth2TokenAuthenticationToken<Jwt> {

	private final CustomOauth2User customOauth2User;

	public CustomOauth2UserAuthenticatedToken(Jwt jwt,
		Collection<? extends GrantedAuthority> authorities, CustomOauth2User customOauth2User) {
		super(jwt, customOauth2User, customOauth2User, authorities);
		super.setAuthenticated(true);
		this.customOauth2User = customOauth2User;
	}

	@Override
	public CustomOauth2User getPrincipal() {
		return customOauth2User;
	}

	@Override
	public CustomOauth2User getCredentials() {
		return customOauth2User;
	}

	@Override
	public Map<String, Object> getTokenAttributes() {
		return this.getToken().getClaims();
	}
}
