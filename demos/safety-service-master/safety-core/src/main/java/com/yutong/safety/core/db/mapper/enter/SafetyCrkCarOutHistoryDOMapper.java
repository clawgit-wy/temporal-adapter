package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyCrkCarOutHistoryDO;
import com.yutong.safety.core.db.entity.enter.SafetyCrkCarOutHistoryDOExample;
import java.util.List;

import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingReportRt;
import com.yutong.safety.core.db.entity.enter.result.SatefyEnterStatisticRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyCrkCarOutHistorySh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingReportSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterStatisticSh;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SafetyCrkCarOutHistoryDOMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SafetyCrkCarOutHistoryDO record);

    SafetyCrkCarOutHistoryDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(SafetyCrkCarOutHistoryDO record);

    int selectCount(SafetyCrkCarOutHistorySh crkCarOutHistorySh);

    List<SafetyCrkCarOutHistoryDO> selectList(SafetyCrkCarOutHistorySh crkCarOutHistorySh);

    int updateBatch(@Param("historyList") List<SafetyCrkCarOutHistoryDO> historyDOS);

    List<SafetyEnterFilingReportRt> selectFilingReportList(SafetyEnterFilingReportSh reportSh);

    int selectFilingReportCount(SafetyEnterFilingReportSh reportSh);

    int selectFilingNum(SafetyEnterStatisticSh statisticSh);

    List<SatefyEnterStatisticRt> selectEnterCarNum(SafetyEnterStatisticSh statisticS);

    //未入场、在场
    List<Integer> selectHistory();
    List<SafetyEnterFilingReportRt> selectFilingReportListWrcOrZc(SafetyEnterFilingReportSh reportSh);
    int selectFilingReportCountWrcOrZc(SafetyEnterFilingReportSh reportSh);

    //已出场
    List<SafetyEnterFilingReportRt> selectFilingReportListWrcYcc(SafetyEnterFilingReportSh reportSh);
    int selectFilingReportCountWrcYcc(SafetyEnterFilingReportSh reportSh);
}