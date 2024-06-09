package co.kr.compig.api.infra.auth.social.google.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginResponse {

	private String sub;
	private String name;
	private String email;
	private String picture;
}
