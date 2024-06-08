package co.kr.compig.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@Profile("!test")
@Configuration
@EnableAsync
public class SpringAsyncConfig extends TaskExecutorConfigurer {
	@Bean
	public TaskExecutor taskExecutor() {
		return taskExecutor(30, 1000);
	}
}
