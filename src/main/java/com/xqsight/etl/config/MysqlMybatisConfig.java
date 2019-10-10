/*
package com.xqsight.etl.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;


@Configuration
@MapperScan(basePackages = "com.xqsight.etl.mapper.mysql", sqlSessionFactoryRef = "mysqlSqlSessionFactoryBean")
public class MysqlMybatisConfig {

    @Bean(name = "mysqlDataSource", destroyMethod = "close")
    public DataSource mysqlDataSource() throws SQLException {
        return DataSourceBuild.createDataSource("metadb");
    }


    @Bean(name = "mysqlSqlSessionFactoryBean")
    public SqlSessionFactory mysqlSqlSessionFactoryBean(@Qualifier("mysqlDataSource") DataSource dataSource) {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // 添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            // 设置xml扫描路径
            bean.setMapperLocations(resolver.getResources("classpath:com/qf/v20etl/mapper/mysql/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            throw new RuntimeException("sqlSessionFactory init fail", e);
        }
    }


    */
/**
     * 用于实际查询的sql工具,传统dao开发形式可以使用这个,基于mapper代理则不需要注入
     *
     * @param sqlSessionFactory
     * @return
     *//*

    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate mysqlSqlSessionTemplate(@Qualifier("mysqlSqlSessionFactoryBean") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "mysqlTransactionTemplate")
    public TransactionTemplate mysqlTransactionTemplate(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }
}
*/
