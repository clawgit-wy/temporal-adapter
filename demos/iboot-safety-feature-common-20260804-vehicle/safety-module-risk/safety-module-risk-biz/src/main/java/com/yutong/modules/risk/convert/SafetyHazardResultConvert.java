package com.yutong.modules.risk.convert;

import com.yutong.modules.risk.dto.YTSafetyHazardResultDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SafetyHazardResultConvert {

    //将 DTO 的值 复制 到已有的实体中（你要的就是这个）
    void dtoToEntity(YTSafetyHazardResultDTO dto, @MappingTarget YTSafetyHazardResult entity);

    List<YTSafetyHazardResult> dtoListToEntityList(List<YTSafetyHazardResultDTO> dtoList);
}
