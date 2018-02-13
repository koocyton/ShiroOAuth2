package com.doopp.gauss.server.undertow;

import com.doopp.gauss.server.configuration.ApplicationConfiguration;
import com.doopp.gauss.server.configuration.WebMvcConfigurer;
import com.doopp.gauss.server.filter.SessionFilter;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

public class WebAppServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

        // set encode
        FilterRegistration.Dynamic encodingFilter = ctx.addFilter("encoding-filter", CharacterEncodingFilter.class);
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        // root web application context
        AnnotationConfigWebApplicationContext rootWebAppContext = new AnnotationConfigWebApplicationContext();
        rootWebAppContext.register(ApplicationConfiguration.class, WebMvcConfigurer.class);
        ctx.addListener(new ContextLoaderListener(rootWebAppContext));

        // set spring mvc dispatcher
        DispatcherServlet dispatcherServlet = new DispatcherServlet(rootWebAppContext);
        ServletRegistration.Dynamic dispatcher = ctx.addServlet("mvc-dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        // OAuth filter
        // FilterRegistration.Dynamic shiroFilter = ctx.addFilter("shiroFilter", new DelegatingFilterProxy("shiroFilter", webApplicationContext));
        // FilterRegistration.Dynamic shiroFilter = ctx.addFilter("shiroFilter", DelegatingFilterProxy.class);
        // shiroFilter.setAsyncSupported(true);
        // shiroFilter.setInitParameter("targetFilterLifecycle", "true");
        // shiroFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        // 添加 druid sql 监控
        // ServletRegistration.Dynamic druidDispatcher = ctx.addServlet("DruidStatView", com.alibaba.druid.support.http.StatViewServlet.class);
        // druidDispatcher.setInitParameter("resetEnable", "false");
        // druidDispatcher.setInitParameter("loginUsername", "druid-Admin");
        // druidDispatcher.setInitParameter("loginPassword", "druid-Password");
        // druidDispatcher.addMapping("/druid/*");

        // session filter
        FilterRegistration.Dynamic sessionFilter = ctx.addFilter("sessionFilter", SessionFilter.class);
        sessionFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }
}