package com.xqsight.etl.mapper.sqlserver;


import com.xqsight.etl.domain.SourceCompany;


import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface SourceCompanyMapper {

    int insert(SourceCompany record);

    List<SourceCompany> selectAll();
}