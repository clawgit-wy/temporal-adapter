package com.yutong.safety.core.service.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteListDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteListDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildWhiteRuleListParam;
import com.yutong.safety.core.db.mapper.build.SafetyBuildWhiteListDTOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildWhiteRuleDTOMapper;
import com.yutong.safety.core.dto.SafetyEmployeeMessageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class SafetyBuildWhiteListService {

    @Autowired
    SafetyBuildWhiteListDTOMapper safetyBuildWhiteListDOMapper;

    @Autowired
    SafetyBuildWhiteRuleDTOMapper safetyBuildWhiteRuleDTOMapper;

    public List<SafetyBuildWhiteListDO> selectPageList(SafetyBuildWhiteListDO param){
        SafetyBuildWhiteListDOExample example = getSafetyBuildWhiteListDOExample(param);
        example.setOffset(param.getOffset());
        example.setLimit(param.getLimit());
        return safetyBuildWhiteListDOMapper.selectByExample(example);
    }

    public long selectPageCount(SafetyBuildWhiteListDO param){
        SafetyBuildWhiteListDOExample example = getSafetyBuildWhiteListDOExample(param);
        return safetyBuildWhiteListDOMapper.countByExample(example);
    }

    public void delete(Integer id) {
        SafetyBuildWhiteListDO param = new SafetyBuildWhiteListDO();
        param.setId(id);
        param.setIsDelete("1");

        safetyBuildWhiteListDOMapper.updateByPrimaryKeySelective(param);
    }

    public void save(SafetyBuildWhiteListDO param) {
        safetyBuildWhiteListDOMapper.insertSelective(param);
    }

    public void batchSave(List<SafetyBuildWhiteListDO> list) {
        safetyBuildWhiteListDOMapper.batchSaveOrUpdate(list);
    }


    private SafetyBuildWhiteListDOExample getSafetyBuildWhiteListDOExample(SafetyBuildWhiteListDO param) {
        SafetyBuildWhiteListDOExample example = new SafetyBuildWhiteListDOExample();
        SafetyBuildWhiteListDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("0");

        if(!StringUtils.isBlank(param.getEmployeeName())){
            criteria.andEmployeeNameLike("%"+ param.getEmployeeName()+"%");
        }

        if(!StringUtils.isBlank(param.getEmployeeCode())){
            criteria.andEmployeeCodeEqualTo(param.getEmployeeCode());
        }

        if(!StringUtils.isBlank(param.getDutyDeptCode())){
            //此处牵涉新增的时候统计该责任部门下现有多少个人在白名单中，改的时候要注意
            criteria.andDutyDeptCodeIn(Arrays.asList(param.getDutyDeptCode().split(";")));
        }

        if(!StringUtils.isBlank(param.getDutyDeptName())){
            criteria.andDutyDeptNameLike("%"+ param.getDutyDeptName()+"%");
        }

        if(!StringUtils.isBlank(param.getCreateUserName())){
            criteria.andCreateUserNameLike("%"+ param.getCreateUserName()+"%");
        }

        if(!StringUtils.isBlank(param.getCreateReason())){
            criteria.andCreateReasonLike("%"+ param.getCreateReason()+"%");
        }

        if(!StringUtils.isBlank(param.getDeptCode())){
            criteria.andDeptCodeLike(param.getDeptCode()+"%");
        }

        if(!StringUtils.isBlank(param.getSourceFrom())){
            criteria.andSourceFromEqualTo(param.getSourceFrom());
        }

        if(!StringUtils.isBlank(param.getCreateUserCode())){
            criteria.andCreateUserCodeEqualTo(param.getCreateUserCode());
        }

        example.setOrderByClause(" create_time desc");
        return example;
    }

    public List<SafetyEmployeeMessageDTO> selectRuleUser(){
        return safetyBuildWhiteRuleDTOMapper.selectRuleUser();
    }

    public void delete(){
        SafetyBuildWhiteListDOExample example = new SafetyBuildWhiteListDOExample();
        SafetyBuildWhiteListDOExample.Criteria criteria = example.createCriteria();
        criteria.andSourceFromEqualTo("1");
        safetyBuildWhiteListDOMapper.deleteByExample(example);
    }

    public void updateByEmpNo(String empNo,List<String> dutyDeptCodeList) {
        safetyBuildWhiteListDOMapper.updateByEmpNo(empNo,dutyDeptCodeList);
    }

    public void batchUpdate(List<SafetyBuildWhiteListDO> list){
        safetyBuildWhiteListDOMapper.batchUpdate(list);
    }

    /**
     * 更新白名单的创建人
     * @param dutyDeptCode 责任部门
     * @param oldEmpCodeSet 原创建人
     * @param newEmpCode 新创建人
     * @param newEmpName 新创建人
     */
    public void updateByConditions(String dutyDeptCode,List<String> oldEmpCodeSet,String newEmpCode,String newEmpName){
        safetyBuildWhiteListDOMapper.updateByConditions(dutyDeptCode,oldEmpCodeSet,newEmpCode,newEmpName);
    }

    public List<SafetyBuildWhiteListDO> selectByEmpCodeList(List<String> empCodeList){
        SafetyBuildWhiteListDOExample example = new SafetyBuildWhiteListDOExample();
        SafetyBuildWhiteListDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("0");

        if (!CollectionUtils.isEmpty(empCodeList)){
            criteria.andEmployeeCodeIn(empCodeList);
        }
        return safetyBuildWhiteListDOMapper.selectByExample(example);
    }

    public int updateByExample(List<String> empCodeList,SafetyBuildWhiteListDO record){
        SafetyBuildWhiteListDOExample example = new SafetyBuildWhiteListDOExample();
        SafetyBuildWhiteListDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("0");
        criteria.andEmployeeCodeIn(empCodeList);

        return safetyBuildWhiteListDOMapper.updateByExampleSelective(record,example);
    }

    /**
     * 全量获取白名单信息
     * @param param
     * @return
     */
    public List<SafetyBuildWhiteListDO> selectList(BuildWhiteRuleListParam param){
        SafetyBuildWhiteListDOExample example = formatExample(param);
        if(param.getPageFlag()!=null && param.getPageFlag()){
            example.setOffset(param.getOffset());
            example.setLimit(param.getLimit());
        }
        example.setOrderByClause(" create_time desc");

        return safetyBuildWhiteListDOMapper.selectByExample(example);
    }

    private SafetyBuildWhiteListDOExample formatExample(BuildWhiteRuleListParam param) {
        SafetyBuildWhiteListDOExample example = new SafetyBuildWhiteListDOExample();
        SafetyBuildWhiteListDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("0");
        if(!CollectionUtils.isEmpty(param.getCreateReasons())){
            criteria.andCreateReasonIn(param.getCreateReasons());
        }
        return example;
    }
}
