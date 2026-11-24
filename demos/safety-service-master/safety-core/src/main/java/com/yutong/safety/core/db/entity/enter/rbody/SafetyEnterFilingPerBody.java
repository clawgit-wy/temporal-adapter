package com.yutong.safety.core.db.entity.enter.rbody;

import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 物流车备案信息表
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterFilingPerBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userCode;
    private String userName;
    private String filingId;

    private List<SafetyEnterFilingPer> per;

}
