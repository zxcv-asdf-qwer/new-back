package co.kr.compig.global.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import co.kr.compig.global.code.AgentCode;
import jakarta.servlet.http.HttpServletRequest;

public class WebUtil {

	/**
	 * ip 가져오기
	 * 웹 어플리케이션을 개발 시 Client ip를 식별할 필요가 있다면 먼저 저 헤더가 있는지 확인한 후에 없으면 getRemoteAddr() 로 IP 를 얻으면 간단하지만 표준을 지키지 않는 것들이 있다.<br>
	 * WebServer, WAS, L4, proxy 종류에 상관없이 client IP 를 잘 가져오기를 바란다면 다음과 같은 순서로 IP 를 얻어내야 한다.<br>
	 */
	public static String getClientIp(HttpServletRequest request) {
		if (request == null) {
			ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
			request = attr.getRequest();
		}

		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static AgentCode getAgentCode(HttpServletRequest request) {
		if (request == null) {
			ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
			request = attr.getRequest();
		}

		String header = StringUtils.defaultIfBlank(request.getHeader("User-Agent"), "");
		String userAgent = header.toUpperCase();

		//mobile
		if (userAgent.indexOf("MOBI") > -1) {
			if (userAgent.indexOf("ANDROID") > -1) {
				return AgentCode.ANDROID;
			} else if (userAgent.indexOf("IPHONE") > -1 || userAgent.indexOf("IPAD") > -1
				|| userAgent.indexOf("IPOD") > -1) {
				return AgentCode.IOS;
			}
			return AgentCode.MOBILE;

		} else {
			if (userAgent.indexOf("TRIDENT") > -1) {                                                // IE
				return AgentCode.IE;
			} else if (userAgent.indexOf("EDGE") > -1 || userAgent.indexOf("EDG") > -1) {        // Edge
				return AgentCode.EDGE;
			} else if (userAgent.indexOf("WHALE") > -1) {                                        // Naver Whale
				return AgentCode.WHALE;
			} else if (userAgent.indexOf("OPERA") > -1 || userAgent.indexOf("OPR") > -1) {        // Opera
				return AgentCode.OPERA;
			} else if (userAgent.indexOf("FIREFOX") > -1) {                                         // Firefox
				return AgentCode.FIREFOX;
			} else if (userAgent.indexOf("SAFARI") > -1 && userAgent.indexOf("CHROME") == -1) {     // Safari
				return AgentCode.SAFARI;
			} else if (userAgent.indexOf("CHROME") > -1) {                                         // Chrome
				return AgentCode.CHROME;
			}
		}

		return null;
	}

}
