package co.kr.compig.api.presentation.social.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialLoginResponse {

	// private String email;
	// private List<String> roles;
	private String access_token;
	private String expires_in;
	private String refresh_expires_in;
	private String refresh_token;
	private String token_type;
	private String session_state;
	private String scope;

	// public void setEmail(String email) {
	// 	this.email = email;
	// }
	//
	// public void setRoles(List<String> roles) {
	// 	this.roles = roles;
	// }
}
