package com.xqsight.etl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ThreadPoolUtil {

    public static Logger log = LoggerFactory.getLogger(ThreadPoolUtil.class);

    public static final int DEFAULT_CORE_THREADS = Runtime.getRuntime().availableProcessors();

    /**
     * 默认线程池创建 CPU核心线程数线程池
     *
     * @param threadPrefix 线程别名
     * @return
     */
    public static ExecutorService newThreadPool(String threadPrefix) {
        return doNewThreadPool(threadPrefix, DEFAULT_CORE_THREADS, DEFAULT_CORE_THREADS);
    }

    /**
     * @param threadPrefix 线程别名
     * @param coreThreads  核心线程数
     * @param maxThreads   最大线程数
     * @return
     */
    public static ExecutorService newThreadPool(String threadPrefix, int coreThreads, int maxThreads) {
        return doNewThreadPool(threadPrefix, coreThreads, maxThreads);
    }

    private static ExecutorService doNewThreadPool(final String threadPrefix, int coreThreads, int maxThreads) {
        /** 线程池阻塞队列 */
        final LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(1024);

        /** 创建线程池 */
        ExecutorService executorService = new ThreadPoolExecutor(coreThreads, maxThreads, 60, TimeUnit.MINUTES, blockingQueue, new ThreadFactory() {
            /** 创建线程 */
            final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(threadPrefix + "-" + counter.incrementAndGet());
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        }, new RejectedExecutionHandler() {
            /** 饱和策略 */
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                try {
                    blockingQueue.put(r);
                } catch (InterruptedException e) {
                    log.debug("饱和策略异常", e);
                }
            }
        });
        return executorService;
    }


    /**
     * 关闭线程池
     *
     * @param executorService
     */
    public static void shutdown(ExecutorService executorService) {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }

    }


}
