package com.doopp.gauss.server.configuration;

import com.doopp.gauss.common.utils.ApplicationContextUtil;
import com.doopp.gauss.common.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.Properties;

public class CommonUtil {

    @Bean
    public Properties applicationProperties() {
        ClassPathResource cpr = new ClassPathResource("config/application.properties");
        Properties pros = new Properties();
        try {
            pros.load(cpr.getInputStream());
        }
        catch (IOException ex) {
            System.out.println("config/application.properties is not exist");
        }
        return pros;
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(1, 1);
    }

    @Bean
    public ThreadPoolTaskExecutor gameTaskExecutor () {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(1000);
        threadPoolTaskExecutor.setQueueCapacity(25);
        threadPoolTaskExecutor.setKeepAliveSeconds(30000);
        return threadPoolTaskExecutor;
    }
}
