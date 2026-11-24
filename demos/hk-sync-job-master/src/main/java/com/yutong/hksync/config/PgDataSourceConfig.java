package com.yutong.hksync.config;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
// 扫描pg mapper接口
@MapperScan(basePackages = "com.yutong.hksync.mapper.hik", sqlSessionTemplateRef = "pgSqlSessionTemplate")
public class PgDataSourceConfig {

    @Bean(name = "pgDataSource")
    @ConfigurationProperties(prefix = "spring.pg.datasource")
    public DataSource pgDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "pgSqlSessionFactory")
    public SqlSessionFactoryBean pgSqlSessionFactory(@Qualifier("pgDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/hik/*.xml");
        factoryBean.setMapperLocations(resources);
//        factoryBean.setTypeAliasesPackage("com.yutong.hksync.entity");
        return factoryBean;
    }

    @Bean(name = "pgSqlSessionTemplate")
    public SqlSessionTemplate pgSqlSessionTemplate(@Qualifier("pgSqlSessionFactory") SqlSessionFactoryBean factoryBean) throws Exception {
        return new SqlSessionTemplate(factoryBean.getObject());
    }

    // PG独立事务管理器
    @Bean(name = "pgTransactionManager")
    public DataSourceTransactionManager pgTransactionManager(@Qualifier("pgDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}