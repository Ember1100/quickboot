package cc.moreluck.quickboot.aspect;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MDCThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    /**
     * @param corePoolSize
     * @param maxPoolSize
     * @param keepAliveTime
     * @param queueCapacity
     * @param poolNamePrefix
     */

    public MDCThreadPoolTaskExecutor(int corePoolSize, int maxPoolSize,
                                     int keepAliveTime, int queueCapacity, String poolNamePrefix) {
        setCorePoolSize(corePoolSize);
        setMaxPoolSize(maxPoolSize);
        setKeepAliveSeconds(keepAliveTime);
        setQueueCapacity(queueCapacity);
        setThreadNamePrefix(poolNamePrefix);
    }

    public MDCThreadPoolTaskExecutor() {
        super();
    }

    /**
     * @return
     */
    private Map<String, String> getContextForTask() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * All executions will have MDC injected. {@code ThreadPoolExecutor}'s submission methods ({@code submit()} etc.)
     * all delegate to this.
     */
    @Override
    public void execute(Runnable command) {
        super.execute(wrap(command, getContextForTask()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task, getContextForTask()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(wrap(task, getContextForTask()));
    }

    /**
     * @param task
     * @param context
     * @param <T>
     * @return
     */
    private static <T> Callable<T> wrap(final Callable<T> task, final Map<String, String> context) {
        return () -> {
            if (context != null && !context.isEmpty()) {
                MDC.setContextMap(context);
            }

            try {
                return task.call();
            } finally {
                if (context != null && !context.isEmpty()) {
                    MDC.clear();
                }
            }
        };
    }

    /**
     * @param runnable
     * @param context
     * @return
     */
    private static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context != null && !context.isEmpty()) {
                MDC.setContextMap(context);
            }

            try {
                runnable.run();
            } finally {
                if (context != null && !context.isEmpty()) {
                    MDC.clear();
                }
            }
        };
    }

}
