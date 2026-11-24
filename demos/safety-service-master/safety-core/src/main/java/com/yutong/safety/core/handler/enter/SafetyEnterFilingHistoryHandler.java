package com.yutong.safety.core.handler.enter;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingHistoryRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingSh;

import java.util.List;

public interface SafetyEnterFilingHistoryHandler {
    /**
     * 查询
     * */
    PageVO selectEnterFilingHistoryPage(SafetyEnterFilingSh enterFilingSh, String token);
    SafetyEnterFilingHistoryRt selectEnterFilingHistoryById(Integer id);
    List<SafetyEnterFilingDock> selectEnterFilingDockHis(String phone);
    /**
     * 导出
     * */
    byte[] exportEnterFilingHistory(SafetyEnterFilingSh enterFilingSh, String token);
}
