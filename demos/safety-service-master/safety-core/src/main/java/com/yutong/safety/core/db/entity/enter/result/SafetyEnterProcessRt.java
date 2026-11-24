package com.yutong.safety.core.db.entity.enter.result;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 物流车入场流程
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterProcessRt implements Serializable {

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

    private String modelId;

    private String templateFormId;

    private String formInstanceId;

    private String fdCarNo;
}
