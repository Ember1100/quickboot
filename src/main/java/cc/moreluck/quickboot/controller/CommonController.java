package cc.moreluck.quickboot.controller;

import cc.moreluck.quickboot.annotation.DistributeLock;
import cc.moreluck.quickboot.domain.User;
import cc.moreluck.quickboot.domain.WorkItem;
import cc.moreluck.quickboot.service.ExcelService;
import cc.moreluck.quickboot.utils.TTLUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    private final AtomicInteger taskCounter = new AtomicInteger(0);

    @Autowired
    private ExecutorService ttlExecutorService;
    @Autowired
    private Executor ttlExecutor;
    @Autowired
    private ExcelService excelService;

    @RequestMapping("/getHello")
    public String get(String param){
        return "hello";
    }


    @DistributeLock(scene = "1", key = "2")
    @RequestMapping("/getCache")
    public String getCache(Object param){
        log.info("param:{}",param);
        return "hello";
    }



    @GetMapping("/process")
    public String processRequest(@RequestParam("taskName") String taskName) throws InterruptedException {
        // 设置当前请求的上下文
        CountDownLatch countDownLatch = new CountDownLatch(3);
        // 提交任务到线程池
        for (int i = 0; i < 3; i++) {
            int subTaskId = i + 1;
            ttlExecutorService.submit(() -> {
                // 模拟子任务读取上下文
                User user = TTLUtil.ttl.get();
                log.info("{} executing SubTask-{} with context: {}", Thread.currentThread().getName(), subTaskId, user);
                // 模拟任务处理
                try {
                    Thread.sleep(200); // 模拟耗时任务
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }finally {
                    countDownLatch.countDown();
                }
            });
            ttlExecutor.execute(() -> {
                log.info("{} executing SubTask-{} with context: {}", Thread.currentThread().getName(), subTaskId, TTLUtil.ttl.get());
            });
        }
        countDownLatch.await();
        return "Task submitted: " + TTLUtil.ttl.get();
    }

    @PostMapping("/processExcel")
    public List<WorkItem> processExcel(@RequestParam String filePath) {
        List<WorkItem> workItems = excelService.readExcel(filePath);
        return excelService.buildHierarchy(workItems);
    }
}
