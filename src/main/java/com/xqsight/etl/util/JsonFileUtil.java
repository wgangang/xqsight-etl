package com.xqsight.etl.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xqsight.etl.common.DataxJobMap;
import com.xqsight.etl.constant.Constant;
import com.xqsight.etl.domain.EtlJobInfo;
import com.xqsight.etl.domain.EtlAllCompany;
import com.xqsight.etl.metadata.Identification;
import com.xqsight.etl.common.jobinfo.*;
import com.xqsight.etl.common.jobinfo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class JsonFileUtil {

    public static Logger log = LoggerFactory.getLogger(JsonFileUtil.class);

    /**
     * 生成datax需要的JSON 文件 信息
     *
     * @param companyMap
     * @param jobInfoList
     * @param dataxJobMap
     * @param channelNum
     * @param spiltNum
     */
    public static void makeDataxJsonFile(Map<String, List<EtlAllCompany>> companyMap, List<EtlJobInfo> jobInfoList,
                                         DataxJobMap dataxJobMap, String channelNum, String spiltNum) {

        Map<String, DataxJob> dataxJobMapNew = Maps.newHashMap();
        List<EtlJobInfo> jobInfoListADD = Lists.newArrayList();
        List<EtlJobInfo> jobInfoListDEL = Lists.newArrayList();

        Integer sn = Integer.valueOf(spiltNum);
        Integer cn = Integer.valueOf(channelNum);

        for (EtlJobInfo jobInfo : jobInfoList) {
            if(Objects.isNull(jobInfo.getFullTableSynchronize())){
                jobInfo.setFullTableSynchronize(Boolean.FALSE);
            }

            String jsonName = jobInfo.getJsonName();
            String templateJsonName = StringUtils.substringAfter(jsonName, Constant.UNDER_LINE);
            DataxJob dataxJob = dataxJobMap.getDataxJobMap().get(templateJsonName);
            if (dataxJob == null) {
                jobInfoListDEL.add(jobInfo);
                continue;
            }
            String ip = getIp(jobInfo.getReaderJdbc());
            List<EtlAllCompany> targetCompanyList = companyMap.get(ip);
            if (targetCompanyList == null) {
                jobInfoListDEL.add(jobInfo);
                continue;
            }

            //TODO
            /*if (!JdbcUtil.testSqlServerConnect(targetCompanyList.get(0))) {
                jobInfoListDEL.add(jobInfo);
                continue;
            }*/

            String where = jobInfo.getCondition();
            String readerSql = dataxJob.takeReaderSql()[0];

            boolean isIncrement = !jobInfo.getFullTableSynchronize();
            if(isIncrement) {
                isIncrement = jobInfo.getJobType().trim().equalsIgnoreCase(Identification.METAJOBINFO_JOBTYPE.INCREMRNT);
            }
            if (targetCompanyList.size() > sn) {
                List<EtlJobInfo> jobInfoListTemp = splitCompany(targetCompanyList, readerSql, where, jobInfo, isIncrement, dataxJobMapNew, jsonName, dataxJob, sn, cn);
                if (jobInfoListTemp != null && !jobInfoListTemp.isEmpty()) {
                    jobInfoListADD.addAll(jobInfoListTemp);
                }
                jobInfoListDEL.add(jobInfo);
            } else {
                List<String> sqlN = generateReaderSql(readerSql, where, jobInfo.getStartTime(), jobInfo.getEndTime(), targetCompanyList, isIncrement);
                DataxJob dataxJob1 = addNewDataxJob(sqlN, jobInfo, dataxJob, cn);
                dataxJobMapNew.put(jsonName, dataxJob1);
            }
        }
        if (!jobInfoListADD.isEmpty()) {
            jobInfoList.addAll(jobInfoListADD);
        }
        if (!jobInfoListDEL.isEmpty()) {
            jobInfoList.removeAll(jobInfoListDEL);
        }
        writeDatax(new DataxJobMap(dataxJobMapNew));
    }

    public static String getIp(String readerJdbc) {
        Pattern sqlServerPattern = compile("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
        Matcher matcher = sqlServerPattern.matcher(readerJdbc);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private static List<EtlJobInfo> splitCompany(List<EtlAllCompany> targetCompanyList, String readerSql, String where, EtlJobInfo jobInfo,
                                                 boolean isIncrement, Map<String, DataxJob> dataxJobMapNew, String jsonName, DataxJob dataxJob, Integer sn, Integer cn) {

        List<EtlJobInfo> jobInfoListTemp = new ArrayList<>();
        String[] jsonNames = StringUtils.split(jsonName, Constant.UNDER_LINE);

        List<List<EtlAllCompany>> temp = Lists.partition(targetCompanyList, sn);

        for (List<EtlAllCompany> list : temp) {
            try {
                StringBuilder newJsonName = new StringBuilder(jsonNames[0])
                        .append(Constant.UNDER_LINE).append(UUID.randomUUID().toString())
                        .append(Constant.UNDER_LINE).append(jsonNames[1]);

                List<String> sqlN = generateReaderSql(readerSql, where, jobInfo.getStartTime(), jobInfo.getEndTime(), list, isIncrement);
                DataxJob dataxJob1 = addNewDataxJob(sqlN, jobInfo, dataxJob, cn);
                dataxJobMapNew.put(newJsonName.toString(), dataxJob1);
                EtlJobInfo j = new EtlJobInfo();
                BeanUtils.copyProperties(jobInfo, j);
                j.setJsonName(newJsonName.toString());
                jobInfoListTemp.add(j);
            } catch (Exception e) {
                log.error("splitCompany,Error", e);
            }
        }
        return jobInfoListTemp;
    }

    private static DataxJob addNewDataxJob(List<String> sqlN, EtlJobInfo jobInfo, DataxJob dataxJob, Integer cn) {
        List<ReaderConnection> readerConnectionList = new ArrayList<>();
        for (int i = 0; i < sqlN.size(); i++) {
            ReaderConnection readerConnection = new ReaderConnection();
            readerConnection.setJdbcUrl(new String[]{jobInfo.getReaderJdbc()});
            readerConnection.setQuerySql(new String[]{sqlN.get(i)});
            readerConnectionList.add(readerConnection);
        }

        DataxJob dataxJob1 = new DataxJob();

        Job jobTemp = new Job();
        dataxJob1.setJob(jobTemp);

        Setting setting = new Setting();
        jobTemp.setSetting(setting);

        Speed speed = new Speed();
        setting.setSpeed(speed);
        speed.setChannel(cn);

        Content[] contents = new Content[1];
        contents[0] = new Content();

        jobTemp.setContent(contents);

        Writer writer = new Writer();
        contents[0].setWriter(writer);
        writer.setName(dataxJob.getJob().getContent()[0].getWriter().getName());

        WriterParameter writerParameter = new WriterParameter();
        writer.setParameter(writerParameter);
        writerParameter.setColumn(dataxJob.getJob().getContent()[0].getWriter().getParameter().getColumn());
        writerParameter.setUsername(jobInfo.getWriterName());
        writerParameter.setPassword(jobInfo.getWriterPword());

        WriterConnection[] writerConnection = new WriterConnection[1];
        writerParameter.setConnection(writerConnection);
        writerConnection[0] = new WriterConnection();
        writerConnection[0].setJdbcUrl(jobInfo.getWriterJdbc());
        writerConnection[0].setTable(dataxJob.getJob().getContent()[0].getWriter().getParameter().getConnection()[0].getTable());

        Reader reader = new Reader();
        contents[0].setReader(reader);
        reader.setName(dataxJob.getJob().getContent()[0].getReader().getName());

        ReaderParameter readerParameter = new ReaderParameter();
        reader.setParameter(readerParameter);
        readerParameter.setWhere(dataxJob.getJob().getContent()[0].getReader().getParameter().getWhere());
        readerParameter.setUsername(jobInfo.getReaderName());
        readerParameter.setPassword(jobInfo.getReaderPword());

        ReaderConnection[] readerConnections = readerConnectionList.toArray(new ReaderConnection[readerConnectionList.size()]);
        readerParameter.setConnection(readerConnections);
        return dataxJob1;
    }

    /**
     * 生成 读 sql
     *
     * @param sql
     * @param where
     * @param startTime
     * @param endTime
     * @param companyList
     * @param isIncrement
     * @return
     */
    private static List<String> generateReaderSql(String sql, String where, String startTime, String endTime, List<EtlAllCompany> companyList, boolean isIncrement) {
        String readerWhere = StringUtils.substringAfter(sql, Constant.WHERE_SQL);
        List<String> readerSqlList = Lists.newArrayList();
        for (EtlAllCompany company : companyList) {
            String readerSql = generateReaderSql(sql, company.getCompanyUuid(), company.getFranchiseeUuid(), company.getDbName());
            String sTime = startTime;
            if (company.getFullSynchronize() || !isIncrement) {
                readerSqlList.add(readerSql + " where " + readerWhere);
            } else {
                readerSqlList.add(readerSql + " " + updateWhere(where, sTime, endTime));
            }
        }
        return readerSqlList;
    }

    /**
     * 生成 读数据的sql 不包含where 条件
     *
     * @param sql
     * @param companyUuid
     * @param franchiseeUuid
     * @param dbName
     * @return
     */
    private static String generateReaderSql(String sql, String companyUuid, String franchiseeUuid, String dbName) {
        if (StringUtils.isBlank(companyUuid)) {
            return updateSql(sql, dbName);
        }

        String tableName = StringUtils.substringBetween(sql, Constant.FROM_SQL, Constant.WHERE_SQL).trim();
        String columns = StringUtils.substringBetween(sql, Constant.SELECT_SQL, Constant.FROM_SQL);

        StringBuffer readSql = new StringBuffer(Constant.SELECT_SQL)
                .append(" companyUuid='").append(franchiseeUuid).append("',")
                .append("externalCompanyUuid='").append(companyUuid).append("',")
                .append(columns).append(Constant.BLANK_SQL).append(Constant.FROM_SQL)
                .append(Constant.BLANK_SQL).append(dbName).append(Constant.SPACE_SQL)
                .append(tableName);

        return readSql.toString();
    }

    private static String updateSql(String sql, String dbName) {
        String sqlMain = StringUtils.substringBefore(sql, Constant.FROM_SQL);
        String tableName = StringUtils.substringBetween(sql, Constant.FROM_SQL, Constant.WHERE_SQL).trim();
        StringBuffer readSql = new StringBuffer(sqlMain)
                .append(Constant.BLANK_SQL).append(Constant.FROM_SQL)
                .append(Constant.BLANK_SQL).append(dbName).append(Constant.SPACE_SQL)
                .append(tableName);

        return readSql.toString();
    }

    /**
     * 奇数的为startTime,偶数的为endTime
     */
    private static String updateWhere(String where, String startTime, String endTime) {
        int count = containsCount(where, "%s");
        String[] params = new String[count];
        for (int i = 0; i < count; i++) {
            if (i % 2 == 0) {
                params[i] = "'" + startTime + "'";
            } else {
                params[i] = "'" + endTime + "'";
            }
        }
        return String.format(where, params);
    }

    private static int containsCount(String str, String s) {
        int count = StringUtils.split(str, s).length;
        if (StringUtils.startsWith(str, s)) {
            count++;
        }
        if (StringUtils.endsWith(str, s)) {
            count++;
        }
        return count - 1;
    }


    /**
     * 生成datax需要的json文件
     *
     * @param jobMap
     */
    public static void writeDatax(DataxJobMap jobMap) {
        Map<String, DataxJob> map = jobMap.getDataxJobMap();
        Set<Map.Entry<String, DataxJob>> set = map.entrySet();
        log.info("总任务数为:" + set.size());
        for (Map.Entry<String, DataxJob> entry : set) {
            String fileName = entry.getKey() + Constant.JOB_SUFFIX;
            File file = new File(PropertyUtils.getValue(PropertyUtils.JSON_FILE_DIR) + File.separator + fileName);
            String dataxJson = JSONObject.toJSONString(entry.getValue());
            log.info("start write datax json : {}", file.getAbsolutePath());
            MyIOUtils.write(file, dataxJson, null);
        }
    }
}
