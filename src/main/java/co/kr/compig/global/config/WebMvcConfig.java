package co.kr.compig.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.kr.compig.global.code.IsYn;
import lombok.RequiredArgsConstructor;

@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final ObjectMapper objectMapper;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new StringHttpMessageConverter());
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		// controller에 @ModelAttribute 사용시 enum 컨버터 적용
		registry.addConverter(String.class, IsYn.class, source -> Arrays.stream(IsYn.values())
			.filter(f -> f.name().equalsIgnoreCase(source))
			.findFirst().orElse(null));
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/*/**")
			.allowedOriginPatterns("*")
			.allowedMethods("*")
			.allowedHeaders("*")
			.allowCredentials(true)
			.maxAge(3600L);
	}

}
