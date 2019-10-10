package com.xqsight.etl;

import com.xqsight.etl.common.DataxJobMap;
import com.xqsight.etl.domain.EtlJobInfo;
import com.xqsight.etl.domain.EtlAllCompany;
import com.xqsight.etl.metadata.DataxExcuteLog;
import com.xqsight.etl.service.CompanyService;
import com.xqsight.etl.service.DataxService;
import com.xqsight.etl.service.JobInfoService;
import com.xqsight.etl.service.MergeService;
import com.xqsight.etl.util.JsonFileUtil;
import com.xqsight.etl.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Slf4j
public class VipEtlMain {

    public static final int CORE_THREADS = 10;

    public static ExecutorService executorService = null;

    public static TreeMap<Long, DataxExcuteLog> dataxExcuteLogListTreeMap = new TreeMap<>();


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        log.info("======================================任务开始==========================================");

        String dateBefore = args.length > 1 && StringUtils.isNotBlank(args[1]) ? args[1] : "0";
        log.info("dateBefore = {}", dateBefore);

        String channelNum = args.length > 2 && StringUtils.isNotBlank(args[2]) ? args[2] : "10";
        log.info("channelNum = {}", channelNum);

        String spiltNum = args.length > 2 && StringUtils.isNotBlank(args[3]) ? args[3] : "200";
        log.info("spiltNum = {}", spiltNum);
        start(dateBefore, channelNum, spiltNum);

        long costTime = System.currentTimeMillis() - startTime;
        log.info("整个任务耗时为:{}", costTime);
    }

    public static void start(String dateBefore, String channelNum, String spiltNum) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        try {
            // 开启线程池
            openPool();
            //数据库查询出的List
            List<EtlJobInfo> jobInfoList = queryJobInfoList(ctx, dateBefore, channelNum, spiltNum);
            // 计数器大小定义为集合大小，避免处理不一致导致主线程无限等待
            CountDownLatch countDownLatch = new CountDownLatch(jobInfoList.size());
            // 循环处理List
            for (EtlJobInfo jobInfo : jobInfoList) {
                // 任务提交线程池
                CompletableFuture.supplyAsync(() -> {
                    try {
                        // datax处理
                        dataxExec(jobInfo, ctx);
                    } finally {
                        countDownLatch.countDown();
                    }
                    return "";
                }, executorService);
            }
            // 主线程等待所有子线程都执行完成时，恢复执行主线程
            countDownLatch.await();
            merge(ctx);
        } catch (Exception e) {
            log.error("start发生了异常", e);
        } finally {
            // 关闭线程池
            closePool();
            ctx.destroy();
        }
    }

    public static List<EtlJobInfo> queryJobInfoList(ClassPathXmlApplicationContext ctx, String dateBefore, String channelNum, String spiltNum) {
        /** 获取同步的公司信息 */
        CompanyService companyService = ctx.getBean(CompanyService.class);
        Map<String, List<EtlAllCompany>> targetCompanyIpMap = companyService.getCompanyIpMap();

        /** 需要同步数据对应的读写库信息 */
        JobInfoService jobInfoService = ctx.getBean(JobInfoService.class);
        List<EtlJobInfo> jobInfoList = jobInfoService.getCanRunJob(dateBefore);
        log.info("jobInfo任务列表：{}", jobInfoList.size());

        /** 启动之前清空临时表中的数据 */
        MergeService mergeService = ctx.getBean(MergeService.class);
        mergeService.truncateTempTable();

        /** 同步数据表对应的json文件信息 */
        DataxService dataxService = ctx.getBean(DataxService.class);
        DataxJobMap dataxJobMap = dataxService.getDataxJobMap();
        log.info("datax 执行的条数{},对应的表名:{}", dataxJobMap.getSize(), dataxJobMap.getDataxJobMap().keySet());

        /** 生产json文件 */
        JsonFileUtil.makeDataxJsonFile(targetCompanyIpMap, jobInfoList, dataxJobMap, channelNum, spiltNum);
        return jobInfoList;
    }

    private static void dataxExec(EtlJobInfo jobInfo, ClassPathXmlApplicationContext ctx) {
        DataxExcuteLog dataxExcuteLog = new DataxExcuteLog();
        JobInfoService jobInfoService = ctx.getBean(JobInfoService.class);
        long startTime = System.currentTimeMillis();
        try {
            log.info("执行开始：{}", jobInfo.getJsonName());
            int state = jobInfoService.execute(jobInfo);
            log.info("执行完成：{};state={}", jobInfo.getJsonName(), state);
        } catch (Throwable e) {
            log.error("执行失败:{}", jobInfo.getJsonName(), e);
        }
        long costTime = System.currentTimeMillis() - startTime;
        dataxExcuteLog.setCost(costTime);
        dataxExcuteLog.setJobCommand(jobInfo.getJsonName());
        dataxExcuteLogListTreeMap.put(dataxExcuteLog.getCost(), dataxExcuteLog);
    }


    public static void merge(ClassPathXmlApplicationContext ctx) {
        for (Map.Entry<Long, DataxExcuteLog> dataxExcuteLogEntry : dataxExcuteLogListTreeMap.entrySet()) {
            log.warn("cost too long : " + dataxExcuteLogEntry.getValue().toString());
        }

        long startTime = System.currentTimeMillis();
        log.warn("开始merge");
        MergeService mergeService = ctx.getBean(MergeService.class);
        mergeService.merge();
        CompanyService companyService = ctx.getBean(CompanyService.class);
        companyService.fullEtlEnd();
        log.warn("结束merge");
        long costTime = System.currentTimeMillis() - startTime;
        log.info("merge耗时为:" + costTime);
    }

    /**
     * 开启线程池
     */
    public static void openPool() {
        if (null == executorService || executorService.isShutdown()) {
            executorService = ThreadPoolUtil.newThreadPool("MyThread", CORE_THREADS, CORE_THREADS);
        }
    }

    /**
     * 关闭线程池
     */
    public static void closePool() {
        if (executorService != null) {
            ThreadPoolUtil.shutdown(executorService);
        }
    }
}
