package com.yutong.modules.risk.service;

import com.yutong.modules.risk.entity.YTSafetyRiskLevelDict;

/**
 * @Description: 风险等级字典表 Service 接口
 * @Author: jeecg-boot
 * @Version: V1.0
 */
public interface IYTSafetyRiskLevelDictService {

    /**
     * 根据风险暴露频率、风险可能性、后果严重性计算风险值，并匹配对应的风险等级
     *
     * @param exposeFreq  风险暴露频率（数值）
     * @param possibility 风险可能性（数值）
     * @param severity    后果严重性（数值）
     * @return 匹配的风险等级字典记录
     *
     * 比对逻辑：
     * 三个数值相乘得到风险值，找到最大的 ≤ 乘积值的 risk_value 所对应的记录。
     * 例如 product=200，则匹配 risk_value=150 的"一般风险"记录。
     */
    YTSafetyRiskLevelDict calculateRiskLevel(Long exposeFreq, Long possibility, Long severity);

    /**
     * 根据风险暴露频率、风险可能性、后果严重性计算风险值，并匹配对应的风险等级
     *
     * @return 匹配的风险等级字典记录
     *
     * 比对逻辑：
     * 三个数值相乘得到风险值，找到最大的 ≤ 乘积值的 risk_value 所对应的记录。
     * 例如 product=200，则匹配 risk_value=150 的"一般风险"记录。
     */
    YTSafetyRiskLevelDict calculateRiskLevel(Double score);
}
