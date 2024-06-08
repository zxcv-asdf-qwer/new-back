package co.kr.compig.global.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Oauth2RestTemplateConfigurer {

	@Autowired
	private ObjectMapper objectMapper;

	protected RestTemplate restTemplate(int connectTimeout, int readTimeOut) {
		return restTemplate(connectTimeout, readTimeOut, null);
	}

	protected RestTemplate restTemplate(int connectTimeout, int readTimeOut, String rootUrl) {
		return restTemplate(connectTimeout, readTimeOut, rootUrl, null);
	}

	protected RestTemplate restTemplate(int connectTimeout, int readTimeOut, String rootUrl,
		ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		RestTemplate restTemplate = restTemplate(connectTimeout, readTimeOut, rootUrl,
			clientHttpRequestInterceptor, false);

		return restTemplate;
	}

	protected RestTemplate oauthRestTemplate(int connectTimeout, int readTimeOut, String rootUrl,
		ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
		return restTemplate(connectTimeout, readTimeOut, rootUrl, clientHttpRequestInterceptor, true);
	}

	private RestTemplate restTemplate(int connectTimeout, int readTimeOut, String rootUrl,
		ClientHttpRequestInterceptor clientHttpRequestInterceptor, boolean oauth) {

		RestTemplateBuilder builder = new RestTemplateBuilder()
			.requestFactory((settings) -> new BufferingClientHttpRequestFactory(
				ClientHttpRequestFactories.get(HttpComponentsClientHttpRequestFactory.class, settings)))
			.setConnectTimeout(Duration.ofSeconds(connectTimeout))
			.setReadTimeout(Duration.ofSeconds(readTimeOut));

		RestTemplate restTemplate = builder.build();

		if (StringUtils.isNotEmpty(rootUrl)) {
			restTemplate.setUriTemplateHandler(new RootUriTemplateHandler(rootUrl));
		}

		if (clientHttpRequestInterceptor != null) {
			restTemplate.getInterceptors().add(clientHttpRequestInterceptor);
		}

		if (oauth) {
			setMessageConverter(restTemplate);
		}

		return restTemplate;
	}

	private void setMessageConverter(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));
		messageConverters.addAll(restTemplate.getMessageConverters());

		restTemplate.setMessageConverters(messageConverters);
	}
}
