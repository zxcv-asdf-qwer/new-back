package co.kr.compig.global.utils;

import org.apache.commons.lang3.StringUtils;

public class PasswordValidation {

	public static boolean isUpdateUserPw(String newUserPw, String chkUserPw) {
		return StringUtils.isNotBlank(newUserPw)
			|| StringUtils.isNotBlank(chkUserPw);
	}

	public static boolean isUpdatableUserPw(String newUserPw, String chkUserPw) {
		return StringUtils.isNotBlank(newUserPw)
			&& StringUtils.isNotBlank(chkUserPw);
	}

	public static boolean isEqualsNewUserPw(String newUserPw, String chkUserPw) {
		return StringUtils.equals(newUserPw, chkUserPw);
	}
}
