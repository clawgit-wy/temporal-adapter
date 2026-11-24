package com.yutong.safety.core.service.build;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildManageProcessDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyEmpNoVO;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyVO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildPowerDOBizMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildPowerDOMapper;
import com.yutong.safety.core.dto.build.SafetyBuildPowerDTO;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SafetyBuildPowerService {

    @Autowired
    private SafetyBuildPowerDOMapper safetyBuildPowerDOMapper;
    @Autowired
    private SafetyBuildPowerDOBizMapper safetyBuildPowerDOBizMapper;

    public List<SafetyBuildPowerDO> selectList(BuildPowerListParam listParam){
        SafetyBuildPowerDOExample example = formatExample(listParam);
        return safetyBuildPowerDOMapper.selectByExample(example);
    }

    /**
     * 参数整理
     * @param param 入参
     * @return example
     */
    private SafetyBuildPowerDOExample formatExample(BuildPowerListParam param){
        SafetyBuildPowerDOExample example = new SafetyBuildPowerDOExample();
        SafetyBuildPowerDOExample.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(param.getDeviceCodeList())){
            criteria.andDeviceCodeIn(param.getDeviceCodeList());
        }
        if(StringUtils.isNotBlank(param.getEmpNo())){
            criteria.andEmployeeCodeEqualTo(param.getEmpNo());
        }
        if(StringUtils.isNotBlank(param.getDutyDeptCode())){
            criteria.andDutyDeptCodeEqualTo(param.getDutyDeptCode());
        }
        if(!CollectionUtils.isEmpty(param.getDutyDeptCodeList())){
            criteria.andDutyDeptCodeIn(param.getDutyDeptCodeList());
        }
        criteria.andIsDeleteEqualTo("0");
        example.setOrderByClause(" update_time desc");
        if(param.getPageFlag() != null){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
            if(StringUtils.isNotBlank(param.getOrderByClause())){
                example.setOrderByClause(param.getOrderByClause());
            }
        }
        return example;
    }

    public void insert(SafetyBuildPowerDO param){
        safetyBuildPowerDOMapper.insertSelective(param);
    }

    public void deleteByDeptCode(String deptCode,String remark){
        safetyBuildPowerDOBizMapper.deleteByDeptCode(deptCode,remark);
    }

    public void batchInsert(List<SafetyBuildPowerDO> list){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<List<SafetyBuildPowerDO>> partition = ListUtils.partition(list,500);
        partition.forEach(e->safetyBuildPowerDOBizMapper.insertSafetyBuildPower(e));
    }

    public void batchUpdate(List<String> dutyDeptCode, SafetyBuildManageProcessDO processDTO){
        safetyBuildPowerDOBizMapper.batchUpdate(dutyDeptCode,processDTO);
    }

    public List<SafetyBuildPowerDTO> selectPageList(SafetyBuildPowerDTO safetyBuildPowerDTO) {
        return safetyBuildPowerDOBizMapper.selectPageList(safetyBuildPowerDTO);
    }

    public List<SafetyBuildPowerDTO> selectPageListExport(SafetyBuildPowerDO param) {
        return safetyBuildPowerDOBizMapper.selectPageListExport(param);
    }

    public long selectListCount(SafetyBuildPowerDTO safetyBuildPowerDTO) {
        return safetyBuildPowerDOBizMapper.selectCountPageList(safetyBuildPowerDTO);
    }

    public List<SafetyBuildPowerDTO> getPeopleList(SafetyBuildPowerDTO param) {
        return safetyBuildPowerDOBizMapper.getPeopleList(param);
    }

    public void updateData(SafetyBuildPowerDO en) {
        en.setUpdateTime(DateUtils.format(new Date(),2));
        safetyBuildPowerDOBizMapper.updateData(en);

        en.setBuildNum(null);
        safetyBuildPowerDOMapper.updateByPrimaryKeySelective(en);
    }

    public List<SafetyBuildPowerDO> selectListByDept(String deptCode){
        if(StringUtils.isBlank(deptCode)){return null;}

        String[] parts = deptCode.split(",");
        List<String> list = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String part : parts) {
            if (current.length() > 0) {
                current.append(",");
            }
            current.append(part);
            list.add(current.toString());
        }

        SafetyBuildPowerDOExample example = new SafetyBuildPowerDOExample();
        SafetyBuildPowerDOExample.Criteria criteria = example.createCriteria();
        criteria.andDutyDeptCodeIn(list);
        criteria.andIsDeleteEqualTo("0");
        example.setOrderByClause(" length(DUTY_DEPT_CODE) desc");

        List<SafetyBuildPowerDO> doList = safetyBuildPowerDOMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(doList)){return null;}

        List<SafetyBuildPowerDO> resultList = new ArrayList<>();
        for(SafetyBuildPowerDO en : doList){
            if(!en.getDutyDeptCode().equals(doList.get(0).getDutyDeptCode())){
                break;
            }
            resultList.add(en);
        }

        return resultList;
    }

    public List<SafetyBuildPowerDO> getListByUserCode(List<String> userCodeList){
        return safetyBuildPowerDOBizMapper.getListByUserCode(userCodeList);
    }

    public List<SafetyBuildPowerDTO> getListWithPosByUserCode(List<String> userCodeList){
        return safetyBuildPowerDOBizMapper.getListWithPosByUserCode(userCodeList);
    }

    public List<SafetyBuildPowerDO> getListByDeviceCode(List<String> deviceCodeList){
        SafetyBuildPowerDOExample example = new SafetyBuildPowerDOExample();
        SafetyBuildPowerDOExample.Criteria criteria = example.createCriteria();
        if (!CollectionUtils.isEmpty(deviceCodeList)) {
            criteria.andDeviceCodeIn(deviceCodeList);
        }
        criteria.andIsDeleteEqualTo("0");

        return safetyBuildPowerDOMapper.selectByExample(example);
    }

    public List<SafetyBuildPowerDO> getWithOutResinPeople(List<String> deptCodeList,String empNo){
        SafetyBuildPowerDOExample example = new SafetyBuildPowerDOExample();
        SafetyBuildPowerDOExample.Criteria criteria = example.createCriteria();
        criteria.andDutyDeptCodeIn(deptCodeList);
        criteria.andIsDeleteEqualTo("0");
        criteria.andEmployeeCodeNotEqualTo(empNo);
        return safetyBuildPowerDOMapper.selectByExample(example);
    }

    /**
     * 根据组织编码，获取对应的责任部门
     * @param orgCodeList 组织编码集合
     * @return
     */
    public List<BuildGroupDutyVO> getGroupDuty(List<String> orgCodeList){
        return safetyBuildPowerDOBizMapper.getGroupDuty(orgCodeList);
    }

    public List<SafetyBuildPowerDO> getDutyEmpByGroup(){
        return safetyBuildPowerDOBizMapper.getDutyEmpByGroup();
    }

    public List<SafetyBuildPowerDO> getDutyInfoByDevice(List<String> deviceCodeList){
        return safetyBuildPowerDOBizMapper.getDutyInfoByDevice(deviceCodeList);
    }

    public List<SafetyBuildPowerDO> getOnyDutyDeptList(List<String> dutyDeptList){
        return safetyBuildPowerDOBizMapper.getOnyDutyDeptList(dutyDeptList);
    }

    /**
     * 根据组织编码，获取对应的责任部门门禁ID
     * @param orgCode 组织ID
     * @return 返回需要的数据， 见实体类
     */
    public List<BuildGroupDutyEmpNoVO> getGroupDutyDeviceCode(String orgCode){
        return safetyBuildPowerDOBizMapper.getGroupDutyDeviceCode(orgCode);
    }

    public int deleteByExample(SafetyBuildPowerDOExample example){
        return safetyBuildPowerDOMapper.deleteByExample(example);
    }

    public int updateByExample(SafetyBuildPowerDO record,SafetyBuildPowerDOExample example){
        return safetyBuildPowerDOMapper.updateByExampleSelective(record,example);
    }
}

