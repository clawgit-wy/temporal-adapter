package com.yutong.safety.core.service.entry;


import com.yutong.safety.common.constants.EipStateEnum;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterApproveRt;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigExample;
import com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryConfigBatchParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryConfigQueryParams;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryConfigMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
public class SafetyEntryConfigService extends AbstractSafetyWebBaseService<SafetyEntryConfig, SafetyEntryConfigExample, SafetyEntryConfigQueryParams> {

    @Resource
    private SafetyEntryConfigMapper safetyEntryConfigMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyEntryConfig, SafetyEntryConfigExample> getMapper() {
        return safetyEntryConfigMapper;
    }

    @Override
    protected void formatExample(SafetyEntryConfigExample example, SafetyEntryConfigQueryParams param, boolean pageType) {
        SafetyEntryConfigExample.Criteria criteria = example.createCriteria();
        //默认查询未删除的
        if(param.getAllFlag()==null || !param.getAllFlag()){
            criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        }
        if(!CollectionUtils.isEmpty(param.getIdList())){
            criteria.andIdIn(param.getIdList());
        }
        if (StringUtils.isNotBlank(param.getButtJoint())) {
            criteria.andButtJointCodeOrButtJointNameEqualTo(param.getButtJoint());
        }
        if (StringUtils.isNotBlank(param.getRecordTypeCode())) {
            criteria.andRecordTypeCodeEqualTo(param.getRecordTypeCode());
        }
        if(StringUtils.isNotBlank(param.getFactoryCode())){
            criteria.andEnterFactoryCodeLike("%"+param.getFactoryCode()+"%");
        }
        if (StringUtils.isNotBlank(param.getEnterFactoryCode())) {
            criteria.andEnterFactoryCodeEqualTo(param.getEnterFactoryCode());
        }
        if (StringUtils.isNotBlank(param.getEnterStartTime())) {
            criteria.andEnterStartTimeGreaterThanOrEqualTo(param.getEnterStartTime());
        }
        if(StringUtils.isNotBlank(param.getEnterStartDay())){
            criteria.andEnterStartTimeLike(param.getEnterStartDay()+"%");
        }
        if (StringUtils.isNotBlank(param.getEnterEndTime())) {
            String startTime = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getEnterEndTime(),1),1),1);
            criteria.andEnterStartTimeLessThan(startTime);
        }
        if (StringUtils.isNotBlank(param.getName())) {
            criteria.andNameEqualTo(param.getName());
        }
        if (StringUtils.isNotBlank(param.getCardNo())) {
            List<String> list = Arrays.asList(param.getCardNo().split("\n"));
            criteria.andCardNoIn(list);
        }
        if(!CollectionUtils.isEmpty(param.getCardNoList())){
            criteria.andCardNoIn(param.getCardNoList());
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
            if(param.getCreateTime().length()<10){
                curCreateTime = param.getCreateTime().substring(0,10)+" 00:00:00";
            }
            criteria.andCreateTimeGreaterThanOrEqualTo(DateUtils.parse(curCreateTime,2));
        }
        if (StringUtils.isNotBlank(param.getSubmitPhone())) {
            criteria.andSubmitPhoneEqualTo(param.getSubmitPhone());
        }
        if(StringUtils.isBlank(param.getOrderByClause())){
            example.setOrderByClause(" UPDATE_TIME DESC ");
        }else{
            example.setOrderByClause(param.getOrderByClause());
        }
        if(pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        if (param.getId() != null) {
            criteria.andIdNotEqualTo(param.getId());
        }
        if (StringUtils.isNotBlank(param.getMinEnterEndTime())) {
            criteria.andEnterEndTimeGreaterThanOrEqualTo(param.getMinEnterEndTime());
        }
        if (StringUtils.isNotBlank(param.getMaxEnterStartTime())) {
            criteria.andEnterStartTimeLessThanOrEqualTo(param.getMaxEnterStartTime());
        }

        if (StringUtils.isNotBlank(param.getMaxEnterEndTime())) {
            criteria.andEnterEndTimeLessThanOrEqualTo(param.getMaxEnterEndTime());
        }
        if(StringUtils.isNotBlank(param.getStatusNot())){
            criteria.andStatusNotEqualTo(param.getStatusNot());
        }
        if(StringUtils.isNotBlank(param.getDownMark())){
            criteria.andDownMarkEqualTo(param.getDownMark());
        }
        if(param.getHistoryId() != null){
            criteria.andHistoryIdEqualTo(param.getHistoryId());
        }
        if (param.getEntryMethod() != null) {
            criteria.andEntryMethodNotEqualTo(param.getEntryMethod());
        }
        if(CollectionUtils.isNotEmpty(param.getStatusList())){
            criteria.andStatusIn(param.getStatusList());
        }
    }

    @Override
    protected void batchDelete(SafetyEntryConfigExample example, List<String> idList) {
        SafetyEntryConfigExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (!CollectionUtils.isEmpty(idList)) {
            List<Integer> list = idList.stream().map(Integer::parseInt).collect(Collectors.toList());
            criteria.andIdIn(list);
        }
    }

    @Override
    protected void isExist(SafetyEntryConfig data, SafetyEntryConfigExample example) {
        SafetyEntryConfigExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
    }

    public int saveOrUpdate(SafetyEntryConfig data){
        int row;
        Date now = new Date();
        data.setUpdateTime(now);
        if(data.getId()!=null){
            row = safetyEntryConfigMapper.updateByPrimaryKeySelective(data);
        }else{
            data.setCreateTime(now);
            row = safetyEntryConfigMapper.insertSelective(data);
        }
        return row;
    }

    public List<SafetyEnterApproveRt> getEntryConfigByPhone(String phone){
        return safetyEntryConfigMapper.getEntryConfigByPhone(phone);
    }

    /**
     * 批量新增
     */
    public void batchSave(List<SafetyEntryConfig> successList) {
        if (CollectionUtils.isEmpty(successList)) {
            return;
        }
        List<List<SafetyEntryConfig>> list = ListUtils.partition(successList, 1000);
        list.forEach(li -> safetyEntryConfigMapper.batchSave(li));
    }

    /**
     * 批量修改
     */
    public void batchUpdate(List<SafetyEntryConfig> successList) {
        if (CollectionUtils.isEmpty(successList)) {
            return;
        }
        List<List<SafetyEntryConfig>> list = ListUtils.partition(successList, 1000);
        list.forEach(li -> safetyEntryConfigMapper.batchUpdate(li));
    }

    /**
     * 管理员的批量功能修改
     * @param params 参数
     * @param curEmpNo 当前处理人员工号
     * @param curEmpName 当前处理人姓名
     * @param auditFlag 是否审批
     * @return 变更条数
     */
    public int batchUpdateForAdmin(SafetyEntryConfigBatchParams params, String curEmpNo, String curEmpName,boolean auditFlag){
        Date d1 = new Date();
        //批量更新备案记录
        SafetyEntryConfig newConfig = new SafetyEntryConfig();
        newConfig.setStatus(params.getStatus()); //审批状态
        newConfig.setUpdateBy(curEmpName);
        newConfig.setUpdateByNo(curEmpNo);
        newConfig.setDelFlag(0);
        if(auditFlag){
            newConfig.setApplyAudit(EipStateEnum.getStatusDesc(params.getStatus()));
            newConfig.setApplyTime(d1);
            if(!SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS.equals(params.getStatus())){
                newConfig.setDelFlag(1);
            }
        }
        newConfig.setEnterFactoryCode(params.getEnterFactoryCode());
        newConfig.setEnterFactory(params.getEnterFactoryName());
        newConfig.setEnterStartTime(params.getEnterStartTime());
        newConfig.setEnterEndTime(params.getEnterEndTime());

        newConfig.setUpdateTime(d1);

        //根据指定ID更新 状态，入厂厂区和期限
        String[] idArr = params.getIdStr().split(",");
        List<Integer> idList = Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());
        SafetyEntryConfigExample example = new SafetyEntryConfigExample();
        example.createCriteria().andIdIn(idList);
        // 入场方式
        if (SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS.equals(params.getStatus())) {
            Integer entryMethod = this.selectEntryMethodByWorkNo(curEmpNo);
            newConfig.setEntryMethod(entryMethod);
        }

        return safetyEntryConfigMapper.updateByExampleSelective(newConfig,example);
    }

    /**
     * 删除
     * @param param
     */
    public void deleteByExample(SafetyEntryConfigQueryParams param){
        SafetyEntryConfigExample example = new SafetyEntryConfigExample();
        formatExample(example, param, false);
        safetyEntryConfigMapper.deleteByExample(example);
    }

    /**
     * 删除已失效审批通过的无数据，只保留最新一条记录
     * @param maxEndTime
     */
    public void clearDisabledConfig(String maxEndTime){
        if(StringUtils.isBlank(maxEndTime)){
            maxEndTime = DateUtils.format(new Date(),2);
        }
        safetyEntryConfigMapper.clearDisabledConfig(maxEndTime);
    }

    /**
     * 批量变更个人全量的下发标记
     * 根据ID变更
     * @param ids id
     */
    public void batchUpdateDownMark(List<String> ids, String downMark) {
        safetyEntryConfigMapper.batchUpdateById(ids, downMark, null);
    }

    /**
     * 批量变更个人全量的二维码
     * 根据ID变更
     * @param ids id
     */
    public void batchUpdateQrCode(List<String> ids, String qrCode) {
        safetyEntryConfigMapper.batchUpdateById(ids, null, qrCode);
    }

    public long selectHistoryCount(SafetyEntryConfigQueryParams param) {
        SafetyEntryConfigExample example = new SafetyEntryConfigExample();
        formatExample(example, param, false);
        return safetyEntryConfigMapper.countHistoryByExample(example);
    }

    public List<SafetyEntryConfig> selectHistoryList(SafetyEntryConfigQueryParams param) {
        SafetyEntryConfigExample example = new SafetyEntryConfigExample();
        formatExample(example, param, true);
        example.setOrderByClause(" create_time DESC");
        return safetyEntryConfigMapper.selectHistoryByExample(example);
    }

    /**
     * 数据新增或修改
     * @param workNo 员工号
     * @param entryMethod 入场配置
     */
    public void selectCountOfEntryMethod(String workNo, Integer entryMethod) {
        long count = safetyEntryConfigMapper.selectCountOfEntryMethod(workNo);
        if (count > 0) {
            safetyEntryConfigMapper.updateEntryMethodInfo(workNo, entryMethod);
        } else {
            safetyEntryConfigMapper.saveEntryMethodInfo(workNo, entryMethod);
        }
    }

    /**
     * 获取入场方式信息
     * @param workNo 员工号
     * @return 返回具体的入场方式信息
     */
    public Integer selectEntryMethodByWorkNo(String workNo) {
        if (StringUtils.isBlank(workNo)) {
            return 1;
        }
        Integer entryMethod = safetyEntryConfigMapper.selectEntryMethodByWorkNo(workNo);
        if (entryMethod == null) {
            return 1;
        } else {
            return entryMethod;
        }
    }

    public Integer getEntryMethod(String workNo) {
        return safetyEntryConfigMapper.selectEntryMethodByWorkNo(workNo);

    }

    /**
     *
     * @param type 0：推送， 1：移除
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public List<SafetyEntryConfig> selectListByTime(String type, String startTime, String endTime) {
        SafetyEntryConfigExample example = new SafetyEntryConfigExample();
        SafetyEntryConfigExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        criteria.andStatusEqualTo("30");
        if (type.equals("0")) {
            criteria.andEnterStartTimeBetween(startTime, endTime);
        }
        if (type.equals("1")) {
            criteria.andEnterEndTimeBetween(startTime, endTime);
        }
        return safetyEntryConfigMapper.selectByExample(example);
    }

    public List<SafetyEntryConfig> selectListByCardNo(String startTime, List<String> cardNos) {
        SafetyEntryConfigExample example = new SafetyEntryConfigExample();
        SafetyEntryConfigExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        criteria.andStatusEqualTo("30");
        criteria.andEnterEndTimeGreaterThan(startTime);
        criteria.andCardNoIn(cardNos);
        return safetyEntryConfigMapper.selectByExample(example);
    }
}
