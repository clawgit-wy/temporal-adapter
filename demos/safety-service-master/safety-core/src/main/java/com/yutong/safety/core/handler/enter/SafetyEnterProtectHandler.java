package com.yutong.safety.core.handler.enter;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterProtect;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterProtectRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterProtectSh;

import java.util.Map;

public interface SafetyEnterProtectHandler {
    /**
     * 查询
     * */
    PageVO selectEnterProtectPage(SafetyEnterProtectSh enterProtectSh);
    /**
     * 新增
     * */
    void insertEnterProtect(SafetyEnterProtect enterProtect, String token);
    /**
     * 删除
     * */
    void deleteEnterProtect(Integer id, String token);
    /**
     * 导出
     * */
    byte[] exportEnterProtect(SafetyEnterProtectSh enterProtectSh);

    Map getSearchParams();

    SafetyEnterProtectRt getEnterProtectByCarNo(String carNo);
}
