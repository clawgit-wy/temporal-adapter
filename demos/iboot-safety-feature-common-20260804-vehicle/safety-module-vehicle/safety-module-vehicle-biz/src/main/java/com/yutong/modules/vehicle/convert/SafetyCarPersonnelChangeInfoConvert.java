package com.yutong.modules.vehicle.convert;

import com.yutong.modules.vehicle.dto.YTSafetyCarPersonnelChangeInfoDTO;
import com.yutong.modules.vehicle.entity.YTSafetyCarPersonnelChangeInfo;
import com.yutong.modules.vehicle.vo.YTSafetyCarPersonnelChangeInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SafetyCarPersonnelChangeInfoConvert {

    //将 DTO 的值 复制 到已有的实体中（你要的就是这个）
    void dtoToEntity(YTSafetyCarPersonnelChangeInfoDTO dto, @MappingTarget YTSafetyCarPersonnelChangeInfo entity);

    YTSafetyCarPersonnelChangeInfoVO entityToVo(YTSafetyCarPersonnelChangeInfo entity);

    List<YTSafetyCarPersonnelChangeInfoVO> entityListToVoList(List<YTSafetyCarPersonnelChangeInfo> entity);
}
