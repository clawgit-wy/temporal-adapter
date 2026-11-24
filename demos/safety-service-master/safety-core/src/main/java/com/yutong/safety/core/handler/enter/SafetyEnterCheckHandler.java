package com.yutong.safety.core.handler.enter;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;

import java.util.List;

public interface SafetyEnterCheckHandler {
    /**
     * 校验登录用户是否在后台黑名单
     * */
    VO checkUserInBlack(String dockCode, String phoneNo);
    /**
     * 校验接口人
     * */
    void checkDockInfo(List<SafetyEnterFilingDock> dockList);
    /**
     * 校验车辆是否已备案
     * */
    int checkCarNo(String carNo);
    /**
     * 校验车辆是否已备案
     * */
    VO checkRecord(String phone);

    /**
     * 根据手机号查询登记详情
     * @param phone 手机号-唯一
     * @return 登记详情
     */
    VO showRegisterInfo(String phone);

    void checkDockInfoForNew(List<SafetyEnterFilingDock> dockList, Boolean enterFlag);
}
