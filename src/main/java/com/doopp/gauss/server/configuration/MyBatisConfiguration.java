package com.doopp.gauss.server.configuration;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import io.undertow.server.handlers.resource.FileResource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.io.File;
import java.io.InputStream;

@Configuration
public class MyBatisConfiguration {

    @Bean
    public DruidDataSource userDataSource() throws Exception {
        DruidDataSource druidDataSource = new DruidDataSource();
        // 基本属性 url、user、password
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/spring-api?characterEncoding=utf-8");
        druidDataSource.setUsername("spring-backend");
        druidDataSource.setPassword("spring-backend");
        // 配置初始化大小、最小、最大
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(20);
        // 配置获取连接等待超时的时间
        druidDataSource.setMaxWait(60000);

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

    /**
     * 开启慢查询
     *
     * @return StatFilter
     */
    @Bean(name="stat-filter")
    public StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(200);
        statFilter.setLogSlowSql(true);
        return statFilter;
    }

    @Bean
    public SqlSessionFactoryBean userSqlSessionFactory(@Qualifier("userDataSource") DruidDataSource userDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(userDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("mybatis-mapper/*.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.doopp.gauss.api.dao");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("userSqlSessionFactory");
        return mapperScannerConfigurer;
    }

    /**
     * (事务管理)transaction manager, use JtaTransactionManager for global tx
     * @return
     */
    @Bean
    public DataSourceTransactionManager userTransactionManager(@Qualifier("userDataSource") DruidDataSource userDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(userDataSource);
        return dataSourceTransactionManager;
    }

//    <!-- 使用annotation定义事务 -->
//    <tx:annotation-driven transaction-manager="userTransactionManager" />
}
