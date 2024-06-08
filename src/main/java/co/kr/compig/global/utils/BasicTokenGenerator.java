package co.kr.compig.global.utils;

import java.util.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicTokenGenerator {
	public static String generateBasicToken(String username, String password) {
		String credentials = username + ":" + password;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		return "Basic " + encodedCredentials;
	}
}
