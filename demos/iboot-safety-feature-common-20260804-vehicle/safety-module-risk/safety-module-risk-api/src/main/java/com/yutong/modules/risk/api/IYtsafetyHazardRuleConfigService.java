package com.yutong.modules.risk.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.entity.YtsafetyHazardRuleConfig;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigAddParam;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigEditParam;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigQueryParam;
import com.yutong.modules.risk.vo.YtsafetyHazardRuleConfigVo;

import java.util.List;

/**
 * @Description: 隐患规则配置
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
public interface IYtsafetyHazardRuleConfigService extends IService<YtsafetyHazardRuleConfig> {

    /**
     * 隐患规则配置列表
     * @param page
     * @param queryWrapper
     * @return
     */
    public IPage<YtsafetyHazardRuleConfigVo> queryVehicleFreezeConfigList(Page<YtsafetyHazardRuleConfig> page, YtsafetyHazardRuleConfigQueryParam queryWrapper);


    /**
     * 隐患规则配置新增
     * @param ytsafetyHazardRuleConfigAddParam
     * @return
     */
    public void saveHazardRuleConfig(YtsafetyHazardRuleConfigAddParam ytsafetyHazardRuleConfigAddParam);


    /**
     * 隐患规则配置修改
     * @param ytsafetyHazardRuleConfigEditParam
     * @return
     */
    public void updateHazardRuleConfig(YtsafetyHazardRuleConfigEditParam ytsafetyHazardRuleConfigEditParam);

    /**
     * 隐患规则配置删除
     * @param id
     * @return
     */
    public void removeRuleById(String id);

    /**
     * 隐患规则配置批量删除
     * @param ids
     * @return
     */
    public void removeRuleByIds(List<String> ids);




}
