package com.xqsight.etl.service;


import com.google.common.collect.Lists;
import com.xqsight.etl.domain.EtlFullCompany;
import com.xqsight.etl.domain.EtlAllCompany;
import com.xqsight.etl.mapper.sqlserver.EtlFullCompanyMapper;
import com.xqsight.etl.mapper.sqlserver.EtlAllCompanyMapper;
import com.xqsight.etl.util.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
@Slf4j
public class CompanyService {

    @Resource
    private EtlAllCompanyMapper etlAllCompanyMapper;

    @Resource
    private EtlFullCompanyMapper fullEtlCompanyMapper;

    /**
     * 查询同步的公司信息
     *
     * @return Map<serverIp, List < etlAllCompany>>
     */
    public Map<String, List<EtlAllCompany>> getCompanyIpMap() {

        /** 需要同步的V10 公司信息 */
        List<EtlAllCompany> etlAllCompanies = etlAllCompanyMapper.selectAll();

        /** 查询非法公司 */
        String[] invalidCompanies = PropertyUtils.getValue(PropertyUtils.INVALID_COMPANY).split(",");
        List<String> invalidCompany = Arrays.asList(invalidCompanies);

        /** 同步过的所有公司 */
        List<EtlFullCompany> etlFullCompanies = fullEtlCompanyMapper.selectAll();
        Map<String, EtlFullCompany> etlFullCompanyMap = Optional.ofNullable(etlFullCompanies).orElseGet(Lists::newArrayList).stream()
                .filter(Objects::nonNull).collect(Collectors.toMap(EtlFullCompany::getCompanyUuid, data -> data, (v1, v2) -> v2));

        Map<String, List<EtlAllCompany>> targetCompanyMap = Optional.ofNullable(etlAllCompanies).orElseGet(Lists::newArrayList).stream()
                // 过滤 无效的公司和无效的数据库
                .filter(Objects::nonNull).filter(company -> {
                    if (StringUtils.isEmpty(company.getCompanyUuid()) || invalidCompany.contains(company.getDbName())) {
                        return false;
                    }
                    return true;
                }).map(company -> {
                    if (Objects.isNull(MapUtils.getObject(etlFullCompanyMap, company.getCompanyUuid()))) {
                        /** 新公司同步全量 */
                        company.setFullSynchronize(Boolean.TRUE);
                        EtlFullCompany fullEtlCompany = new EtlFullCompany();
                        fullEtlCompany.setCompanyUuid(company.getCompanyUuid());
                        fullEtlCompany.setCount(0);
                        fullEtlCompany.setType("新公司");
                        fullEtlCompanyMapper.insert(fullEtlCompany);
                    }
                    return company;
                }).collect(Collectors.groupingBy(EtlAllCompany::getServerIp));

        log.info("执行的公司数：{}", targetCompanyMap.values().stream().collect(Collectors.toList()).size());

        return targetCompanyMap;
    }

    /**
     *
     */
    public void fullEtlEnd() {
        etlAllCompanyMapper.updateAllFullSynchronize();
        fullEtlCompanyMapper.updateEtlFullCompanyCount();
    }
}



