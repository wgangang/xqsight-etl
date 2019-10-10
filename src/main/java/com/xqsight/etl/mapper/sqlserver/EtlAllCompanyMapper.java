package com.xqsight.etl.mapper.sqlserver;

import com.xqsight.etl.domain.EtlAllCompany;

import java.util.List;

/**
 * etl 同步所有公司
 *
 * @author ganggang.wang
 * @date
 */
public interface EtlAllCompanyMapper {

    /**
     * 工具CompanyUuid删除同步的公司
     *
     * @param companyUuid
     * @return
     */
    int deleteByCompanyUuid(String companyUuid);

    /**
     * 插入需要同步的公司
     *
     * @param record
     * @return
     */
    int insert(EtlAllCompany record);

    /**
     * 通过公司Uuid查询同步的公司
     *
     * @param companyUuid
     * @return
     */
    EtlAllCompany selectByCompanyUuid(String companyUuid);

    /**
     * 查询所有同步公司
     *
     * @return
     */
    List<EtlAllCompany> selectAll();

    /**
     * 更新公司 不全量同步
     *
     * @return
     */
    int updateAllFullSynchronize();

    /**
     * 更新 同步公司 根据companyUuid
     *
     * @param record
     * @return
     */
    int updateByCompanyUuid(EtlAllCompany record);
}