package cc.moreluck.quickboot;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentExample {
    public static void main(String[] args) throws InterruptedException {
        // 创建普通的线程池
        ExecutorService originalExecutor = Executors.newFixedThreadPool(3);

        // 包装线程池，支持 TransmittableThreadLocal 的上下文传递
        ExecutorService ttlExecutor = TtlExecutors.getTtlExecutorService(originalExecutor);

        // 创建 TransmittableThreadLocal 变量
        TransmittableThreadLocal<String> ttl = new TransmittableThreadLocal<>();

        // 模拟并发场景
        for (int i = 1; i <= 5; i++) {
            int taskId = i;

            // 设置父线程的上下文变量
            ttl.set("Task-" + taskId);

            // 提交任务到线程池
            ttlExecutor.submit(() -> {
                // 模拟子线程读取上下文变量
                String context = ttl.get();
                System.out.println(Thread.currentThread().getName() + " executing with context: " + context);

                // 模拟子线程任务处理
                try {
                    Thread.sleep(500); // 模拟任务耗时
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // 等待一段时间再提交下一个任务，模拟父线程上下文的切换
            Thread.sleep(100);
        }

        // 关闭线程池
        ttlExecutor.shutdown();
    }
}