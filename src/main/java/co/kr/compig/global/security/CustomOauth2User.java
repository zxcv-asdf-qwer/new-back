package co.kr.compig.global.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomOauth2User implements Serializable {

	private String id;
	private String userId;
	private String username;
	private String email;
	private boolean userYn;
}
