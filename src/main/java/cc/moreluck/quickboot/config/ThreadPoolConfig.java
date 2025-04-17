package cc.moreluck.quickboot.config;


import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
@RequiredArgsConstructor
@Configuration
public class ThreadPoolConfig {
    /**
     * 核心线程数（默认线程数）
     */
    private static final int CORE_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors() + 1;

    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 128;

    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private static final int KEEP_ALIVE_TIME = 5;

    /**
     * 任务的等待时间
     */
    private static final int AWAIT_TERMINATION_TIME = 30;

    /**
     * 缓冲队列数
     */
    private static final int QUEUE_CAPACITY = 1200;

    /**
     * 线程池名前缀
     */
    private static final String THREAD_NAME_PREFIX = "boot-thread-pool";

    @Bean("ttlExecutor")
    public Executor threadPoolExecutor() {
        ThreadPoolExecutorMdcWrapper threadPoolExecutorMdcWrapper = new ThreadPoolExecutorMdcWrapper(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY));
        return TtlExecutors.getTtlExecutor(threadPoolExecutorMdcWrapper);
    }

    @Bean("ttlExecutorService")
    public ExecutorService ttlExecutorService() {
        // 创建普通的线程池
//        ExecutorService originalExecutor = Executors.newFixedThreadPool(10);

        // 包装线程池，支持 TransmittableThreadLocal 的上下文传递
        ThreadPoolExecutorMdcWrapper threadPoolExecutorMdcWrapper = new ThreadPoolExecutorMdcWrapper(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY));
        return TtlExecutors.getTtlExecutorService(threadPoolExecutorMdcWrapper);
    }
}