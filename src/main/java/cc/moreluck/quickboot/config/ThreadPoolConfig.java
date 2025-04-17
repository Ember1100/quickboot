package cc.moreluck.quickboot.config;


import lombok.RequiredArgsConstructor;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
@RequiredArgsConstructor
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


}