package com.doopp.gauss.server.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableCaching

@Import({
    EhcacheConfiguration.class,
    RedisConfiguration.class,
    MyBatisConfiguration.class,
    TaskExecutorConfiguration.class
})

@ComponentScan(basePackages={"com.doopp.gauss"}, excludeFilters={
    @ComponentScan.Filter(type= FilterType.ANNOTATION, value=EnableWebMvc.class)
})

public class ApplicationConfiguration {
}
