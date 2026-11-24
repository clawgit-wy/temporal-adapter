package com.yutong.safety.core.db.entity.enter.rbody;

import com.yutong.safety.core.db.entity.enter.SafetyEnterFiling;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.entity.enter.SafetyEnterProtect;
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
public class SafetyEnterFilingBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private String openId;
    // 用户手机号
    private String userCode;
    // 用户姓名
    private String userName;
    // 备案信息
    private SafetyEnterFiling filing;
    // 物流车环保信息
    private SafetyEnterProtect prt;
    // 接口人信息
    private List<SafetyEnterFilingDock> dock;
    // 备案人员信息
    private List<SafetyEnterFilingPer> per;

}
