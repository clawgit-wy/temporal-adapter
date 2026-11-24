package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.vo.SafetyGroupInfoOptionVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 部门表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface SafetyGroupInfoMapper extends BaseMapper<SafetyGroupInfo> {

    @Select("select * from safety_group_info where status = 'Y'")
    List<SafetyGroupInfoOptionVO> getList();
}
