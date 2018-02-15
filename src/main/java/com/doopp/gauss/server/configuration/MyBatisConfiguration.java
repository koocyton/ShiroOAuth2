package com.doopp.gauss.server.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.Properties;

// @Configuration
public class MyBatisConfiguration {

    @Bean
    public HikariDataSource hikariDataSource(Properties applicationProperties) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(applicationProperties.getProperty("jdbc.user.driver"));
        config.setJdbcUrl(applicationProperties.getProperty("jdbc.user.url"));
        config.setUsername(applicationProperties.getProperty("jdbc.user.username"));
        config.setPassword(applicationProperties.getProperty("jdbc.user.password"));
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(100);
        config.setConnectionTestQuery("select 1");

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        return new HikariDataSource(config);
    }

//    @Bean
//    public DruidDataSource druidDataSource(Properties applicationProperties) throws Exception {
//
//        DruidDataSource druidDataSource = new DruidDataSource();
//        // 基本属性 url、user、password
//        druidDataSource.setDriverClassName(applicationProperties.getProperty("jdbc.user.driver"));
//        druidDataSource.setUrl(applicationProperties.getProperty("jdbc.user.url"));
//        druidDataSource.setUsername(applicationProperties.getProperty("jdbc.user.username"));
//        druidDataSource.setPassword(applicationProperties.getProperty("jdbc.user.password"));
//        // 配置初始化大小、最小、最大
//        druidDataSource.setInitialSize(Integer.parseInt(applicationProperties.getProperty("jdbc.initialSize")));
//        druidDataSource.setMinIdle(Integer.parseInt(applicationProperties.getProperty("jdbc.minIdle")));
//        // druidDataSource.setMaxIdle(maxIdle);
//        druidDataSource.setMaxActive(Integer.parseInt(applicationProperties.getProperty("jdbc.maxActive")));
//        // 配置获取连接等待超时的时间
//        druidDataSource.setMaxWait(Integer.parseInt(applicationProperties.getProperty("jdbc.maxWait")));
//
//        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
//        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
//
//        // 配置一个连接在池中最小生存的时间，单位是毫秒
//        druidDataSource.setMinEvictableIdleTimeMillis(300000);
//
//        druidDataSource.setValidationQuery("SELECT 'x'");
//        druidDataSource.setTestWhileIdle(true);
//        druidDataSource.setTestOnBorrow(false);
//        druidDataSource.setTestOnReturn(false);
//
//        // 打开PSCache，并且指定每个连接上PSCache的大小
//        // Oracle，则把poolPreparedStatements配置为true，mysql可以配置为false
//        druidDataSource.setPoolPreparedStatements(false);
//        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
//
//        // 配置监控统计拦截的filters
//        // druidDataSource.setFilters("stat,log4j");
//
//        return druidDataSource;
//    }

//     @Bean
//     public StatFilter statFilter() {
//         StatFilter statFilter = new StatFilter();
//         statFilter.setLogSlowSql(true);
//         statFilter.setSlowSqlMillis(200);
//         return statFilter;
//     }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(HikariDataSource hikariDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(hikariDataSource);
        // PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis-mapper/*.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.doopp.gauss.common.dao"); // 多个目录用逗号分隔
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        return mapperScannerConfigurer;
    }

    /**
     * (事务管理)transaction manager, use JtaTransactionManager for global tx
     */
    @Bean
    public DataSourceTransactionManager userTransactionManager(HikariDataSource hikariDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(hikariDataSource);
        return dataSourceTransactionManager;
    }
}
