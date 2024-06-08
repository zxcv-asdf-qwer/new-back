package co.kr.compig.global.notify.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import co.kr.compig.global.notify.Attachment;
import co.kr.compig.global.notify.Field;
import co.kr.compig.global.notify.NotifyMessage;
import co.kr.compig.global.notify.Payload;
import co.kr.compig.global.security.CustomOauth2User;
import co.kr.compig.global.utils.ApplicationContextUtil;
import co.kr.compig.global.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

/** Slack에서 channel 생성 필요 */
@Slf4j
public class SlackMessage implements NotifyMessage {

	private final String channelName;
	private final String slackWebHookUrl;
	private final RestTemplate restTemplate;
	private final HttpHeaders headers;

	public SlackMessage(String channelName, String slackWebHookUrl, String token) {
		this.channelName = channelName;
		this.slackWebHookUrl = slackWebHookUrl;
		restTemplate = new RestTemplate();
		headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
	}

	/** 메시지를 특정 채널로 전송 */
	@Override
	public void sendMessage(String channelName, String message) {
		List<Field> fields = new ArrayList<>();
		fields.add(getField("Message", message));

		setCustomOauth2User(fields);
		post(
			Payload.builder()
				.channel(channelName)
				.attachments(List.of(Attachment.builder().fields(fields).build()))
				.build());
	}

	/**
	 * 오류 내용을 슬랙으로 전송
	 *
	 * @param e 오류
	 */
	@Override
	public void sendErrorMessage(Throwable e) {
		sendErrorMessage(e, null);
	}

	/**
	 * 오류 내용과 추가 데이터를 슬랙으로 전송
	 *
	 * @param throwable 오류
	 * @param additionalData 추가 데이터
	 */
	@Override
	public void sendErrorMessage(Throwable throwable, Object additionalData) {
		post(getPayload(throwable, additionalData));
	}

	private Payload getPayload(Throwable e, Object additionalData) {
		return Payload.builder()
			.channel(channelName)
			.attachments(List.of(getException(e, additionalData)))
			.build();
	}

	private Attachment getException(Throwable e, Object additionalData) {
		List<Field> fields = new ArrayList<>();
		fields.add(getField(e.toString(), e.getMessage()));
		setTraceData(fields, e);
		setAdditionalData(fields, additionalData);
		setCustomOauth2User(fields);
		setRequestData(fields);

		return Attachment.builder().fields(fields).build();
	}

	private Field getField(String title, String value) {
		return Field.builder().title(title).value(value).build();
	}

	private void setCustomOauth2User(List<Field> fields) {
		CustomOauth2User customOauth2User = SecurityUtil.getCustomOauth2User();
		if (customOauth2User != null) {
			fields.add(getField("TokenUser", customOauth2User.toString()));
		}
	}

	private void setTraceData(List<Field> fields, Throwable e) {
		fields.add(
			getField(
				"Trace",
				Arrays.stream(e.getStackTrace())
					.map(StackTraceElement::toString)
					.collect(Collectors.joining("\n"))));
	}

	private void setAdditionalData(List<Field> fields, Object additionalData) {
		if (additionalData != null) {
			fields.add(getField("Data", additionalData.toString()));
		}
	}

	private void setRequestData(List<Field> fields) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			String uri = ((ServletRequestAttributes)requestAttributes).getRequest().getRequestURI();

			if (StringUtils.isNotBlank(uri)) {
				fields.add(Field.builder().title("URL").value(uri).build());
			}
		}
	}

	private void post(Payload payload) {
		if (ApplicationContextUtil.getActiveProfile().contains("local")) {
			// local 프로필에서는 로그를 남기지 않음
			return;
		}
		if (StringUtils.isNotBlank(slackWebHookUrl)) {
			try {
				restTemplate.exchange(
					slackWebHookUrl, HttpMethod.POST, new HttpEntity<>(payload, headers), String.class);
			} catch (Exception e) {
				log.error("Slack Message Exception: {}", e.getMessage());
			}
		}
	}
}
