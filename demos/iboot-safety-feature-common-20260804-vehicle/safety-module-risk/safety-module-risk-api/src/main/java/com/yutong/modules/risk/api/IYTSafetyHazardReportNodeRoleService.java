package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.entity.YTSafetyHazardReportNodeRole;

import java.util.List;

/**
 * @Description: 隐患上报表流程节点配置角色
 * @Author: jeecg-boot
 * @Date:   2026-07-15
 * @Version: V1.0
 */
public interface IYTSafetyHazardReportNodeRoleService extends IService<YTSafetyHazardReportNodeRole> {

    List<YTSafetyHazardReportNodeRole> queryList(YTSafetyHazardReportNodeRole hazardReportNodeRole);

}
