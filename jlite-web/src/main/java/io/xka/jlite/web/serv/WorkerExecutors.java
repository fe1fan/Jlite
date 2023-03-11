package io.xka.jlite.web.serv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkerExecutors {

    private static final Logger logger = LoggerFactory.getLogger(WorkerExecutors.class);

    private static volatile ThreadPoolExecutor threadPoolExecutor;

    public static void init() {
        if (threadPoolExecutor == null) {
            synchronized (WorkerExecutors.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(
                            10,
                            100,
                            60,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(1000)
                    );
                }
            }
        }
    }

    public static void shutdown() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }
    }

    public static Future<?> submit(Runnable runnable) {
        Thread thread = new Thread(runnable);
        return threadPoolExecutor.submit(thread);
    }

    public synchronized static void dump() {
        logger.info("已经执行完任务的线程数：{}", threadPoolExecutor.getCompletedTaskCount());
        // 输出池中正在执行任务的线程数
        logger.info("正在执行任务的线程数：{}", threadPoolExecutor.getActiveCount());
        // 输出池的核心线程数
        logger.info("池的核心线程数：{}", threadPoolExecutor.getCorePoolSize());
        // 输出池的最大线程数
        logger.info("池的最大线程数：{}", threadPoolExecutor.getMaximumPoolSize());
        // 输出池中等待执行的任务数
        logger.info("池中等待执行的任务数：{}", threadPoolExecutor.getQueue().size());
        // 判断池是否已经关闭
        if (threadPoolExecutor.isShutdown()) {
            logger.info("池已经关闭。");
        }
        // 判断池中所有任务是否都已经执行完毕
        if (threadPoolExecutor.isTerminated()) {
            logger.info("池中所有任务都已执行完毕。");
        }
    }

}
