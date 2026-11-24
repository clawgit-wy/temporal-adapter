package com.yutong.modules.vehicle.service.impl;

import com.yutong.modules.entity.SafetyGeneralConfig;
import com.yutong.modules.vehicle.mapper.SafetyGeneralConfigJobMapper;
import com.yutong.modules.vehicle.service.ISafetyGeneralConfigJob;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 安全通用配置表
 * @Author: jeecg-boot
 * @Date:   2026-08-12
 * @Version: V1.0
 */
@Service
public class SafetyGeneralConfigJob extends ServiceImpl<SafetyGeneralConfigJobMapper, SafetyGeneralConfig> implements ISafetyGeneralConfigJob {

}
