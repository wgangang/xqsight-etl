package com.xqsight.etl.mapper.sqlserver;


import com.xqsight.etl.domain.EtlUpdateSql;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface EtlUpdateSqlMapper {

    int deleteByPrimaryKey(String tablename);

    int insert(EtlUpdateSql record);

    EtlUpdateSql selectByPrimaryKey(String tablename);

    List<EtlUpdateSql> selectAll();

    List<String> selectAllTableName();

    int updateByPrimaryKey(EtlUpdateSql record);
}