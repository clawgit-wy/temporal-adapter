package com.yutong.safety.core.service.entry;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigHistoryDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigHistoryDOExample;
import com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants;
import com.yutong.safety.core.db.entity.entry.param.EntryConfigHistoryQueryParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryConfigBatchParams;
import com.yutong.safety.core.db.entity.entry.vo.EntryFactoryRecordVO;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryConfigHistoryDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SafetyEntryConfigHistoryService
        extends AbstractSafetyWebBaseService<SafetyEntryConfigHistoryDO, SafetyEntryConfigHistoryDOExample, EntryConfigHistoryQueryParams> {

    @Resource
    private SafetyEntryConfigService safetyEntryConfigService;
    @Resource
    private SafetyEntryConfigHistoryDOMapper safetyEntryConfigHistoryDOMapper;
    @Override
    protected SafetyWebBaseMapper<SafetyEntryConfigHistoryDO, SafetyEntryConfigHistoryDOExample> getMapper() {
        return safetyEntryConfigHistoryDOMapper;
    }

    @Override
    protected void formatExample(SafetyEntryConfigHistoryDOExample example,
                                 EntryConfigHistoryQueryParams param, boolean pageType) {
        example.setOrderByClause(" create_time desc");
        SafetyEntryConfigHistoryDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(param.getButtJoint())) {
            criteria.andButtJointCodeOrButtJointNameEqualTo(param.getButtJoint());
        }
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
        if(StringUtils.isNotBlank(param.getCreateByNo())){
            criteria.andCreateByNoEqualTo(param.getCreateByNo());
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
    protected void batchDelete(SafetyEntryConfigHistoryDOExample example, List<String> idList) {
        SafetyEntryConfigHistoryDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (!CollectionUtils.isEmpty(idList)) {
            List<Integer> list = idList.stream().map(Integer::parseInt).collect(Collectors.toList());
            criteria.andIdIn(list);
        }
    }

    @Override
    protected void isExist(SafetyEntryConfigHistoryDO data, SafetyEntryConfigHistoryDOExample example) {

    }

    public int save(SafetyEntryConfigHistoryDO data){
        Date curDate =  new Date();
        data.setCreateTime(curDate);
        data.setUpdateTime(curDate);
        return safetyEntryConfigHistoryDOMapper.insert(data);
    }

    /**
     * 批量续期
     * @param list
     * @return
     */
    public int batchSave(List<SafetyEntryConfigHistoryDO> list){
        if(CollectionUtils.isEmpty(list)){
            return 0;
        }
        AtomicInteger rows = new AtomicInteger();
        List<List<SafetyEntryConfigHistoryDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> {
            int j = safetyEntryConfigHistoryDOMapper.insertBatch(list);
            rows.addAndGet(j);
        });
        return rows.get();
    }

    /**
     * 批量新增
     */
    public void batchSaveHistory(List<SafetyEntryConfig> successList) {
        if (CollectionUtils.isEmpty(successList)) {
            return;
        }
        List<List<SafetyEntryConfig>> list = ListUtils.partition(successList, 1000);
        list.forEach(li -> safetyEntryConfigHistoryDOMapper.batchSave(li));
    }

    /**
     * 按照对接人，备案人身份证号，备案类型 统计已备案次数
     * @param params
     * @return
     */
    public List<EntryFactoryRecordVO> tallyEntryList(EntryConfigHistoryQueryParams params,String year){
        String beginDate = year+"-01-01 00:00:00";
        String endDate = (Integer.valueOf(year)+1)+"-01-01 00:00:00";
        params.setCreateTimeBegin(beginDate);
        params.setCreateTimeEnd(endDate);
        return safetyEntryConfigHistoryDOMapper.tallyEntryList(params);
    }

    /**
     * 批量审批或修改功能对应的历史表更新
     * @param historyIdList 历史表ID集合
     * @param params 提交入参
     * @param curEmpNo 当前操作人
     * @param curEmpName 当前操作人
     * @param auditFlag 审批标识 true/false
     */
    public void batchUpdateForAudit(List<Integer> historyIdList,SafetyEntryConfigBatchParams params,
                                    String curEmpNo, String curEmpName,boolean auditFlag){

        //批量更新备案记录
        SafetyEntryConfigHistoryDO newConfig = new SafetyEntryConfigHistoryDO();
        newConfig.setUpdateBy(curEmpName);
        newConfig.setUpdateByNo(curEmpNo);
        if(auditFlag){
            if(params.getStatus().equals(SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS)){
                newConfig.setApplyAudit("审核通过");
            }else{
                newConfig.setApplyAudit("驳回");
            }
            newConfig.setStatus(params.getStatus()); //审批状态
            newConfig.setApplyTime(new Date());
        }else{
            newConfig.setStatus(SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS);
        }
        newConfig.setEnterFactoryCode(params.getEnterFactoryCode());
        newConfig.setEnterStartTime(params.getEnterStartTime());
        newConfig.setEnterEndTime(params.getEnterEndTime());
        if (SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS.equals(newConfig.getStatus())) {
            Integer entryMethod = safetyEntryConfigService.selectEntryMethodByWorkNo(curEmpNo);
            newConfig.setEntryMethod(entryMethod);
        }

        //根据指定ID更新 状态，入厂厂区和期限
        SafetyEntryConfigHistoryDOExample example = new SafetyEntryConfigHistoryDOExample();
        example.createCriteria().andIdIn(historyIdList);

        safetyEntryConfigHistoryDOMapper.updateByExampleSelective(newConfig,example);
    }
}
