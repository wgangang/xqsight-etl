package com.xqsight.etl.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xqsight.etl.common.DataxJobMap;
import com.xqsight.etl.common.jobinfo.DataxJob;
import com.xqsight.etl.domain.EtlUpdateSql;
import com.xqsight.etl.mapper.sqlserver.EtlUpdateSqlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


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
public class DataxService {

    @Resource
    private EtlUpdateSqlMapper etlUpdateSqlMapper;

    /**
     * 查询同步的sql
     * 通过数据的数据 映射 成 map<tableName,dataxJob> </tableName,dataxJob>
     *
     * @return
     */
    public DataxJobMap getDataxJobMap() {
        /** 查询需要的sql */
        List<EtlUpdateSql> etlUpdateSqlList = etlUpdateSqlMapper.selectAll();

        Map<String, DataxJob> dataxJobs = Maps.newHashMap();

        for (EtlUpdateSql etlUpdateSql : etlUpdateSqlList) {
            String dataxJson = etlUpdateSql.getDataxJson();
            if (Objects.isNull(dataxJson)) {
                continue;
            }
            DataxJob dataxJob = JSONObject.parseObject(dataxJson, DataxJob.class);
            dataxJobs.put(etlUpdateSql.getTableName(), dataxJob);
        }
        return new DataxJobMap(dataxJobs);
    }
}
