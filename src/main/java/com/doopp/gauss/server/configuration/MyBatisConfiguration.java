package com.doopp.gauss.server.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.Properties;

// @Configuration
public class MyBatisConfiguration {

    @Bean
    public DruidDataSource druidDataSource(Properties applicationProperties) throws Exception {

        String driver = applicationProperties.getProperty("jdbc.user.driver");
        String url = applicationProperties.getProperty("jdbc.user.url");
        String username = applicationProperties.getProperty("jdbc.user.username");
        String password = applicationProperties.getProperty("jdbc.user.password");
        int initialSize = Integer.parseInt(applicationProperties.getProperty("jdbc.initialSize"));
        int maxActive = Integer.parseInt(applicationProperties.getProperty("jdbc.maxActive"));
        int maxIdle = Integer.parseInt(applicationProperties.getProperty("jdbc.maxIdle"));
        int minIdle = Integer.parseInt(applicationProperties.getProperty("jdbc.minIdle"));
        int maxWait = Integer.parseInt(applicationProperties.getProperty("jdbc.maxWait"));

        DruidDataSource druidDataSource = new DruidDataSource();
        // 基本属性 url、user、password
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        // 配置初始化大小、最小、最大
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxIdle(maxIdle);
        druidDataSource.setMaxActive(maxActive);
        // 配置获取连接等待超时的时间
        druidDataSource.setMaxWait(maxWait);

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);

        // 配置一个连接在池中最小生存的时间，单位是毫秒
        druidDataSource.setMinEvictableIdleTimeMillis(300000);

        druidDataSource.setValidationQuery("SELECT 'x'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);

        // 打开PSCache，并且指定每个连接上PSCache的大小
        // Oracle，则把poolPreparedStatements配置为true，mysql可以配置为false
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        // 配置监控统计拦截的filters
        druidDataSource.setFilters("stat,log4j");

        return druidDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DruidDataSource druidDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(druidDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis-mapper/*.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.doopp.gauss.common.utils.dao");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        return mapperScannerConfigurer;
    }

    /**
     * (事务管理)transaction manager, use JtaTransactionManager for global tx
     */
    @Bean
    public DataSourceTransactionManager userTransactionManager(DruidDataSource druidDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(druidDataSource);
        return dataSourceTransactionManager;
    }
}
