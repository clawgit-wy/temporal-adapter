package com.yutong.modules.risk.convert;

import com.yutong.modules.risk.dto.YTSafetyHazardReportDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.vo.YTSafetyHazardReportVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SafetyHazardReportConvert {

    //将 DTO 的值 复制 到已有的实体中（你要的就是这个）
    void dtoToEntity(YTSafetyHazardReportDTO dto, @MappingTarget YTSafetyHazardReport entity);

    YTSafetyHazardReportVO entityToVo(YTSafetyHazardReport entity);
}
