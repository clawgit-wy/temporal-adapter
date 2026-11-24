package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoQueryDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.query.YtsafetyLocationInfoQuery;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoVO;
import org.apache.ibatis.annotations.Param;

import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import org.jeecg.common.aspect.annotation.DataPermission;

import java.util.List;

/**
 * @Description: 位置信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
public interface YtsafetyLocationInfoMapper extends BaseMapper<YtsafetyLocationInfo> {

    @DataPermission(permCode="risk:workStation")
    IPage<YtsafetyLocationInfoVO> queryPageList(Page<YtsafetyLocationInfoVO> page,
                                                @Param(Constants.WRAPPER) Wrapper<YtsafetyLocationInfo> wrapper,
                                                @Param("query") YtsafetyLocationInfoQueryDTO query);
    @DataPermission(permCode="risk:workStation")
    List<YtsafetyLocationInfoVO> queryList(@Param(Constants.WRAPPER) Wrapper<YtsafetyLocationInfo> wrapper,
                                           @Param("query") YtsafetyLocationInfoQueryDTO query);

    YtsafetyLocationInfoVO queryById(@Param("id") Long id);

    /**
     * 根据组织编码+角色编码查询安全责任人名称
     * 当前组织查不到责任人时向上级组织递归查找，取组织路径上最近一级有责任人的部门，多人逗号分隔
     */
    String queryResponsibleNames(@Param("orgCode") String orgCode, @Param("roleCode") String roleCode);

    void updateRiskLevel(@Param("list") List<YtsafetyLocationInfo> list);

}
