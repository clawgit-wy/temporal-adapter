package com.yutong.modules.vehicle.service.impl;

import com.yutong.modules.entity.SafetyVisitorAuthorization;
import com.yutong.modules.vehicle.mapper.SafetyVisitorAuthorizationJobMapper;
import com.yutong.modules.vehicle.service.ISafetyVisitorAuthorizationJob;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 访客授权配置
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Service
public class SafetyVisitorAuthorizationJob extends ServiceImpl<SafetyVisitorAuthorizationJobMapper, SafetyVisitorAuthorization> implements ISafetyVisitorAuthorizationJob {

}
