package com.yutong.modules.risk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.risk.entity.YTSafetyRiskLevelDict;
import com.yutong.modules.risk.entity.YtsafetyRiskGradingStandard;
import com.yutong.modules.risk.mapper.YTSafetyRiskLevelDictMapper;
import com.yutong.modules.risk.mapper.YtsafetyRiskGradingStandardMapper;
import com.yutong.modules.risk.service.IYTSafetyRiskLevelDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 风险等级字典表 Service 实现
 * @Author: jeecg-boot
 * @Version: V1.0
 */
@Service
public class YTSafetyRiskLevelDictServiceImpl implements IYTSafetyRiskLevelDictService {

    @Autowired
    private YTSafetyRiskLevelDictMapper riskLevelDictMapper;


    @Autowired
    private YtsafetyRiskGradingStandardMapper riskGradingStandardMapper;

    @Override
    public YTSafetyRiskLevelDict calculateRiskLevel(Long exposeFreq, Long possibility, Long severity) {

        Map<Long, Double> map = riskGradingStandardMapper
                .selectList(new LambdaQueryWrapper<YtsafetyRiskGradingStandard>()
                        .in(YtsafetyRiskGradingStandard::getId, Arrays.asList(exposeFreq, possibility, severity)))
                .stream()
                .collect(Collectors.toMap(YtsafetyRiskGradingStandard::getId, YtsafetyRiskGradingStandard::getScore));

        // 计算风险值（乘积）
        double product = map.getOrDefault(exposeFreq, 1d) * map.getOrDefault(possibility,1d) * map.getOrDefault(severity,1d);

        // 查询所有风险等级字典（已按 risk_value 升序排列）
        List<YTSafetyRiskLevelDict> dictList = riskLevelDictMapper.selectAllOrderByRiskValue();

        if (dictList == null || dictList.isEmpty()) {
            throw new IllegalStateException("风险等级字典表数据为空");
        }

        // 找到最大的 ≤ product 的 risk_value 对应的记录
        YTSafetyRiskLevelDict result = dictList.get(0); // 默认取第一条（最低风险）
        for (YTSafetyRiskLevelDict dict : dictList) {
            if (product >= dict.getRiskValue()) {
                result = dict;
            } else {
                break; // 后续 risk_value 更大，无需继续遍历
            }
        }
        result.setRiskValue(product);

        return result;
    }

    @Override
    public YTSafetyRiskLevelDict calculateRiskLevel(Double score) {
        // 查询所有风险等级字典（已按 risk_value 升序排列）
        List<YTSafetyRiskLevelDict> dictList = riskLevelDictMapper.selectAllOrderByRiskValue();

        if (dictList == null || dictList.isEmpty()) {
            throw new IllegalStateException("风险等级字典表数据为空");
        }
        // 找到最大的 ≤ product 的 risk_value 对应的记录
        YTSafetyRiskLevelDict result = dictList.get(0); // 默认取第一条（最低风险）
        for (YTSafetyRiskLevelDict dict : dictList) {
            if (score > dict.getRiskValue()) {
                result = dict;
            } else {
                break; // 后续 risk_value 更大，无需继续遍历
            }
        }
        return result;
    }
}
