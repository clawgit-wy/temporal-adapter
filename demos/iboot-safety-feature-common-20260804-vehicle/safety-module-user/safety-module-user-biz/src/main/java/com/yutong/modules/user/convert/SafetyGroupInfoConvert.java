package com.yutong.modules.user.convert;


import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.vo.SafetyGroupInfoOptionListVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 组织表转换
 */
@Mapper(componentModel = "spring")
public interface SafetyGroupInfoConvert {

    // 单个对象转换
    SafetyGroupInfoOptionListVO entityToOptionListVo(SafetyGroupInfo safetyGroupInfo);

    // 集合转换（自动循环）
    List<SafetyGroupInfoOptionListVO>  entityToOptionListVoList(List<SafetyGroupInfo> list);
}
