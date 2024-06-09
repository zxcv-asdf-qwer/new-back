package co.kr.compig.api.presentation.social.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialAuthResponse {
    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;
    private String refresh_token;
    private String refresh_token_expires_in;

}
