package com.yutong.hksync.config;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
// 扫描mysql mapper接口
@MapperScan(basePackages = "com.yutong.hksync.mapper.safety", sqlSessionTemplateRef = "mysqlSqlSessionTemplate")
public class MysqlDataSourceConfig {

    // 主数据源Primary
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.mysql.datasource")
    @Primary
    public DataSource mysqlDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    // SqlSessionFactory
    @Bean(name = "mysqlSqlSessionFactory")
    @Primary
    public SqlSessionFactoryBean mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        // 加载mysql mapper xml
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/safety/*.xml");
        factoryBean.setMapperLocations(resources);
        // 实体类别名包
//        factoryBean.setTypeAliasesPackage("com.yutong.hksync.entity");
        return factoryBean;
    }

    // SqlSessionTemplate
    @Bean(name = "mysqlSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate mysqlSqlSessionTemplate(@Qualifier("mysqlSqlSessionFactory") SqlSessionFactoryBean factoryBean) throws Exception {
        return new SqlSessionTemplate(factoryBean.getObject());
    }

    // MySQL事务管理器
    @Bean(name = "mysqlTransactionManager")
    @Primary
    public DataSourceTransactionManager mysqlTransactionManager(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}