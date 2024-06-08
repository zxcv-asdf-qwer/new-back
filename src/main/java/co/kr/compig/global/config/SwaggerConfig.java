package co.kr.compig.global.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@OpenAPIDefinition(
	info = @Info(title = "Compig API",
		description = "Compig API 명세",
		version = "v1"))
@SecurityScheme(
	name = "Bearer Authentication",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	scheme = "bearer"
)
@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		List<Server> servers = new ArrayList<>();
		Server localServer = new Server();
		localServer.setUrl("http://localhost:8080");
		servers.add(localServer);

		return new OpenAPI()
			.servers(List.of(localServer));
	}

	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		return new InternalResourceViewResolver();
	}
}
