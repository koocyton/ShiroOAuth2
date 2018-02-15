package com.doopp.gauss.server.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.net.ssl.*;
import javax.servlet.ServletContainerInitializer;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashSet;

public class UndertowServer implements InitializingBean, DisposableBean {

    private String webAppName;
    private Resource webAppRoot;
    private String host = "127.0.0.1";
    private int port = 8088;
    private int sslPort = 8089;
    private InputStream jksStream;
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
                // .addServlet(Servlets.servlet("default", DefaultServlet.class))
                .setResourceManager(new FileResourceManager(webAppRoot.getFile(), 0))
                .setClassLoader(UndertowServer.class.getClassLoader())
                .setContextPath(webAppName)
                .setDeploymentName(webAppName + "-war");

        manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        manager.deploy();
        HttpHandler httpHandler = path()
                .addPrefixPath("/", manager.start())
                .addPrefixPath("/game-socket", websocket(new GameSocketConnectionCallback()));

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(getKeyManagers(), null, null);

        server = Undertow.builder()
                .addHttpListener(port, host)
                .addHttpsListener(sslPort, host, sslContext)
                .setHandler(httpHandler)
                .build();
        server.start();

        System.out.print("\n >>> Undertow web server started at http://" + host + ":" + port + " and https://" + host + ":" + sslPort + "\n\n");
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
        manager.stop();
        manager.undeploy();
        System.console().printf("Undertow web server on port " + port + " stopped");
    }

    private KeyManager[] getKeyManagers() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(jksStream, "1234567890".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "214338424690893".toCharArray());
            return keyManagerFactory.getKeyManagers();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setWebAppRoot(Resource webAppRoot) {
        this.webAppRoot = webAppRoot;//new ClassPathResource(webAppRoot);
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

    public void setJksFile(String jksFile) {
        this.jksStream = UndertowServer.class.getResourceAsStream(jksFile);
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public void setServletContainerInitializer(ServletContainerInitializer servletContainerInitializer) {
        this.servletContainerInitializer = servletContainerInitializer;
    }
}
