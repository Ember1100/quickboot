package cc.moreluck.quickboot.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 6333136319870641818L;

    private String name;

    private Integer age;
    private String id;


}
