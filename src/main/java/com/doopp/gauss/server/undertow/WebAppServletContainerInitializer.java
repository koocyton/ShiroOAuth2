package com.doopp.gauss.server.undertow;

import com.doopp.gauss.server.configuration.ApplicationConfiguration;
import com.doopp.gauss.server.configuration.WebMvcConfigurer;
import com.doopp.gauss.server.filter.SessionFilter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

public class WebAppServletContainerInitializer implements ServletContainerInitializer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

        // set encode
        FilterRegistration.Dynamic encodingFilter = ctx.addFilter("encoding-filter", CharacterEncodingFilter.class);
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        // encodingFilter.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, "/*");
        encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        // session filter
        FilterRegistration.Dynamic sessionFilter = ctx.addFilter("sessionFilter", SessionFilter.class);
        sessionFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        // root web application context
        AnnotationConfigWebApplicationContext rootWebAppContext = new AnnotationConfigWebApplicationContext();
        rootWebAppContext.register(ApplicationConfiguration.class, WebMvcConfigurer.class);
        // rootWebAppContext.register(WebMvcConfigurer.class);
        // rootWebAppContext.scan("com.doopp.gauss.server.configuration");
        ctx.addListener(new ContextLoaderListener(rootWebAppContext));

        // set spring mvc servlet
        //注解扫描上下文
        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        // dispatcherServlet.setContextConfigLocation("classpath:config/spring-mvc/mvc-dispatcher-servlet.xml");
        ServletRegistration.Dynamic dispatcher = ctx.addServlet("mvc-dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");

        // 添加 druid sql 监控
        ServletRegistration.Dynamic druidDispatcher = ctx.addServlet("DruidStatView", com.alibaba.druid.support.http.StatViewServlet.class);
        druidDispatcher.setInitParameter("resetEnable", "false");
        druidDispatcher.setInitParameter("loginUsername", "druidAdmin");
        druidDispatcher.setInitParameter("loginPassword", "druidPassword");
        druidDispatcher.addMapping("/druid/*");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}