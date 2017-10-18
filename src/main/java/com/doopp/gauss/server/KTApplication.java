package com.doopp.gauss.server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

@ComponentScan(
        basePackages={"com.doopp.gauss"},
        excludeFilters={
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes={
                        org.springframework.stereotype.Controller.class,
                        org.springframework.web.bind.annotation.ControllerAdvice.class
                })
        })

public class KTApplication {

    public static void main(String[] args) {

        // init applicationContext
        final AbstractApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:config/spring-undertow.xml");

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();
    }
}
