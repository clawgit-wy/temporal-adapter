package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 物流车备案信息表
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterFilingAppSh extends Page implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    private String phoneNo;

}
