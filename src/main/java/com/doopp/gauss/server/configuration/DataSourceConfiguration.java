package com.doopp.gauss.server.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {

    @Bean(name="userDataSource")
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

//    /**
//     * 开启慢查询
//     *
//     * @return StatFilter
//     */
//    @Bean(name="stat-filter")
//    public StatFilter statFilter() {
//        StatFilter statFilter = new StatFilter();
//        statFilter.setSlowSqlMillis(200);
//        statFilter.setLogSlowSql(true);
//        return statFilter;
//    }
}
