package com.doopp.gauss.server.configuration;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
// @EnableCaching

@Import({
        CommonConfiguration.class,
        MyBatisConfiguration.class,
        RedisConfiguration.class
})

@ComponentScan(basePackages = {"com.doopp.gauss"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)
})

public class ApplicationConfiguration {

}
