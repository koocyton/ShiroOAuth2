package com.doopp.gauss.server.configuration;


import net.sf.ehcache.Ehcache;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

// @Configuration
// @EnableCaching
public class EhcacheConfiguration {

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("config/ehcache/ehcache.xml"));
        ehCacheManagerFactoryBean.setCacheManagerName("cacheManager");
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public Ehcache sessionCache() {
        EhCacheFactoryBean cache = new EhCacheFactoryBean();
        cache.setCacheManager(ehCacheManagerFactoryBean().getObject());
        cache.setCacheName("session");
        return cache.getObject();
    }


}