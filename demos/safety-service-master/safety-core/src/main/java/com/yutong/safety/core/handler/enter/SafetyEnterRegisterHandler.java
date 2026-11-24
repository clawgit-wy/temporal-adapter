package com.yutong.safety.core.handler.enter;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.enter.EnterFactoryRecord;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.entity.enter.SafetyEnterRegister;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterRegisterSh;

import java.util.List;

public interface SafetyEnterRegisterHandler {
    /**
     * 查询
     * */
    PageVO selectEnterRegisterPage(SafetyEnterRegisterSh enterRegister);

    SafetyEnterRegisterRt selectEnterRegisterByPhone(String phone, String userName);
    SafetyEnterRegisterRt selectEnterRegisterByCard(String card);

    /**
     * 新增
     * */
    void insertEnterRegister(List<EnterFactoryRecord> factoryRecords,String sessionKey);
    /**
     * 编辑
     * */
    void updateEnterRegister(SafetyEnterRegister enterRegister, String token);
    void updateEnterRegisterProcess(List<SafetyEnterFilingPer> perList, String updateUser);
    /**
     * 删除
     * */
    void deleteEnterRegister(Integer id,String delReason, String token);
    /**
     * 导出
     * */
    byte[] exportEnterRegister(SafetyEnterRegisterSh enterRegister);
    /**
     * 备案二维码
     * */
    String doorQrCode(String phoneNo, String idNo, String staffName, int[] nos, String crossType,String endTime,String crossData, String tcmid);

    SafetyEnterRegisterRt selectEnterRegisterByPhone(String phone);

    SafetyEnterRegisterRt selectInfo(String phone);
}
