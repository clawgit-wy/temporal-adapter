package com.yutong.modules.common.api.bpm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.entity.YtsafetyProcessNodeConfig;

import java.util.List;

/**
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date:   2026-07-23
 * @Version: V1.0
 */
public interface IYtsafetyProcessNodeConfigService extends IService<YtsafetyProcessNodeConfig> {

    /**
     * 获取节点审批人
     * @param modelFlag
     * @param nodeFlag
     * @param factoryId
     * @param orgCode
     * @return
     */
    public String queryApprovers(String modelFlag,String nodeFlag,String dataType,Long factoryId,String orgCode);


    /**
     *
     * @param orgCodes
     * @param roleCodes
     * @return
     */
    public String queryProposalApprovers(String modelFlag,String nodeFlag,String dataType,List<String> orgCodes);
}
