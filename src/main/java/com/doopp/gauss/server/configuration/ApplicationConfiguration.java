package com.doopp.gauss.server.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@EnableCaching
@EnableWebMvc
@EnableWebSocket
//@ComponentScan(basePackages={"com.doopp.gauss"}, excludeFilters={
//    @ComponentScan.Filter(type = FilterType.ANNOTATION, classes={
//        org.springframework.stereotype.Controller.class,
//        org.springframework.web.bind.annotation.ControllerAdvice.class
//    })
//})
@Import({
    EhcacheConfiguration.class,
    RedisConfiguration.class,
    TaskExecutorConfiguration.class,
    WebSocketConfiguration.class,
    MyBatisConfiguration.class
})
public class ApplicationConfiguration {
}
