package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingHistory;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPerHistory;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingHistoryRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingSh;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SafetyEnterFilingHistoryMapper {

    List<SafetyEnterFilingHistoryRt> selectEnterFilingHistoryList(SafetyEnterFilingSh enterFilingSh);
    Integer selectEnterFilingHistoryCount(SafetyEnterFilingSh enterFilingSh);
    SafetyEnterFilingHistoryRt selectEnterFilingForHistory(@Param("filingId") String filingId);
    SafetyEnterFilingHistoryRt selectEnterFilingHistoryById(@Param("id") Integer id);
    List<SafetyEnterFilingPer> selectEnterFilingPerHistoryList(@Param("id") Integer id);
    Integer insertEnterFilingHistory(SafetyEnterFilingHistory enterFilingHistory);
    Integer insertEnterFilingPerHistory(List<SafetyEnterFilingPerHistory> list);

    List<SafetyEnterFilingDock> selectEnterFilingDockHis(@Param("phone") String phone);

}
