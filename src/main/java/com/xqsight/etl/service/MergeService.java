package com.xqsight.etl.service;


import com.google.common.collect.Lists;
import com.xqsight.etl.VipEtlMain;
import com.xqsight.etl.constant.Constant;
import com.xqsight.etl.domain.EtlJobInfo;
import com.xqsight.etl.domain.EtlUpdateSql;
import com.xqsight.etl.mapper.sqlserver.MergeMapper;
import com.xqsight.etl.mapper.sqlserver.EtlUpdateSqlMapper;
import com.xqsight.etl.mapper.sqlserver.EtlJobInfoMapper;
import com.xqsight.etl.metadata.Identification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
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
public class MergeService {

    @Resource
    private MergeMapper mergeMapper;

    @Resource
    private EtlJobInfoMapper jobInfoMapper;

    @Resource
    private EtlUpdateSqlMapper etlUpdateSqlMapper;


    public void truncateTempTable() {
        exec("truncate");
    }

    public void merge() {
        exec("merge");
    }

    private void exec(String type) {
        try {
            List<EtlUpdateSql> etlUpdateSqls = etlUpdateSqlMapper.selectAll();
            VipEtlMain.openPool();
            CountDownLatch countDownLatch = new CountDownLatch(etlUpdateSqls.size());
            for (EtlUpdateSql etlUpdateSql : etlUpdateSqls) {
                CompletableFuture.supplyAsync(() -> {
                    try {
                        if ("truncate".equals(type)) {
                            truncateOdsStageTale(etlUpdateSql);
                        } else if ("merge".equals(type)) {
                            merge(etlUpdateSql);
                        }
                    } catch (Exception e) {
                        log.error(type + "异常", e);
                    } finally {
                        countDownLatch.countDown();
                    }
                    return "";
                }, VipEtlMain.executorService);
            }
            countDownLatch.await();
        } catch (Exception e) {
            log.error(type + "发生了异常", e);
        } finally {
        }
    }

    private void truncateOdsStageTale(EtlUpdateSql etlUpdateSql) {
        List<EtlJobInfo> UnLockEtlJobInfo = jobInfoMapper.selectAllWithNoLock();
        Set<String> jobJsonName = Optional.ofNullable(UnLockEtlJobInfo).orElseGet(Lists::newArrayList).stream()
                .map(EtlJobInfo::getJsonName).map(name -> StringUtils.split(name, Constant.UNDER_LINE)[1]).collect(Collectors.toSet());
        if (!jobJsonName.contains(etlUpdateSql.getTableName())) {
            int res = mergeMapper.truncateTable(etlUpdateSql.getTableName());
            log.info("truncate temp table : temp_{},result:{}", etlUpdateSql.getTableName(), res);
        } else {
            log.info("vtemp_{} is lock,not truncate", etlUpdateSql.getTableName());
        }


    }

    private void merge(EtlUpdateSql etlUpdateSql) {
        int res = mergeMapper.merge(etlUpdateSql.getUpdateSql());
        log.info("update :  " + etlUpdateSql.getTableName() + "---> " + res);
        if (res == -1) {
            jobInfoMapper.unlockJobAndUpdateState(Identification.METAJOBINFO_STATE.FAIL, etlUpdateSql.getTableName());
        } else {
            jobInfoMapper.unlockJob(etlUpdateSql.getTableName());
        }
    }
}
