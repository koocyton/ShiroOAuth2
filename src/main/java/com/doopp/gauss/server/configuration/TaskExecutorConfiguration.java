package com.doopp.gauss.server.configuration;


import com.doopp.gauss.server.task.GameTaskDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class TaskExecutorConfiguration {

    @Bean
    public GameTaskDispatcher gameTaskDispatcher(ThreadPoolTaskExecutor taskExecutor) {
        return new GameTaskDispatcher(taskExecutor);
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor () {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(25);
        return threadPoolTaskExecutor;
    }
}
