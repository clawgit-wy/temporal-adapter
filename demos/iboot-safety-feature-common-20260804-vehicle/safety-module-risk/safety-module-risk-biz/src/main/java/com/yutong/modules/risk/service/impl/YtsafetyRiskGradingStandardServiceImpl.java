package com.yutong.modules.risk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.risk.entity.YtsafetyRiskGradingStandard;
import com.yutong.modules.risk.mapper.YtsafetyRiskGradingStandardMapper;
import com.yutong.modules.risk.service.IYtsafetyRiskGradingStandardService;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.api.IYtsafetySysDictItemService;
import com.yutong.modules.user.entity.SafetyDictItem;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 风险分级标准表 Service 实现
 * @Author: jeecg-boot
 * @Version: V1.0
 */
@Service
public class YtsafetyRiskGradingStandardServiceImpl
        extends ServiceImpl<YtsafetyRiskGradingStandardMapper, YtsafetyRiskGradingStandard>
        implements IYtsafetyRiskGradingStandardService {

    @Autowired
    private YtsafetyRiskGradingStandardMapper riskGradingStandardMapper;

    @Autowired
    private IYtsafetySysDictItemService sysDictItemService;

    @Autowired
    private ISafetyFieldManageService safetyFieldManageService;

    @Override
    public List<YtsafetyRiskGradingStandard> listByRiskTypeAndFormula(Integer type, Long riskTypeId, Long riskCategoryId) {
        if (Objects.isNull(riskTypeId) || Objects.isNull(riskCategoryId)){
            return Lists.newArrayList();
        }
        // 1. 根据风险类型id查字典表，获取 item_code
        SafetyDictItem dictItem = sysDictItemService.getById(riskTypeId);
        if (dictItem == null) {
            throw new IllegalArgumentException("风险类型不存在，id=" + riskTypeId);
        }

        // 2. 根据 item_code 确定 risk_type
        Integer riskType;
        String itemCode = dictItem.getItemCode();
        if ("ONE_FIRE_PC".equals(itemCode)) {
            riskType = 2; // 消防
        } else if ("ONE_SECURITY_PC".equals(itemCode)) {
            riskType = 1; // 安全
        } else {
            throw new IllegalArgumentException("不支持的 item_code：" + itemCode);
        }

        // 3. 根据风险类别id查字段管理表，获取 field_code
        SafetyDictItem fieldManage = sysDictItemService.getById(riskCategoryId);
        if (fieldManage == null) {
            throw new IllegalArgumentException("风险类别不存在，id=" + riskCategoryId);
        }

        // 4. 根据 field_code 后缀确定 formula
        Integer formula;
        String fieldCode = fieldManage.getItemCode();
        if (fieldCode != null && fieldCode.endsWith("FPC")) {
            formula = 2; // F*P*C
        } else {
            formula = 1; // P*C（默认）
        }

        // 5. 查询分级标准
        return riskGradingStandardMapper.selectByRiskTypeAndFormula(type, riskType, formula);
    }
}
