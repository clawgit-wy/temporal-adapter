package com.yutong.modules.risk.service;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.risk.api.IYtsafetyHazardRuleConfigService;
import com.yutong.modules.risk.entity.YtsafetyHazardRuleConfig;
import com.yutong.modules.risk.mapper.YtsafetyHazardRuleConfigMapper;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigAddParam;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigEditParam;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigQueryParam;
import com.yutong.modules.risk.vo.YtsafetyHazardRuleConfigVo;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 隐患规则配置
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public class YtsafetyHazardRuleConfigServiceImpl extends ServiceImpl<YtsafetyHazardRuleConfigMapper, YtsafetyHazardRuleConfig> implements IYtsafetyHazardRuleConfigService {
    /**
     * 隐患规则配置列表
     * @param page
     * @param queryParam
     * @return
     */
    public  IPage<YtsafetyHazardRuleConfigVo> queryVehicleFreezeConfigList(Page<YtsafetyHazardRuleConfig> page, YtsafetyHazardRuleConfigQueryParam queryParam){
        LambdaQueryWrapper<YtsafetyHazardRuleConfig> queryWrapper = new LambdaQueryWrapper<YtsafetyHazardRuleConfig>();
        queryWrapper.like(StrUtil.isNotBlank(queryParam.getRuleName()),YtsafetyHazardRuleConfig::getRuleName,queryParam.getRuleName());
        queryWrapper.like(StrUtil.isNotBlank(queryParam.getRuleCode()),YtsafetyHazardRuleConfig::getRuleCode,queryParam.getRuleCode());
        queryWrapper.eq(YtsafetyHazardRuleConfig::getDelFlag,0);
        IPage<YtsafetyHazardRuleConfig> pageList = this.page(page, queryWrapper);
        // 新建VO分页对象
        Page<YtsafetyHazardRuleConfigVo> voPage = new Page<>();
        BeanUtils.copyProperties(pageList, voPage, "records");

        List<YtsafetyHazardRuleConfigVo> voList = pageList.getRecords().stream()
                .map(entity -> {
                    YtsafetyHazardRuleConfigVo vo = new YtsafetyHazardRuleConfigVo();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    public void saveHazardRuleConfig(YtsafetyHazardRuleConfigAddParam addParam){
        LoginUser user = BusUtits.getLoginUser();
        LambdaQueryWrapper<YtsafetyHazardRuleConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(YtsafetyHazardRuleConfig::getRuleCode, addParam.getRuleCode())
                .or()
                .eq(YtsafetyHazardRuleConfig::getRuleName, addParam.getRuleName());
        List<YtsafetyHazardRuleConfig> existList = this.list(wrapper);

        for (YtsafetyHazardRuleConfig item : existList) {
            if(addParam.getRuleCode().equals(item.getRuleCode())){
                throw new RuntimeException("规则编码【" + addParam.getRuleCode() + "】已存在，请勿重复添加");
            }
            if(addParam.getRuleName().equals(item.getRuleName())){
                throw new RuntimeException("规则名称【" + addParam.getRuleName() + "】已存在，请勿重复添加");
            }
        }
        YtsafetyHazardRuleConfig  entity=new YtsafetyHazardRuleConfig();
        BeanUtils.copyProperties(addParam, entity);
        entity.setCreateUser(user.getId());
        entity.setCreateName(user.getRealname());
        entity.setCreateTime(new DateTime());
        this.save(entity);
    }


    /**
     * 隐患规则配置修改
     * @param editParam
     * @return
     */
    public void updateHazardRuleConfig(YtsafetyHazardRuleConfigEditParam editParam){
        YtsafetyHazardRuleConfig  entity=getById(editParam.getId());
        LambdaQueryWrapper<YtsafetyHazardRuleConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(YtsafetyHazardRuleConfig::getId, editParam.getId())
                .and(w -> w.eq(YtsafetyHazardRuleConfig::getRuleCode, editParam.getRuleCode())
                        .or()
                        .eq(YtsafetyHazardRuleConfig::getRuleName, editParam.getRuleName()));
        List<YtsafetyHazardRuleConfig> existList = this.list(wrapper);

        for (YtsafetyHazardRuleConfig item : existList) {
            if(editParam.getRuleCode().equals(item.getRuleCode())){
                throw new RuntimeException("规则编码【" + editParam.getRuleCode() + "】已存在，请勿重复添加");
            }
            if(editParam.getRuleName().equals(item.getRuleName())){
                throw new RuntimeException("规则名称【" + editParam.getRuleName() + "】已存在，请勿重复添加");
            }
        }
        if(entity!=null){
            BeanUtils.copyProperties(editParam, entity);
        }
        this.updateById(entity);
    }

    /**
     * 隐患规则配置删除
     * @param id
     * @return
     */
    public void removeRuleById(String id){
        YtsafetyHazardRuleConfig  entity=getById(id);
        if(entity!=null){
            entity.setDelFlag(1);
        }
        this.updateById(entity);
    }

    /**
     * 隐患规则配置批量删除
     * @param ids
     * @return
     */
    public void removeRuleByIds(List<String> ids){
        List<YtsafetyHazardRuleConfig> list=new ArrayList<>();
        for(String id:ids) {
            YtsafetyHazardRuleConfig  entity=getById(id);
            if (entity != null) {
                entity.setDelFlag(1);
                list.add(entity);
            }
        }
        if(!list.isEmpty()){
            this.updateBatchById(list);
        }
    }

}

