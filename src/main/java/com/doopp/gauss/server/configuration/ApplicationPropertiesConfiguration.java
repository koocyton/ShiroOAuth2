package com.doopp.gauss.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

public class ApplicationPropertiesConfiguration {

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
}
