package com.yutong.modules.user.convert;

import com.yutong.modules.user.entity.SafetySysPerm;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.SafetySysRoleOptionListVO;
import com.yutong.modules.user.vo.SafetySysRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 角色表转换
 */
@Mapper(componentModel = "spring")
public interface SafetySysRoleConvert {
    //实体转vo
    SafetySysRoleVO entityToVo(SafetySysRole entity);

    //集合转换拉框VO
    List<SafetySysRoleOptionListVO> entityListToOptionListVO(List<SafetySysRole> list);

    // 集合转换（自动循环）
    List<MenuTree> safetySysPermToMenuTree(List<SafetySysPerm> list);

    // 单个转换
    @Mapping(target = "name", source = "permName")
    @Mapping(target = "pid", source = "parentId")
    MenuTree permToMenuTree(SafetySysPerm perm);
}
