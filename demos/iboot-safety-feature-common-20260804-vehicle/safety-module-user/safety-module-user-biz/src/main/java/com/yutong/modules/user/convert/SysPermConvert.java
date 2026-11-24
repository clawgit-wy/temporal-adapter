package com.yutong.modules.user.convert;

import com.yutong.modules.user.dto.SafetySysPermDTO;
import com.yutong.modules.user.entity.SafetySysPerm;
import com.yutong.modules.user.vo.SafetySysPermVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SysPermConvert {

    SafetySysPerm toVO(SafetySysPermDTO dto);

    List<SafetySysPermVO> toVOList(List<SafetySysPerm> list);
}
