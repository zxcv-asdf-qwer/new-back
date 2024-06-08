package co.kr.compig.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.kr.compig.global.config.jackson.CustomObjectMapper;
import co.kr.compig.global.error.handler.CommonExceptionHandler;
import co.kr.compig.global.notify.NotifyMessage;
import co.kr.compig.global.notify.impl.SlackMessage;
import co.kr.compig.global.utils.ApplicationContextUtil;

@Import(value = {CommonExceptionHandler.class, ApplicationContextUtil.class})
@Configuration
public class CommonConfig {

	@Value("${slack.channel.error}")
	private String slackChannel;

	@Value("${slack.url}")
	private String slackUrl;

	@Value("${slack.token}")
	private String slackToken;

	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(true);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludeHeaders(true);
		loggingFilter.setIncludePayload(true);

		return loggingFilter;
	}

	@Bean
	@Primary
	public ObjectMapper commonObjectMapper() {
		return CustomObjectMapper.getObjectMapper();
	}

	@Bean
	public NotifyMessage notifyMessage() {
		return new SlackMessage(slackChannel, slackUrl, slackToken);
	}
}
