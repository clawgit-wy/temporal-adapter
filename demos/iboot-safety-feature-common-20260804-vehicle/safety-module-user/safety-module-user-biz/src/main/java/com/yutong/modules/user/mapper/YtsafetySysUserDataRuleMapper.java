package com.yutong.modules.user.mapper;


import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 用户菜单数据规则表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
public interface YtsafetySysUserDataRuleMapper extends BaseMapper<YtsafetySysUserDataRule> {

    public List<YtsafetySysUserDataRule> getListByUserId(@Param("userId") Long userId, @Param("permCode") String permCode);

    public List<YtsafetySysUserDataRule> getColumnPermissionByUserId(@Param("userId") Long userId, @Param("ruleValue") String ruleValue,@Param("permCode") String permCode);
}
