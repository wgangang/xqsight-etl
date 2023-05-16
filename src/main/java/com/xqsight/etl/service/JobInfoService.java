package com.xqsight.etl.service;

import com.xqsight.etl.constant.Constant;
import com.xqsight.etl.domain.EtlErrorInfo;
import com.xqsight.etl.domain.EtlJobInfo;
import com.xqsight.etl.mapper.sqlserver.EtlErrorInfoMapper;
import com.xqsight.etl.mapper.sqlserver.EtlJobInfoMapper;
import com.xqsight.etl.metadata.Identification;
import com.xqsight.etl.util.CronUtils;
import com.xqsight.etl.util.DingtalkUtil;
import com.xqsight.etl.util.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class JobInfoService {

    @Resource
    private EtlJobInfoMapper jobInfoMapper;

    @Resource
    private EtlErrorInfoMapper errorInfoMapper;

    public List<EtlJobInfo> getCanRunJob(String dateBefore) {
        /** 查询没有执行的任务 */
        List<EtlJobInfo> etlJobInfoList = jobInfoMapper.selectAllWithNoLock();

        for (EtlJobInfo jobInfo : etlJobInfoList) {
            String startTime = jobInfo.getStartTime();
            String endTime = jobInfo.getEndTime();
            /** 如果为空不运行 */
            if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
                continue;
            }
            if (jobInfo.getState() != null && jobInfo.getState().intValue() != 0) {
                continue;
            }

            if (Objects.equals(dateBefore, "0")) {
                startTime = endTime;
            } else {
                startTime = CronUtils.getDayBefore(Integer.valueOf(dateBefore));
            }

            endTime = CronUtils.getEndTime();
            jobInfo.setStartTime(startTime);
            jobInfo.setEndTime(endTime);
        }
        return etlJobInfoList;
    }

    /**
     * 执行datax
     *
     * @param exeJob
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int execute(EtlJobInfo exeJob) throws IOException, InterruptedException {
        String jsonName = exeJob.getJsonName();
        /** 锁定任务 */
        jobInfoMapper.lockJobByName(jsonName);

        String makeCommand = makeCommand(jsonName);
        log.info("datax command:{}", makeCommand);

        Process process = Runtime.getRuntime().exec(makeCommand);
        keepIO(process, jsonName);
        int state = process.waitFor();
        process.destroy();

        if (state == Identification.METAJOBINFO_STATE.OK) {
            exeJob.setJsonName(jsonName);
            jobInfoMapper.updateJobSuccessStatus(exeJob);
        } else {
            jobInfoMapper.updateJobStatus(state, jsonName);
        }
        return state;
    }

    /**
     * 生成执行命令
     *
     * @param jsonName
     * @return
     */
    private String makeCommand(String jsonName) {
        StringBuilder sb = new StringBuilder();
        sb.append("python ");
        sb.append(PropertyUtils.getValue("dataxDir").trim());
        sb.append("/bin/datax.py ");
        sb.append(PropertyUtils.getValue("jsonFileDir"));
        sb.append("/");
        sb.append(jsonName).append(Constant.JOB_SUFFIX);
        return sb.toString();
    }

    private void keepIO(Process process, String jsonName) throws InterruptedException {
        Thread inputStreamThread = new ProcessClearStream(process.getInputStream(), jsonName);
        Thread errorStreamThread = new ProcessClearStream(process.getErrorStream(), jsonName);

        inputStreamThread.start();
        errorStreamThread.start();

        inputStreamThread.join();
        errorStreamThread.join();
    }

    private class ProcessClearStream extends Thread {
        private InputStream in;
        private String jsonName;

        public ProcessClearStream(InputStream in, String jsonName) {
            this.in = in;
            this.jsonName = jsonName;
        }

        @Override
        public void run() {
            InputStreamReader reader = null;
            BufferedReader br = null;
            try {
                reader = new InputStreamReader(in);
                br = new BufferedReader(reader);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (StringUtils.containsIgnoreCase(line, "DataXException")) {
                        EtlErrorInfo errorInfo = new EtlErrorInfo();
                        errorInfo.setCatchTime(new Date());
                        errorInfo.setErrorInfo(line);
                        errorInfo.setJsonName(jsonName);
                        errorInfoMapper.insert(errorInfo);
                        //DingtalkUtil.reportMarkdownMessage("大客户ETL错误通知", PropertyUtils.getValue(Constant.ENV) + "环境：" + jsonName + ",拉去数据失败", line);
                    }
                }
            } catch (Exception e) {
                log.error("error write error !", e);
            } finally {
                try {
                    br.close();
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

    }

}
