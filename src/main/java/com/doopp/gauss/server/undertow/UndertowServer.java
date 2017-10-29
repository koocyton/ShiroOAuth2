package com.doopp.gauss.server.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.handlers.DefaultServlet;
import io.undertow.servlet.util.ImmediateInstanceFactory;

import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import javax.servlet.ServletContainerInitializer;
import java.util.HashSet;

public class UndertowServer implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String webAppName;
    private Resource webAppRoot;
    private String host = "127.0.0.1";
    private int port = 8080;
    private ServletContainerInitializer servletContainerInitializer;

    private Undertow server;
    private DeploymentManager manager;

    @Override
    public void afterPropertiesSet() throws Exception {
        // web servlet
        InstanceFactory<? extends ServletContainerInitializer> instanceFactory = new ImmediateInstanceFactory<>(servletContainerInitializer);
        ServletContainerInitializerInfo sciInfo = new ServletContainerInitializerInfo(WebAppServletContainerInitializer.class, instanceFactory, new HashSet<>());
        DeploymentInfo deploymentInfo = Servlets.deployment()
                .addServletContainerInitalizer(sciInfo)
                .setClassLoader(UndertowServer.class.getClassLoader())
                .setContextPath(webAppName)
                .setDeploymentName(webAppName + "-war")
                .setResourceManager(new FileResourceManager(webAppRoot.getFile(), 0))
                .addServlet(Servlets.servlet("default", DefaultServlet.class));

        // add webSocket
        final WebSocketDeploymentInfo wsInfo = new WebSocketDeploymentInfo();
        deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, wsInfo);

        manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        manager.deploy();
        HttpHandler httpHandler = manager.start();

        //RedirectHandler defaultHandler = Handlers.redirect("/");
        //PathHandler pathHandler = Handlers.path(defaultHandler);
        //pathHandler.addPrefixPath("/", httpHandler);

        server = Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(httpHandler)
            .build();

        server.start();

        logger.info("Undertow web server started; web application available at http://{}:{}", host, port);
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Stopping Undertow web server on port " + port);
        server.stop();
        manager.stop();
        manager.undeploy();
        logger.info("Undertow web server on port " + port + " stopped");
    }

    public void setWebAppRoot(Resource webAppRoot) {
        this.webAppRoot = webAppRoot;
    }

    public void setWebAppName(String webAppName) {
        this.webAppName = webAppName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setServletContainerInitializer(ServletContainerInitializer servletContainerInitializer) {
        this.servletContainerInitializer = servletContainerInitializer;
    }
}
