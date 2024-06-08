package co.kr.compig.global.aspect;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogDataConverter {
	public static String dataConverter(Object data) {
		try {
			String converterData;
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			converterData = objectMapper.writeValueAsString(data);
			if (converterData.getBytes(StandardCharsets.UTF_8).length / 1024 > 512) {
				return "Message Too Large.";
			}
			return converterData;
		} catch (Exception e) {
			log.error("LoggingAspect dataConverter Exception: {}", e.getMessage(), e);
			return "Can not dataConverter data.";
		}
	}

	public static String errorDataConverter(String errorData) {
		try {
			String converterData = errorData;
			if (converterData.getBytes(StandardCharsets.UTF_8).length / 1024 > 512) {
				int maxLength = (512 - 3) * 1024;
				converterData = converterData.substring(0, maxLength) + "...";
			}
			return converterData;
		} catch (Exception e) {
			log.error("LoggingAspect errorDataConverter Exception: {}", e.getMessage(), e);
			return "Can not errorDataConverter data.";
		}
	}

	public static Map<String, Object> requestData(
		ProceedingJoinPoint pjp,
		ContentCachingRequestWrapper request) {
		Map<String, Object> data = new HashMap<>();
		try {
			final MethodSignature methodSignature = (MethodSignature)pjp.getSignature();
			final Object[] args = pjp.getArgs();
			final String[] parameterNames = methodSignature.getParameterNames();
			final String contentType = request.getContentType();
			if (StringUtils.isNotBlank(contentType)) {
				if (contentType.contains("application/json")) {
					for (int i = 0; i < parameterNames.length; i++) {
						if (!(args[i] instanceof Errors)) {
							data.put(parameterNames[i], args[i]);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("LoggingAspect RequestData Exception: {}", e.getMessage(), e);
		}
		return data;
	}

	public static String getUrlParameter(ContentCachingRequestWrapper request) {
		String urlParameter = null;
		urlParameter = Optional.ofNullable(request.getQueryString()).isPresent() ?
			URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8) : null;
		return urlParameter;
	}
}
