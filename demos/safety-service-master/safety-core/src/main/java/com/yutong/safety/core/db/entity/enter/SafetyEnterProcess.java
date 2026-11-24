package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物流车入场流程
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Integer id;

    /**
     * 流程ID
     */
    private String flowId;

    /**
     * 流程主题
     */
    private String title;

    /**
     * 流程提交人
     */
    private String creator;

    /**
     * 流程创建时间
     */
    private Date createTime;

    /**
     * 流程发布时间
     */
    private Date publishTime;

    /**
     * 流程状态
     */
    private String status;

    private String modelId;

    private String templateFormId;

    private String formInstanceId;


}
