package com.xqsight.etl.mapper.sqlserver;

import com.xqsight.etl.domain.EtlFullCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface EtlFullCompanyMapper {

    /**
     * 插入同步记录
     *
     * @param record
     * @return
     */
    int insert(EtlFullCompany record);

    /**
     * 批量查询同步过的公司
     *
     * @param etlFullCompanies
     * @return
     */
    int batchInsert(@Param("etlFullCompanies") List<EtlFullCompany> etlFullCompanies);

    /**
     * 查询同步过的所有公司
     *
     * @return
     */
    List<EtlFullCompany> selectAll();

    /**
     * 查询同步次数小于3的
     *
     * @return
     */
    List<String> selectNeedEtl();

    /**
     * 更新 全量同步的次数
     *
     * @return
     */
    int updateEtlFullCompanyCount(@Param("companyUuidList") List<String> companyUuidList);
}