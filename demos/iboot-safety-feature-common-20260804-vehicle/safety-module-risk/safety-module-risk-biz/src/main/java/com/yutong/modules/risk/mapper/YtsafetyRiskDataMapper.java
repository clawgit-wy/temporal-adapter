package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YtsafetyRiskDataQueryDTO;
import com.yutong.modules.risk.entity.YtsafetyRiskData;
import com.yutong.modules.risk.vo.YtsafetyRiskDataExportVO;
import com.yutong.modules.risk.vo.YtsafetyRiskDataVO;
import com.yutong.modules.risk.vo.YtsafetyTaskRiskDataVO;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.common.aspect.annotation.DataPermission;

import java.util.List;

/**
 * @Description: 风险数据表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
public interface YtsafetyRiskDataMapper extends BaseMapper<YtsafetyRiskData> {

    @DataPermission(permCode="risk:riskdata")
    IPage<YtsafetyRiskDataVO> queryPageList(Page<YtsafetyRiskDataVO> page,
                                                @Param(Constants.WRAPPER) Wrapper<YtsafetyRiskData> wrapper,
                                                @Param("query") YtsafetyRiskDataQueryDTO  query);

    @DataPermission(permCode="risk:riskdata")
    List<YtsafetyRiskDataVO> queryList(@Param(Constants.WRAPPER) Wrapper<YtsafetyRiskData> wrapper,
                                             @Param("query") YtsafetyRiskDataQueryDTO query);

    YtsafetyRiskDataVO queryByDataId(@Param("id") Long id);

    /**
     * 根据组织编码+角色编码查询安全责任人名称
     * 当前组织查不到责任人时向上级组织递归查找，取组织路径上最近一级有责任人的部门，多人逗号分隔
     */
    String queryResponsibleNames(@Param("orgCode") String orgCode, @Param("roleCode") String roleCode);

}
