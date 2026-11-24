package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YTSafetyHazardLedgerQueryDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.vo.YTSafetyHazardLedgerVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportListVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 隐患上报表
 * @Author: jeecg-boot
 * @Date:   2026-06-26
 * @Version: V1.0
 */

public interface YTSafetyHazardReportMapper extends BaseMapper<YTSafetyHazardReport> {

    @Select("SELECT field_name FROM safety_field_manage WHERE id = #{id} AND delete_falg = 0 LIMIT 1")
    String selectFieldNameById(@Param("id") Long id);

    IPage<YTSafetyHazardReportListVO> historyList(@Param("page") Page<YTSafetyHazardReportListVO> page, @Param("empNo") String empNo, @Param("screeningCondition") String screeningCondition);

    IPage<YTSafetyHazardReportListVO> getFactoryHazardList(@Param("page") Page<YTSafetyHazardReportListVO> page, @Param("factoryList") List<Long> factoryList, @Param("screeningCondition") String screeningCondition);

    IPage<YTSafetyHazardReportListVO> getDraftList(@Param("page") Page<YTSafetyHazardReportListVO> page,  @Param("empNo") String empNo);

    IPage<YTSafetyHazardLedgerVO> getHazardLedgerList(@Param("page") Page<YTSafetyHazardLedgerVO> page, @Param("query") YTSafetyHazardLedgerQueryDTO query);

    List<YTSafetyHazardLedgerVO> exportHazardLedger(@Param("query") YTSafetyHazardLedgerQueryDTO query);

    YTSafetyHazardReportVO getByIdDetail(@Param("id")Long id);
}
