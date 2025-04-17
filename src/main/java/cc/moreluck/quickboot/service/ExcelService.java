package cc.moreluck.quickboot.service;

import cc.moreluck.quickboot.domain.WorkItem;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
@Service
public class ExcelService {

    public List<WorkItem> readExcel(String filePath) {
        List<WorkItem> workItems = new ArrayList<>();

        EasyExcel.read(filePath, WorkItem.class, new ReadListener<WorkItem>() {
            @Override
            public void invoke(WorkItem workItem, AnalysisContext context) {
                workItems.add(workItem);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 解析完成后的处理
            }
        }).sheet().doRead();

        return workItems;
    }

    public List<WorkItem> buildHierarchy(List<WorkItem> tasks) {
        Map<String, List<WorkItem>> taskMap = new HashMap<>();
        List<WorkItem> rootTasks = new ArrayList<>();

        // 将任务根据 parentId 进行分组
        for (WorkItem task : tasks) {
            taskMap.computeIfAbsent(task.getParentId(), k -> new ArrayList<>()).add(task);
        }

        // 构建层级关系
        for (WorkItem task : tasks) {
            if (task.getParentId() == null || task.getParentId().isEmpty()) {
                rootTasks.add(task);
            } else {
                List<WorkItem> children = taskMap.get(task.getId());
                // 这里可以设置子项到父项
            }
        }
        return rootTasks;
    }
}
