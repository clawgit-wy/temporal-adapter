package com.yutong.safety.core.service.build;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.BuildLongConfirmListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildDepartConfirmVO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDepartChangeDOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDepartConfirmDOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDepartConfirmHistoryDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SafetyBuildDepartConfirmService {

    @Autowired
    private SafetyBuildDepartConfirmDOMapper safetyBuildDepartConfirmDOMapper;

    @Autowired
    private SafetyBuildDepartChangeDOMapper safetyBuildDepartChangeDOMapper;

    @Autowired
    private SafetyBuildDepartConfirmHistoryDOMapper safetyBuildDepartConfirmHistoryDOMapper;

    public List<SafetyBuildDepartConfirmDO> selectList(BuildLongConfirmListParam listParam){
        SafetyBuildDepartConfirmDOExample example = formatExample(listParam);
        if(listParam.getPageFlag()!=null && listParam.getPageFlag()){
            example.setLimit(listParam.getLimit());
            example.setOffset(listParam.getOffset());
        }
        if(StringUtils.isNotBlank(listParam.getOrderByClause())){
            example.setOrderByClause(listParam.getOrderByClause());
        }
        return safetyBuildDepartConfirmDOMapper.selectByExample(example);
    }

    public Long selectCount(BuildLongConfirmListParam listParam){
        SafetyBuildDepartConfirmDOExample example = formatExample(listParam);
        return safetyBuildDepartConfirmDOMapper.countByExample(example);
    }

    private SafetyBuildDepartConfirmDOExample formatExample(BuildLongConfirmListParam listParam){
        SafetyBuildDepartConfirmDOExample example = new SafetyBuildDepartConfirmDOExample();
        SafetyBuildDepartConfirmDOExample.Criteria criteria = example.createCriteria();

        if(StringUtils.isNotBlank(listParam.getConfirmFlag())){
            criteria.andConfirmFlagEqualTo(listParam.getConfirmFlag());
        }else{
            criteria.andConfirmFlagEqualTo("0");
        }
        if(StringUtils.isNotBlank(listParam.getCorresFlag())){
            criteria.andCorresFlagEqualTo(listParam.getCorresFlag());
        }
        if(StringUtils.isNotBlank(listParam.getOperTimeBegin())){
            criteria.andCreateTimeGreaterThanOrEqualTo(listParam.getOperTimeBegin());
        }
        if(StringUtils.isNotBlank(listParam.getOperTimeEnd())){
            criteria.andCreateTimeLessThanOrEqualTo(listParam.getOperTimeEnd());
        }
        if(StringUtils.isNotBlank(listParam.getCurEmpNo())){
            criteria.andCorresEmpCodeLike("%"+listParam.getCurEmpNo()+"%");
        }
        if(!CollectionUtils.isEmpty(listParam.getIdList())){
            criteria.andIdIn(listParam.getIdList());
        }
        if (StringUtils.isNotBlank(listParam.getLtUpdateTime())) {
            criteria.andUpdateTimeLessThan(listParam.getLtUpdateTime());
        }
        if(!CollectionUtils.isEmpty(listParam.getEmployeeCodeList())){
            criteria.andEmployeeCodeIn(listParam.getEmployeeCodeList());
        }
        return example;
    }

    public List<BuildDepartConfirmVO> selectDepartList(BuildLongConfirmListParam listParam){
        listParam.setOrderByClause(" sdcl.update_time desc ");
        return safetyBuildDepartConfirmDOMapper.selectDepartList(listParam);
    }

    public Long selectDepartCount(BuildLongConfirmListParam listParam){
        return safetyBuildDepartConfirmDOMapper.selectDepartCount(listParam);
    }

    public int batchInsert(List<SafetyBuildDepartConfirmDO> list){
        return safetyBuildDepartConfirmDOMapper.batchInsert(list);
    }

    public int update(SafetyBuildDepartConfirmDO confirmDO){
        return safetyBuildDepartConfirmDOMapper.updateByPrimaryKeySelective(confirmDO);
    }

    public int updateByExample(BuildLongConfirmListParam listParam,SafetyBuildDepartConfirmDO confirmDO){
        SafetyBuildDepartConfirmDOExample example = formatExample(listParam);
        return safetyBuildDepartConfirmDOMapper.updateByExampleSelective(confirmDO,example);
    }

    public List<SafetyBuildDepartChangeDO> selectDepartChangeList(String beginTime,String empCreateBegin){
        return safetyBuildDepartChangeDOMapper.selectDepartChangeList(beginTime,empCreateBegin);
    }

    public int insertBatchDepartChange(List<SafetyBuildDepartChangeDO> list){
        return safetyBuildDepartChangeDOMapper.insertBatchDepartChange(list);
    }


    public int updateDepartChangeByIdList(List<Integer> idList, SafetyBuildDepartChangeDO record){
        SafetyBuildDepartChangeDOExample example = new SafetyBuildDepartChangeDOExample();
        SafetyBuildDepartChangeDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(idList);
        return safetyBuildDepartChangeDOMapper.updateByExampleSelective(record,example);
    }

    public int deleteChangeByExample(BuildLongConfirmListParam listParam){
        SafetyBuildDepartChangeDOExample example = new SafetyBuildDepartChangeDOExample();
        SafetyBuildDepartChangeDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(listParam.getConfirmFlag())){
            criteria.andBuildConfirmFlagEqualTo(listParam.getConfirmFlag());
        }
        if(StringUtils.isNotBlank(listParam.getEmployeeCode())){
            criteria.andEmployeeCodeEqualTo(listParam.getEmployeeCode());
        }
        if(!CollectionUtils.isEmpty(listParam.getEmployeeCodeList())){
            criteria.andEmployeeCodeIn(listParam.getEmployeeCodeList());
        }
        if(StringUtils.isNotBlank(listParam.getNewDeptCode())){
            criteria.andNewDeptCodeEqualTo(listParam.getNewDeptCode());
        }
        return safetyBuildDepartChangeDOMapper.deleteByExample(example);
    }

    public int deleteConfirmByExample(BuildLongConfirmListParam listParam){
        SafetyBuildDepartConfirmDOExample example = new SafetyBuildDepartConfirmDOExample();
        SafetyBuildDepartConfirmDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(listParam.getConfirmFlag())){
            criteria.andConfirmFlagEqualTo(listParam.getConfirmFlag());
        }
        if(StringUtils.isNotBlank(listParam.getEmployeeCode())){
            criteria.andEmployeeCodeEqualTo(listParam.getEmployeeCode());
        }
        if(!CollectionUtils.isEmpty(listParam.getEmployeeCodeList())){
            criteria.andEmployeeCodeIn(listParam.getEmployeeCodeList());
        }
        if(StringUtils.isNotBlank(listParam.getNewDeptCode())){
            criteria.andNewDeptCodeEqualTo(listParam.getNewDeptCode());
        }
        return safetyBuildDepartConfirmDOMapper.deleteByExample(example);
    }

    public  List<SafetyBuildDepartConfirmDO> selectUnConfirmList(BuildLongConfirmListParam listParam){
        return safetyBuildDepartConfirmDOMapper.selectUnConfirmList(listParam);
    }

    /**
     * 批量保存确认历史表
     * @param list
     * @return
     */
    public int batchInsertHistory(List<SafetyBuildDepartConfirmDO> list){
        return safetyBuildDepartConfirmHistoryDOMapper.batchInsert(list);
    }

    public void batchUpdate(Set<Integer> ids) {
        safetyBuildDepartChangeDOMapper.batchUpdateById(ids);
    }
}
