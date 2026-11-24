package com.yutong.modules.risk.service;

import com.yutong.modules.risk.entity.YtsafetyRiskGradingStandard;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 风险分级标准表 Service 接口
 * @Author: jeecg-boot
 * @Version: V1.0
 */
public interface IYtsafetyRiskGradingStandardService extends IService<YtsafetyRiskGradingStandard> {

    /**
     * 根据风险类型id和风险类别id查询所有分级标准
     *
     * 通过风险类型id查询字典 item_code 判断：
     *   ONE_FIRE_PC → 消防（risk_type=2）
     *   ONE_SECURITY_PC → 安全（risk_type=1）
     * 通过风险类别id查询字段编码后缀判断公式：
     *   后缀 FPC → formula=2（F*P*C）
     *   后缀 PC 或其他 → formula=1（P*C）
     *
     * @param riskTypeId     风险类型id（safety_dict_item 表）
     * @param riskCategoryId 风险类别id（safety_field_manage 表）
     * @return 分级标准列表
     */
    List<YtsafetyRiskGradingStandard> listByRiskTypeAndFormula(Integer type, Long riskTypeId, Long riskCategoryId);
}
