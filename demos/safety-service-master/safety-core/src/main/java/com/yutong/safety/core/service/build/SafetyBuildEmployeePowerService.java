package com.yutong.safety.core.service.build;

import com.yutong.safety.common.annotation.DbLog;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDOExample;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmpNoMaxPowerTimeVo;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildEmployeePowerDOMapper;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.SafetyClosePublicBuildDto;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SafetyBuildEmployeePowerService {

    @Autowired
    private SafetyBuildEmployeePowerDOMapper safetyBuildEmployeePowerDOMapper;

    @Resource
    private SafetyBuildIssuedLogService safetyBuildIssuedLogService;

    @DbLog
    public void insertBatch(List<SafetyBuildEmployeePowerDO> list){
        List<List<SafetyBuildEmployeePowerDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyBuildEmployeePowerDOMapper.insertBatch(li));
    }

    @DbLog
    public void insertBatchForInit(List<SafetyBuildEmployeePowerDO> list){
        safetyBuildEmployeePowerDOMapper.insertBatchForInit(list);
    }

    //富士权限初始化使用
    @DbLog
    public void updateBatchForCrkInit(List<SafetyBuildEmployeePowerDO> list){
        List<List<SafetyBuildEmployeePowerDO>> partition = ListUtils.partition(list, 500);
        partition.forEach(li -> safetyBuildEmployeePowerDOMapper.updateBatch(li));
    }

    public List<SafetyBuildEmployeePowerDO> selectByExample(BuildEmployeePowerListParam listParam){
        SafetyBuildEmployeePowerDOExample example = formatExample(listParam);
        if(StringUtils.isNotBlank(listParam.getOrderByClause())){
            example.setOrderByClause(listParam.getOrderByClause());
        }
        if(listParam.isPageFlag()){
            example.setLimit(listParam.getLimit());
            example.setOffset(listParam.getOffset());
        }
        return safetyBuildEmployeePowerDOMapper.selectByExample(example);
    }

    public long countByExample(BuildEmployeePowerListParam listParam){
        SafetyBuildEmployeePowerDOExample example = formatExample(listParam);
        return safetyBuildEmployeePowerDOMapper.countByExample(example);
    }

    public List<BuildEmployeePowerVO> selectList(BuildEmployeePowerListParam listParam){
        if(StringUtils.isBlank(listParam.getOrderByClause())){
            listParam.setOrderByClause(" update_time desc ");
        }
        return safetyBuildEmployeePowerDOMapper.selectList(listParam);
    }


    public long selectCount(BuildEmployeePowerListParam listParam){
        return safetyBuildEmployeePowerDOMapper.selectCount(listParam);
    }

    private SafetyBuildEmployeePowerDOExample formatExample(BuildEmployeePowerListParam param){
        SafetyBuildEmployeePowerDOExample example =new SafetyBuildEmployeePowerDOExample();
        SafetyBuildEmployeePowerDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFlagEqualTo("0"); //查询未删除的
        if(StringUtils.isNotBlank(param.getEmployeeCode())){
            criteria.andEmployeeCodeEqualTo(param.getEmployeeCode());
        }
        if(StringUtils.isNotBlank(param.getDutyDeptCode())){
            if("null".equals(param.getDutyDeptCode())){
                criteria.andDutyDeptCodeIsNull();
            }else{
                criteria.andDutyDeptCodeLike(param.getDutyDeptCode()+"%");
            }
        }
        if(param.getFilterFlag() != null && param.getFilterFlag() == 1){
            criteria.andEndTimeGreaterThanOrEqualTo(DateUtils.format(new Date(),1));
        }
        if(!CollectionUtils.isEmpty(param.getEmpCodeList())){
            criteria.andEmployeeCodeIn(param.getEmpCodeList());
        }
        if(!CollectionUtils.isEmpty(param.getNotIdList())){
            criteria.andIdNotIn(param.getNotIdList());
        }
        if(!CollectionUtils.isEmpty(param.getIdList())){
            criteria.andIdIn(param.getIdList());
        }
        if(!StringUtils.isBlank(param.getProcessId())){
            criteria.andProcessIdEqualTo(param.getProcessId());
        }

        if(!CollectionUtils.isEmpty(param.getEmployeeCodeList())){
            criteria.andEmployeeCodeIn(param.getEmployeeCodeList());
        }
        if(!CollectionUtils.isEmpty(param.getDeviceCodeList())){
            criteria.andDeviceCodeIn(param.getDeviceCodeList());
        }
        if(StringUtils.isNotBlank(param.getUpdateTimeEnd())){
            Date endDate = DateUtils.parse(param.getUpdateTimeEnd(),2);
            criteria.andUpdateTimeLessThan(endDate);
        }
        return example;
    }

    public int insert(SafetyBuildEmployeePowerDO employeePowerDO){
        return safetyBuildEmployeePowerDOMapper.insert(employeePowerDO);
    }

    public int update(SafetyBuildEmployeePowerDO employeePowerDO){
        return safetyBuildEmployeePowerDOMapper.updateByPrimaryKeySelective(employeePowerDO);
    }

    public int delete(SafetyBuildEmployeePowerDOExample example){
        return safetyBuildEmployeePowerDOMapper.deleteByExample(example);
    }

    /**
     * 根据员工号关闭门禁相关权限
     * @param buildPowerList 员工号
     * @param operateName 操作人姓名
     * @param reason 执行原因
     */
    public void cancelBuildPowerForEmployee(List<SafetyBuildEmployeePowerDO> buildPowerList,String operateName,String reason){
        String curTime = DateUtils.format(new Date(),2);
        String endTime = "2099-04-07";
        List<SafetyBuildEmployeePowerDO> cancelList = new ArrayList<>();
        List<SafetyBuildIssuedLogDO> logDOS = new ArrayList<>();
        Map<String,List<SafetyBuildEmployeePowerDO>> empPowerMap = buildPowerList.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
        empPowerMap.forEach((key,value)->{
            SafetyBuildIssuedLogDO curLog = new SafetyBuildIssuedLogDO();
            String deviceCodes = value.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).collect(Collectors.joining(","));
            String deviceNames = value.stream().map(SafetyBuildEmployeePowerDO::getDeviceName).collect(Collectors.joining(","));
            curLog.setDeviceCode(deviceCodes);
            curLog.setDeviceName(deviceNames);
            curLog.setIssuedType("关闭");
            curLog.setCreateBy(operateName);
            curLog.setCreateTime(new Date());
            curLog.setEmployeeCode(key);
            curLog.setEmployeeName(value.get(0).getEmployeeName());
            curLog.setBeginTime(curTime);
            curLog.setEndTime(endTime);
            curLog.setRemark(reason); //申请原因
            curLog.setSourceType("3");  //超级管理员注销

            String issuedStatus = safetyBuildIssuedLogService.issuedBuildPower(curLog);
            curLog.setIssuedStatus(issuedStatus);
            logDOS.add(curLog);

            if("成功".equals(issuedStatus)){
                value.forEach(e->{
                    e.setDeleteFlag("1");
                    e.setDeleteTime(new Date());
                    e.setRemark(reason);
                });
                cancelList.addAll(value);
            }
        });

        try {
            safetyBuildIssuedLogService.batchInsert(logDOS);
        }catch (Exception e){
            LogUtils.error(ResponseEnum.FAIL.getCode(),"批量保存异常{}",e);
        }
        if(!CollectionUtils.isEmpty(cancelList)){
            try{
//                safetyBuildEmployeePowerDOMapper.insertBatch(cancelList);
                this.insertBatch(cancelList);
            }catch (Exception e){
                LogUtils.error(ResponseEnum.FAIL.getCode(),"批量添加人员明细异常{}",e);
            }
        }
    }

    public List<SafetyEmployeeMessageDO> getEmployeeForEmpPower(BuildEmployeePowerListParam listParam){
        return safetyBuildEmployeePowerDOMapper.getEmployeeForEmpPower(listParam);
    }

    public int updateByExample(BuildEmployeePowerListParam listParam,SafetyBuildEmployeePowerDO record){
        SafetyBuildEmployeePowerDOExample example = formatExample(listParam);
        return this.updateByExampleSelective(record,example);
    }

    public int deleteByExample(BuildEmployeePowerListParam listParam,SafetyBuildEmployeePowerDO record){
        SafetyBuildEmployeePowerDOExample example = formatExample(listParam);
        record.setUpdateTime(new Date());
        record.setDeleteFlag("1");
        record.setDeleteTime(new Date());
        return this.updateByExampleSelective(record,example);
    }
    @DbLog
    public int updateByExampleSelective(SafetyBuildEmployeePowerDO record, SafetyBuildEmployeePowerDOExample example) {
        return safetyBuildEmployeePowerDOMapper.updateByExampleSelective(record,example);
    }

    /**
     * 获取员工门禁权限-返回合并后的设备信息
     * @param listParam
     * @return 返回合并后的设备信息
     */
    public List<SafetyBuildEmployeePowerDO> getEmpPowerConcat(BuildEmployeePowerListParam listParam){
        return safetyBuildEmployeePowerDOMapper.getEmpPowerConcat(listParam);
    }

    /**
     * 获取员工门禁权限-返回未合并的设备信息
     * @param listParam
     * @return 返回合并后的设备信息
     */
    public List<SafetyBuildEmployeePowerDO> getEmpPowerList(BuildEmployeePowerListParam listParam){
        return safetyBuildEmployeePowerDOMapper.getEmpPowerList(listParam);
    }

    /**
     * 根据员工号获取对应的员工楼宇门禁权限对应的厂区编码信息
     * @param empNoList 员工号
     * @return 返回名称和对应的厂区ID信息
     */
    public List<ParamDTO> getFactoryIdByBuildPower(List<String> empNoList){
        return safetyBuildEmployeePowerDOMapper.getFactoryIdByBuildPower(empNoList);
    }

    /**
     * 批量删除员工大门权限
     * @param empNos
     * @return
     */
    public int batchDelEmployeePower(List<String> empNos){
        if(CollectionUtils.isEmpty(empNos)){
            return 0;
        }
        SafetyBuildEmployeePowerDOExample example =  new SafetyBuildEmployeePowerDOExample();
        SafetyBuildEmployeePowerDOExample.Criteria criteria = example.createCriteria();
        criteria.andEmployeeCodeIn(empNos);
        try{
            return safetyBuildEmployeePowerDOMapper.deleteByExample(example);
        }catch (Exception e){
            LogUtils.error("批量更新状态失败",e);
        }
        return 0;
    }


    public List<BuildEmployeePowerVO> selectEmpPowerList(BuildEmployeePowerListParam listParam) {
        return safetyBuildEmployeePowerDOMapper.selectEmpPowerList(listParam);
    }
    /**
     * 获取制定员工号中没有指定门禁设备的数据
     * @param empCodeList 员工号
     * @param deviceCode 楼宇门禁设备编码
     * @return 返回符合条件的数据
     */
    public List<SafetyBuildEmployeePowerDO> selectDeviceCodeInfo(List<String> empCodeList, String deviceCode){
        SafetyBuildEmployeePowerDOExample example =new SafetyBuildEmployeePowerDOExample();
        SafetyBuildEmployeePowerDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFlagEqualTo("0"); //查询未删除的
        criteria.andDeviceCodeEqualTo(deviceCode);
        if (!CollectionUtils.isEmpty(empCodeList)) {
            criteria.andEmployeeCodeIn(empCodeList);
        }
        return safetyBuildEmployeePowerDOMapper.selectByExample(example);
    }


    /**
     * 获取员工拥有权限的楼栋信息
     * @param empNoList 员工号
     * @return 返回具体的信息
     */
    public List<SafetyClosePublicBuildDto> selectPowerByEmpNo(List<String> empNoList) {
        return safetyBuildEmployeePowerDOMapper.selectPowerByEmpNo(empNoList);
    }


    public void delete(List<String> deleteIds, String msg) {
        safetyBuildEmployeePowerDOMapper.batchUpdate(deleteIds, msg);
    }

    /**
     * 获取员工拥有的非公共楼宇 权限结束日期的最大日期
     * @param empNoList 员工号
     * @return 集合
     */
    public List<BuildEmpNoMaxPowerTimeVo> selectMaxEndTimeByEmpNo(List<String> empNoList) {
        return safetyBuildEmployeePowerDOMapper.selectMaxEndTimeByEmpNo(empNoList);
    }

    public List<BuildEmployeePowerVO> selectCommonEmpPowerList(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return safetyBuildEmployeePowerDOMapper.selectCommonEmpPowerList(ids);
    }

    public void updateDepartChangeTime(List<String> empNos) {
        safetyBuildEmployeePowerDOMapper.updateDepartChangeTime(empNos);
    }
}
