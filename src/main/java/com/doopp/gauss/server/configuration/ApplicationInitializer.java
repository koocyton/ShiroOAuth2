//package com.doopp.gauss.server.configuration;
//
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
//
//public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
//
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return new Class<?>[] { ApplicationConfiguration.class };
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        return new Class<?>[] { WebMvcConfigurer.class };
//    }
//
//    @Override
//    protected WebApplicationContext createServletApplicationContext() {
//        return super.createServletApplicationContext();
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[] { "/*" };
//    }
//
//}
