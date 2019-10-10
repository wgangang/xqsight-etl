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

import javax.sql.DataSource;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Configuration
@MapperScan(basePackages = "com.xqsight.etl.mapper.sqlserver", sqlSessionFactoryRef = "sqlServerSqlSessionFactoryBean")
public class SqlServerMybatisConfig {

    @Bean(name = "sqlServerDataSource", destroyMethod = "close")
    public DataSource sqlServerDataSource(){
        return DataSourceBuild.createDataSource("companylist");
    }


    @Bean(name = "sqlServerSqlSessionFactoryBean")
    public SqlSessionFactory sqlServerSqlSessionFactoryBean(@Qualifier("sqlServerDataSource") DataSource dataSource) {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // 添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            // 设置xml扫描路径
            bean.setMapperLocations(resolver.getResources("classpath:com/xqsight/etl/mapper/sqlserver/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            throw new RuntimeException("sqlSessionFactory init fail", e);
        }
    }


    /**
     * 用于实际查询的sql工具,传统dao开发形式可以使用这个,基于mapper代理则不需要注入
     *
     * @param sqlSessionFactory
     * @return
     */
    @Bean(name = "sqlServerSqlSessionTemplate")
    public SqlSessionTemplate sqlServerSqlSessionTemplate(@Qualifier("sqlServerSqlSessionFactoryBean") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
