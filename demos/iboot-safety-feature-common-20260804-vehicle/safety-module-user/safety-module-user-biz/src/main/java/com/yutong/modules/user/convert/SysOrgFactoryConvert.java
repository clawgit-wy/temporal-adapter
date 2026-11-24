package com.yutong.modules.user.convert;

import com.yutong.modules.user.dto.YTSafetySysOrgFactoryDTO;
import com.yutong.modules.user.entity.YTSafetySysOrgFactory;
import com.yutong.modules.user.vo.YTSafetySysOrgFactoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SysOrgFactoryConvert {

    // 单例
    SysOrgFactoryConvert INSTANCE = Mappers.getMapper(SysOrgFactoryConvert.class);

    // 1. 单个对象转换
    YTSafetySysOrgFactoryVO entityToVo(YTSafetySysOrgFactory ytSafetySysOrgFactory);

    // 2. 集合转换（自动循环）
    List<YTSafetySysOrgFactoryVO> entityListToVoList(List<YTSafetySysOrgFactory> list);

    // 3.DTO 转 实体
    YTSafetySysOrgFactory dtoToEntity(YTSafetySysOrgFactoryDTO dto);

    //将 DTO 的值 复制 到已有的实体中（你要的就是这个）
    void dtoToEntity(YTSafetySysOrgFactoryDTO dto, @MappingTarget YTSafetySysOrgFactory entity);

}
