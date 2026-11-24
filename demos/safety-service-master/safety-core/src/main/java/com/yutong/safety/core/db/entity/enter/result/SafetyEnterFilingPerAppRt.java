package com.yutong.safety.core.db.entity.enter.result;

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
public class SafetyEnterFilingPerAppRt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 司机
     */
    private SafetyEnterFilingPer driver;

    /**
     * 随行人员
     */
    private List<SafetyEnterFilingPer> per;
}
