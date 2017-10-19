package com.doopp.gauss.server.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class MyBatisConfiguration {

    @Primary
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

//    /**
//     * (事务管理)transaction manager, use JtaTransactionManager for global tx
//     * @return
//     */
//    @Bean
//    public DataSourceTransactionManager userTransactionManager(@Qualifier("userDataSource") DruidDataSource userDataSource) {
//        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
//        dataSourceTransactionManager.setDataSource(userDataSource);
//        return dataSourceTransactionManager;
//    }

//    <!-- 使用annotation定义事务 -->
//    <tx:annotation-driven transaction-manager="userTransactionManager" />
}
