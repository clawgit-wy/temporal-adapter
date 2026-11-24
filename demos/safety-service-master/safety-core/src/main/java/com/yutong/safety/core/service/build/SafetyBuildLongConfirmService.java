package com.yutong.safety.core.service.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildLongConfirmDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildLongConfirmDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildLongConfirmListParam;
import com.yutong.safety.core.db.mapper.build.SafetyBuildLongConfirmDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyBuildLongConfirmService {

    @Autowired
    private SafetyBuildLongConfirmDOMapper safetyBuildLongConfirmDOMapper;

    /**
     * 长期未进出门禁-数据判断
     * @param param
     * @return
     */
    public List<SafetyBuildLongConfirmDO> queryLongTermList(BuildLongConfirmListParam param) {
        param.setPageFlag(false);
        return safetyBuildLongConfirmDOMapper.queryLongTermList(param);
    }

    /**
     * 批量新增
     * @param longConfirmDOS
     * @return
     */
    public int insertBatch(List<SafetyBuildLongConfirmDO> longConfirmDOS){
        if(CollectionUtils.isEmpty(longConfirmDOS)){
            return 0;
        }
        return safetyBuildLongConfirmDOMapper.insertBatch(longConfirmDOS);
    }

    public List<SafetyBuildLongConfirmDO> selectList(BuildLongConfirmListParam param){
        SafetyBuildLongConfirmDOExample example = formatExample(param);
        if(param.getPageFlag()!=null && param.getPageFlag()){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        return safetyBuildLongConfirmDOMapper.selectByExample(example);
    }

    public Long selectCount(BuildLongConfirmListParam param){
        SafetyBuildLongConfirmDOExample example = formatExample(param);
        return safetyBuildLongConfirmDOMapper.countByExample(example);
    }

    private SafetyBuildLongConfirmDOExample formatExample(BuildLongConfirmListParam param){
        SafetyBuildLongConfirmDOExample example = new SafetyBuildLongConfirmDOExample();
        SafetyBuildLongConfirmDOExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(param.getConfirmFlag())){
            criteria.andConfirmFlagEqualTo(param.getConfirmFlag()); //待确认列表
        }
        if (!StringUtils.isEmpty(param.getNotConfirmFlag())) {
            criteria.andConfirmFlagNotEqualTo(param.getNotConfirmFlag()); // 待确认和保留授权的列表
        }
        if(!StringUtils.isEmpty(param.getFilterDutyDept())){
            example.setFilterDutyDept(param.getFilterDutyDept());
        }
        if(!StringUtils.isEmpty(param.getCurEmpNo())){
            example.setCurEmpNo(param.getCurEmpNo());
        }
        if(!StringUtils.isEmpty(param.getEmployeeCode())){
            criteria.andEmployeeCodeEqualTo(param.getEmployeeCode());
        }
        if(!StringUtils.isEmpty(param.getEmployeeName())){
            criteria.andEmployeeNameEqualTo(param.getEmployeeName());
        }
        if(!StringUtils.isEmpty(param.getCorresFlag())){
            criteria.andCorresFlagEqualTo(param.getCorresFlag());
        }
        if(!StringUtils.isEmpty(param.getDeviceName())){
            criteria.andDeviceNameLike("%"+param.getDeviceName()+"%");
        }
        if(param.getMinDay() != null){
            criteria.andDiffDayGreaterThanOrEqualTo(param.getMinDay());
        }
        if(param.getMaxDay() != null){
            criteria.andDiffDayLessThanOrEqualTo(param.getMaxDay());
        }
        if(!StringUtils.isEmpty(param.getDutyPerson())){
            criteria.andDutyEmpNameEqualTo(param.getDutyPerson());
        }
        if(param.getIdList() != null){
            criteria.andIdIn(param.getIdList());
        }
        if(param.getDiffDayList() != null){
            criteria.andDiffDayIn(param.getDiffDayList());
        }
        if(param.getEmployeeCodeList() != null) {
            criteria.andEmployeeCodeIn(param.getEmployeeCodeList());
        }
        if (!StringUtils.isEmpty(param.getLtEipTodoDate())) {
            criteria.andEipTodoDateLessThan(param.getLtEipTodoDate());
        }
        if (!StringUtils.isEmpty(param.getGtEipTodoDate())) {
            criteria.andEipTodoDateGreaterThan(param.getGtEipTodoDate());
        }
        if (Boolean.TRUE.equals(param.getEipTodoDateNotNull())) {
            criteria.andEipTodoDateIsNotNull();
        }
        if (Boolean.TRUE.equals(param.getEipTodoDateNull())) {
            criteria.andEipTodoDateIsNull();
        }
        return example;
    }

    public int updateByExample(BuildLongConfirmListParam param,SafetyBuildLongConfirmDO longConfirmDO){

        SafetyBuildLongConfirmDOExample example = formatExample(param);
        return safetyBuildLongConfirmDOMapper.updateByExampleSelective(longConfirmDO,example);
    }

    public int deleteHistoryConfirm(){
        return safetyBuildLongConfirmDOMapper.deleteHistoryConfirm();
    }

    public List<SafetyBuildLongConfirmDO> selectOtherList() {
        SafetyBuildLongConfirmDOExample example = new SafetyBuildLongConfirmDOExample();
        example.createCriteria()
                .andConfirmFlagEqualTo("0")
                .andCorresFlagEqualTo("0")
                .andDiffDayGreaterThan(173)
                .andEipTodoStatusEqualTo(0);
        return safetyBuildLongConfirmDOMapper.selectByExample(example);
    }

    public Map<String, Integer> selectDeleteList() {
        SafetyBuildLongConfirmDOExample example = new SafetyBuildLongConfirmDOExample();
        example.createCriteria().andConfirmFlagEqualTo("0");
        List<SafetyBuildLongConfirmDO> list = safetyBuildLongConfirmDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(li -> li.getEmployeeCode() + "&" + li.getDeviceCode(), SafetyBuildLongConfirmDO::getId));
    }

    public void deleteListById(List<Integer> li) {
        SafetyBuildLongConfirmDOExample example = new SafetyBuildLongConfirmDOExample();
        example.createCriteria().andIdIn(li);
        safetyBuildLongConfirmDOMapper.deleteByExample(example);
    }

    public void saveHistory() {
        safetyBuildLongConfirmDOMapper.saveHistory();
    }

    public void updateOfHistoryInfo() {
        // 回写数据
        safetyBuildLongConfirmDOMapper.updateOfHistoryInfo();
        // 删除历史表
        safetyBuildLongConfirmDOMapper.deleteHistory();
    }
}
