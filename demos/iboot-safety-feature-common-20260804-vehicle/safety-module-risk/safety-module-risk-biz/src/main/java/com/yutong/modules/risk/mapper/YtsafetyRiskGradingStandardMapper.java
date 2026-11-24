package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.entity.YtsafetyRiskGradingStandard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 风险分级标准表 Mapper
 * @Author: jeecg-boot
 * @Version: V1.0
 */
@Mapper
public interface YtsafetyRiskGradingStandardMapper extends BaseMapper<YtsafetyRiskGradingStandard> {

    /**
     * 根据适用类型和公式查询所有分级标准
     * @param riskType 适用类型：1安全，2消防
     * @param formula 公式：1 P*C，2 F*P*C
     * @return 分级标准列表
     */
    List<YtsafetyRiskGradingStandard> selectByRiskTypeAndFormula(@Param("type") Integer type, @Param("riskType") Integer riskType,
                                                                  @Param("formula") Integer formula);
}
