package co.kr.compig.global.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsUtils;

import co.kr.compig.global.code.UserType;
import co.kr.compig.global.security.CustomOidcUserService;
import co.kr.compig.global.security.HttpCookieOAuth2AuthorizationRequestRepository;
import co.kr.compig.global.security.converter.CustomJwtAuthenticationConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final OAuth2ClientProperties oAuth2ClientProperties;
	private final KeycloakLogoutHandler keycloakLogoutHandler;

	@Bean
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// http.csrf(withDefaults());
		http.csrf(AbstractHttpConfigurer::disable);
		http.authorizeHttpRequests(auth -> auth.requestMatchers(CorsUtils::isPreFlightRequest)
			.permitAll()
			.requestMatchers(antMatcher("/admin/**"))
			.hasAnyRole(UserType.SYS_USER.getCode(), UserType.SYS_ADMIN.getCode())
			.requestMatchers(
				antMatcher("/swagger-ui/**"),
				antMatcher("/v3/**"))
			.hasAnyRole(UserType.SYS_ADMIN.getCode())
			.requestMatchers(
				antMatcher("/pb/**"),
				antMatcher("/actuator/**"),
				antMatcher("/docs/**"),
				antMatcher("/favicon.ico")
			)
			.permitAll()
			.anyRequest()
			.authenticated());
		http.exceptionHandling(exceptionHandling -> exceptionHandling
			// .authenticationEntryPoint(customAuthenticationEntryPoint())
			.accessDeniedHandler(customAccessDeniedHandler()));
		http.oauth2Login((login) -> {
			login.authorizedClientRepository(
				authorizedClientRepository(authorizedClientService(clientRegistrationRepository())));
			login.authorizationEndpoint(
				authorizationEndpointConfig -> authorizationEndpointConfig.authorizationRequestRepository(
					authorizationRequestRepository()));
			login.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.oidcUserService(new CustomOidcUserService()));
		}); // 인증 요청을 쿠키에 저장하고 검색);

		http.oauth2ResourceServer((oauth2) -> oauth2.jwt(
			jwt -> jwt.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter("compig-back"))));
		http.logout(logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/"));
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		return http.build();
	}

	@Bean
	public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		List<ClientRegistration> registrations = new ArrayList<>(
			new OAuth2ClientPropertiesMapper(oAuth2ClientProperties).asClientRegistrations().values());
		return new InMemoryClientRegistrationRepository(registrations);
	}

	@Bean
	public OAuth2AuthorizedClientService authorizedClientService(
		ClientRegistrationRepository clientRegistrationRepository) {
		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
	}

	@Bean
	public OAuth2AuthorizedClientRepository authorizedClientRepository(
		OAuth2AuthorizedClientService authorizedClientService) {
		return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
	}

	private AuthenticationEntryPoint customAuthenticationEntryPoint() {
		return (request, response, accessDeniedException) -> {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().print("{" + "\"message\"" + ":" + "\"로그인을 먼저 진행해주세요\"" + "}");
		};
	}

	private AccessDeniedHandler customAccessDeniedHandler() {
		return (request, response, accessDeniedException) -> {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().print("{" + "\"message\"" + ":" + "\"접근 권한이 없습니다\"" + "}");
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	private Converter<Jwt, AbstractAuthenticationToken> customJwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
			new CustomJwtGrantedAuthoritiesConverter());
		return jwtAuthenticationConverter;
	}

	private final class CustomJwtGrantedAuthoritiesConverter implements
		Converter<Jwt, Collection<GrantedAuthority>> {

		@Override
		public Collection<GrantedAuthority> convert(Jwt jwt) {
			var realmAccess = (Map<String, List<String>>)jwt.getClaim("realm_access");
			if (realmAccess == null || realmAccess.isEmpty()) {
				return List.of(); // Return an empty list if the realm_access claim is not available
			}
			return realmAccess.get("roles").stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
		}
	}

	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		final OidcUserService delegate = new OidcUserService();

		return (userRequest) -> {
			// Delegate to the default implementation for loading a user
			OidcUser oidcUser = delegate.loadUser(userRequest);

			OAuth2AccessToken accessToken = userRequest.getAccessToken();
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

			// TODO
			// 1) Fetch the authority information from the protected resource using accessToken
			// 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities

			// 3) Create a copy of oidcUser but use the mappedAuthorities instead
			oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

			return oidcUser;
		};
	}

}
