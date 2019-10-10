package com.xqsight.etl.util;

import com.xqsight.etl.domain.EtlJobInfo;
import com.xqsight.etl.mapper.sqlserver.EtlJobInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Register {

    public static void main(String[] args) {
        new Register().registerTable("v_activity_employee","ALL");
        //new Register().registerIp("10.30.1.84");
    }

    public EtlJobInfoMapper initSpringContainer() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        return ctx.getBean(EtlJobInfoMapper.class);
    }

    /**
     *
     * @param tableName 数仓中的表名 ，如pro_housingacquisition_emp
     * @param jsonType 增量还是全量：INCREMRNT，ALL
     */
    public void registerTable(String tableName,String jsonType){
        //1、etl_update_sql中加入对应的datax json与merge语句
        //2、ods_stage和ods表创建
        //3、创建jobinfo信息
        EtlJobInfoMapper jobInfoMapper = initSpringContainer();
        List<EtlJobInfo> jobInfoList = jobInfoMapper.selectLikeJsonName("org_employee.json");
        List<EtlJobInfo> registerList = new ArrayList<>();
        for (EtlJobInfo jobInfo : jobInfoList) {
            EtlJobInfo jobInfoNew = new EtlJobInfo();
            BeanUtils.copyProperties(jobInfo, jobInfoNew);
            jobInfoNew.setJsonName(jobInfo.getJsonName().replace("org_employee", tableName));
            jobInfoNew.setStartTime("1990-01-01 00:00:00");
            jobInfoNew.setEndTime("1990-01-01 00:00:00");
            jobInfoNew.setState(0);
            jobInfoNew.setLocked(false);
            jobInfoNew.setJobType(jsonType);
            registerList.add(jobInfoNew);
        }
        jobInfoMapper.register(registerList);

    }
    public void registerIp(String ip) {
        EtlJobInfoMapper jobInfoMapper = initSpringContainer();
        List<EtlJobInfo> jobInfoList = jobInfoMapper.selectByIp("10.30.1.87");
        List<EtlJobInfo> registerList = new ArrayList<>();
        for (EtlJobInfo jobInfo : jobInfoList) {
            EtlJobInfo jobInfoNew = new EtlJobInfo();
            BeanUtils.copyProperties(jobInfo, jobInfoNew);
            jobInfoNew.setReaderJdbc(jobInfo.getReaderJdbc().replace("10.30.1.87", ip));
            jobInfoNew.setJsonName(jobInfo.getJsonName().replace("10.30.1.87", ip));
            jobInfoNew.setStartTime("1990-01-01 00:00:00");
            jobInfoNew.setEndTime("1990-01-01 00:00:00");
            jobInfoNew.setState(0);
            jobInfoNew.setLocked(false);
            registerList.add(jobInfoNew);
        }
        jobInfoMapper.register(registerList);
    }
    public void changeIP(String oldIp,String newIp) {
        EtlJobInfoMapper jobInfoMapper = initSpringContainer();
        List<EtlJobInfo> jobInfoList = jobInfoMapper.selectByIp(oldIp);
        for (EtlJobInfo jobInfo : jobInfoList) {
            EtlJobInfo jobInfoNew = new EtlJobInfo();
            BeanUtils.copyProperties(jobInfo, jobInfoNew);
            jobInfoNew.setReaderJdbc(jobInfo.getReaderJdbc().replace(oldIp, newIp));
            jobInfoNew.setNewJsonName(jobInfo.getJsonName().replace(oldIp, newIp));
            jobInfoMapper.updateByPrimaryKey(jobInfoNew);
        }
    }

}
