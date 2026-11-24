package com.yutong.modules.risk.convert;


import com.yutong.modules.risk.dto.YTSafetyTaskInfoDTO;
import com.yutong.modules.risk.entity.YTSafetyTaskInfo;
import org.mapstruct.Mapper;

/**
 * 任务信息转换
 */
@Mapper(componentModel = "spring")
public interface SafetyTaskInfoConvert {

    // DTO转Entity
    YTSafetyTaskInfo dtoToEntity(YTSafetyTaskInfoDTO dto);
}
