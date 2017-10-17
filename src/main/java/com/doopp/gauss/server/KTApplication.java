package com.doopp.gauss.server;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class KTApplication {

    public static void main(String[] args) {

        // init applicationContext
        final AbstractApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:config/spring-undertow.xml");

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();
    }
}
