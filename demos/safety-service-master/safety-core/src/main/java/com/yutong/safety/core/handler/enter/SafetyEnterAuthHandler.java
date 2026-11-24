package com.yutong.safety.core.handler.enter;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterAuth;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterAuthSh;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryMethodParams;

import java.util.List;
import java.util.Map;

public interface SafetyEnterAuthHandler {
    /**
     * 查询
     * */
    PageVO selectEnterAuthPage(SafetyEnterAuthSh enterAuthSh);
    SafetyEnterAuthRt selectEnterAuthInfo(Integer id);
    List selectEnterAuth(String token);
    List selectEnterAuthDict();
    /**
     * 新增
     * */
    void insertEnterAuth(SafetyEnterAuth enterAuth, String token);
    /**
     * 编辑
     * */
    void updateEnterAuth(SafetyEnterAuth enterAuth, String token);
    /**
     * 删除
     * */
    void deleteEnterAuth(Integer id, String token);

    List selectAuthFactory(String token, String empNo, String filingId);

    Map getSearchParams();

    /**
     * 校验员工信息
     * */
    SafetyEnterAuthRt checkAuthEmpInfo(String empNo, String phone);

    SafetyEnterAuthRt checkAuthEmpInfoNew(String empNo, String phone,Boolean enterFlag);

    /**
     * 设置组织结构ID
     * @param id
     * @param powerOrgCode
     * @param userName
     * @return
     */
    int updatePowerOrg(Integer id,String powerOrgCode,String userName);
}
