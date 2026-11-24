package com.yutong.modules.risk.mapper;

import com.yutong.modules.user.entity.YtsafetySysResponsible;
import org.apache.ibatis.annotations.Param;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcess;
import com.yutong.modules.risk.vo.YtsafetyRiskDataProcessVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 风险数据流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface YtsafetyRiskDataProcessMapper extends BaseMapper<YtsafetyRiskDataProcess> {

    YtsafetyRiskDataProcessVO queryById(@Param("id") Long id);

    List<YtsafetySysResponsible> queryResponsibleByDepartAndRole(@Param("departCode") String departCode, @Param("roleId") String roleId);

    List<YtsafetySysResponsible> queryResponsibleByDepartAndRoleCodes(@Param("departCode") String departCode, @Param("roleCodes") List<String> roleCodes);

    List<YtsafetySysResponsible> queryResponsibleByFactoryAndDepartAndRoleCodes(@Param("factoryId") Long factoryId, @Param("departCode") String departCode, @Param("roleCodes") List<String> roleCodes);

}
