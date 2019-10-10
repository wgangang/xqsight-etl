package com.xqsight.etl.mapper.sqlserver;


import com.xqsight.etl.domain.EtlJobInfo;
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
public interface EtlJobInfoMapper {

    int deleteByPrimaryKey(String jsonName);

    int insert(EtlJobInfo record);

    EtlJobInfo selectByPrimaryKey(String jsonName);

    List<EtlJobInfo> selectLikeJsonName(@Param("jsonName") String jsonName);

    /**
     * 查询可同步的任务
     *
     * @return
     */
    List<EtlJobInfo> selectAllWithNoLock();

    List<EtlJobInfo> selectAll();

    int updateByPrimaryKey(EtlJobInfo record);

    /**
     * 锁定任务
     *
     * @param jsonName
     * @return
     */
    int lockJobByName(String jsonName);

    int unlockJobAndUpdateState(@Param("status") int status, @Param("jsonName") String jsonName);

    int unlockJob(@Param("jsonName") String jsonName);

    /**
     * 更新job状态
     *
     * @param status
     * @param jsonName
     * @return
     */
    int updateJobStatus(@Param("status") int status, @Param("jsonName") String jsonName);

    /**
     * 更新job状态
     *
     * @param record
     * @return
     */
    int updateJobSuccessStatus(EtlJobInfo record);

    int register(@Param("jobInfoList") List<EtlJobInfo> jobInfoList);

    List<EtlJobInfo> selectByIp(@Param("ip") String ip);
}