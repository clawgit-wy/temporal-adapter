package com.yutong.modules.risk.convert;

import com.yutong.modules.risk.dto.YTSafetyHazardDescriptionDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardDescription;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SafetyHazardDescriptionConvert {

    //将 DTO 的值 复制 到已有的实体中（你要的就是这个）
    void dtoToEntity(YTSafetyHazardDescriptionDTO dto, @MappingTarget YTSafetyHazardDescription entity);

    List<YTSafetyHazardDescription> dtoListToEntityList(List<YTSafetyHazardDescriptionDTO> dtoList);
}
