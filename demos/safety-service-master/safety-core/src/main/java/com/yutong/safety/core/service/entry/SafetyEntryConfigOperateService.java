package com.yutong.safety.core.service.entry;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigOperateDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigOperateDOExample;
import com.yutong.safety.core.db.entity.entry.param.ConfigOperateQueryParams;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryConfigOperateDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class SafetyEntryConfigOperateService
        extends AbstractSafetyWebBaseService<SafetyEntryConfigOperateDO, SafetyEntryConfigOperateDOExample, ConfigOperateQueryParams> {

    @Resource
    private SafetyEntryConfigOperateDOMapper safetyEntryConfigOperateDOMapper;
    @Override
    protected SafetyWebBaseMapper<SafetyEntryConfigOperateDO, SafetyEntryConfigOperateDOExample> getMapper() {
        return safetyEntryConfigOperateDOMapper;
    }
    @Override
    protected void formatExample(SafetyEntryConfigOperateDOExample example,
                                 ConfigOperateQueryParams param, boolean pageType) {
        example.setOrderByClause(" create_time desc");
        SafetyEntryConfigOperateDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(param.getRecordTypeCode())) {
            criteria.andRecordTypeCodeEqualTo(param.getRecordTypeCode());
        }
        if (StringUtils.isNotBlank(param.getEnterFactoryCode())) {
            criteria.andEnterFactoryCodeEqualTo(param.getEnterFactoryCode());
        }
        if (StringUtils.isNotBlank(param.getEnterStartTime())) {
            criteria.andEnterStartTimeGreaterThanOrEqualTo(param.getEnterStartTime());
        }
        if (StringUtils.isNotBlank(param.getEnterEndTime())) {
            criteria.andEnterEndTimeLessThanOrEqualTo(param.getEnterEndTime());
        }
        if (StringUtils.isNotBlank(param.getName())) {
            criteria.andNameEqualTo(param.getName());
        }
        if (StringUtils.isNotBlank(param.getCardNo())) {
            List<String> list = Arrays.asList(param.getCardNo().split(","));
            criteria.andCardNoIn(list);
        }
        if (StringUtils.isNotBlank(param.getPhone())) {
            criteria.andPhoneEqualTo(param.getPhone());
        }
        if (StringUtils.isNotBlank(param.getCompanyName())) {
            criteria.andCompanyNameLike("%" + param.getCompanyName() + "%");
        }
        if (StringUtils.isNotBlank(param.getStatus())) {
            criteria.andStatusEqualTo(param.getStatus());
        }
        if (StringUtils.isNotBlank(param.getRemark())) {
            criteria.andRemarkLike("%" + param.getRemark() + "%");
        }
        if(StringUtils.isNotBlank(param.getCreateTime())){
            String curCreateTime = param.getCreateTime();
            if(param.getCreateTime().length()<19){
                curCreateTime = param.getCreateTime().substring(0,10)+" 00:00:00";
            }
            criteria.andCreateTimeGreaterThanOrEqualTo(DateUtils.parse(curCreateTime,2));
        }
        if(pageType){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        if(StringUtils.isNotBlank(param.getOrderByClause())){
            example.setOrderByClause(param.getOrderByClause());
        }
    }

    @Override
    protected void batchDelete(SafetyEntryConfigOperateDOExample example, List<String> idList) {

    }

    @Override
    protected void isExist(SafetyEntryConfigOperateDO data, SafetyEntryConfigOperateDOExample example) {

    }

    public void save(SafetyEntryConfigOperateDO data) {
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        data.setUpdateBy(data.getCreateBy());
        data.setUpdateByNo(data.getUpdateByNo());
        this.safetyEntryConfigOperateDOMapper.insert(data);
    }

    public void batchSave(List<SafetyEntryConfigOperateDO> dataList){
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        List<List<SafetyEntryConfigOperateDO>> list = ListUtils.partition(dataList, 1000);
        list.forEach(li -> safetyEntryConfigOperateDOMapper.batchSave(li));
    }
}
