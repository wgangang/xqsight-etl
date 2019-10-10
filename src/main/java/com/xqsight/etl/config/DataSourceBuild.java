package com.xqsight.etl.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.xqsight.etl.util.PropertyUtils;

import java.util.Arrays;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DataSourceBuild {

    public static DruidDataSource createDataSource(String key) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(PropertyUtils.getValue(key + ".driver"));
        dataSource.setUrl(PropertyUtils.getValue(key + ".jdbcurl"));
        dataSource.setUsername(PropertyUtils.getValue(key + ".username"));
        dataSource.setPassword(PropertyUtils.getValue(key + ".password"));
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(10);
        dataSource.setMaxWait(5000);
        dataSource.setTimeBetweenEvictionRunsMillis(5000);
        dataSource.setMinEvictableIdleTimeMillis(30000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(200);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(1800);
        dataSource.setLogAbandoned(true);
        dataSource.setProxyFilters(Arrays.asList(statFilter()));
        return dataSource;
    }

    private static StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(1000 * 60 * 5);
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(false);
        return statFilter;
    }

}
