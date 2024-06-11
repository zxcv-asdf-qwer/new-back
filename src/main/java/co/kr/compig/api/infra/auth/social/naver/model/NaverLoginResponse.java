package co.kr.compig.api.infra.auth.social.naver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginResponse {

	// {"resultcode":"00","message":"success","response":{"id":"asdfsadfsadfsad","email":"asdfasdf@asdfasdf.com"}}
	private String resultcode;
	private String message;
	private Response response;

	@Getter
	public static class Response {

		private String id;
		private String email;
		private String name;
	}

}
