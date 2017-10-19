//package com.doopp.gauss.server.configuration;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cache.ehcache.EhCacheCacheManager;
//import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//@Configuration
//public class EhcacheConfiguration {
//
//    @Bean
//    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
//        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
//        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("config/ehcache/ehcache.xml"));
//        return ehCacheManagerFactoryBean;
//    }
//
//    @Bean
//    public EhCacheCacheManager ehCacheCacheManager (@Qualifier("ehCacheManagerFactoryBean") EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
//        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
//        ehCacheCacheManager.setCacheManager(ehCacheManagerFactoryBean.getObject());
//        return ehCacheCacheManager;
//    }
//}