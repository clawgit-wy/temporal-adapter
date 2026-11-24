package com.yutong.modules.user.convert;

import com.yutong.modules.user.dto.YTSafetySysJobRoleMappingDTO;
import com.yutong.modules.user.entity.YTSafetySysJobRoleMapping;
import com.yutong.modules.user.vo.YTSafetySysJobRoleMappingVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SysJobRoleMappingConvert {

    // 单例
    SysJobRoleMappingConvert INSTANCE = Mappers.getMapper(SysJobRoleMappingConvert.class);

    // 1. 单个对象转换
    YTSafetySysJobRoleMappingVO entityToVo(YTSafetySysJobRoleMapping ytSafetySysJobRoleMapping);

    // 2. 集合转换（自动循环）
    List<YTSafetySysJobRoleMappingVO> entityListToVoList(List<YTSafetySysJobRoleMapping> list);

    // 3.DTO 转 实体
    YTSafetySysJobRoleMapping dtoToEntity(YTSafetySysJobRoleMappingDTO dto);

    //将 DTO 的值 复制 到已有的实体中（你要的就是这个）
    void dtoToEntity(YTSafetySysJobRoleMappingDTO dto, @MappingTarget YTSafetySysJobRoleMapping entity);

}
