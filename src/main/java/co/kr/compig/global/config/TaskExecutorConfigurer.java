package co.kr.compig.global.config;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

public class TaskExecutorConfigurer {
	protected TaskExecutor taskExecutor(int poolSize, int queueCapacity) {
		return taskExecutor(poolSize, poolSize, queueCapacity);
	}

	protected TaskExecutor taskExecutor(int corePoolSize, int maxPoolSize, int queueCapacity) {
		return taskExecutor(corePoolSize, maxPoolSize, queueCapacity, "AsyncTask-");
	}

	protected TaskExecutor taskExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String threadNamePrefix) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(threadNamePrefix);
		executor.initialize();

		return new DelegatingSecurityContextAsyncTaskExecutor(executor);
	}
}
