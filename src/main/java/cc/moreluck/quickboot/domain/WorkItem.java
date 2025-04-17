package cc.moreluck.quickboot.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
@Data
public class WorkItem {
    @ExcelProperty("主键ID（用于和子表进行关联）")
    private String id;

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("层级")
    private String level;

    @ExcelProperty("编号")
    private String code;

    @ExcelProperty("父ID")
    private String parentId;
}
