package com.yutong.safety.core.service.park;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDOExample;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerInfoDOBizMapper;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerInfoDOMapper;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.park.SafetyParkEmployeePowerInfoDTO;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class SafetyParkEmployeePowerInfoService {
    @Autowired
    SafetyParkEmployeePowerInfoDOMapper safetyParkEmployeePowerInfoDOMapper;
    @Autowired
    private SafetyParkEmployeePowerInfoDOBizMapper safetyParkEmployeePowerInfoDOBizMapper;

    /**
     * 检索条件
     * @param param
     */
    public List<SafetyParkEmployeePowerInfoDO> selectList(ParkEmployeePowerListParam param){
        SafetyParkEmployeePowerInfoDOExample example = formatExample(param);
        if(param.getPageFlag()){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        example.setOrderByClause(" POWER_END_TIME desc");
        return safetyParkEmployeePowerInfoDOMapper.selectByExample(example);
    }

    public List<SafetyParkEmployeePowerInfoDTO> selectListByEmpNo(String empNo){
        return safetyParkEmployeePowerInfoDOBizMapper.selectListByEmpNo(empNo);
    }

    /**
     * 获取符合条件的条数
     * @param param
     * @return
     */
    public Long selectCount(ParkEmployeePowerListParam param){
        SafetyParkEmployeePowerInfoDOExample example = formatExample(param);
        return safetyParkEmployeePowerInfoDOMapper.countByExample(example);
    }

    private SafetyParkEmployeePowerInfoDOExample formatExample(ParkEmployeePowerListParam param){
        SafetyParkEmployeePowerInfoDOExample example = new SafetyParkEmployeePowerInfoDOExample();
        SafetyParkEmployeePowerInfoDOExample.Criteria criteria = example.createCriteria();
        if(!Collections.isEmpty(param.getEmpNoList())){
            criteria.andEmployeeCodeIn(param.getEmpNoList());
        }
        if(!StringUtils.isEmpty(param.getEmpNo())){
            criteria.andEmployeeCodeEqualTo(param.getEmpNo());
        }
        if(!StringUtils.isEmpty(param.getDownMark())){
            criteria.andDownMarkEqualTo(param.getDownMark());
        }
        if(!StringUtils.isEmpty(param.getDeleteFlag())){
            criteria.andIsDeleteEqualTo(Short.valueOf(param.getDeleteFlag()));
        }
        if(!Collections.isEmpty(param.getFactoryIdList())){
            criteria.andFactoryIdIn(param.getFactoryIdList());
        }

        return example;
    }

    public List<SafetyParkEmployeePowerInfoDO> getDetailById(String userCode) {
        SafetyParkEmployeePowerInfoDO en = new SafetyParkEmployeePowerInfoDO();
        en.setEmployeeCode(userCode);
        ParkEmployeePowerListParam listParam = new ParkEmployeePowerListParam();
        listParam.setEmpNo(userCode);
        return safetyParkEmployeePowerInfoDOBizMapper.getDetailByCon(listParam);
    }

    public List<SafetyParkEmployeePowerInfoDO> getDetailByCon(ParkEmployeePowerListParam listParam){
        return safetyParkEmployeePowerInfoDOBizMapper.getDetailByCon(listParam);
    }

    /**
     * 获取员工当前有效的权限信息
     * @param userCode 员工号
     * @return
     */
    public List<SafetyParkEmployeePowerInfoDO> getValidPowerByEmp(String userCode) {
        SafetyParkEmployeePowerInfoDOExample example = new SafetyParkEmployeePowerInfoDOExample();
        SafetyParkEmployeePowerInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andEmployeeCodeEqualTo(userCode);
        criteria.andIsDeleteEqualTo((short) 0);//未删除状态
        criteria.andPowerEndTimeGreaterThan(DateUtils.format(new Date(),1));
        return safetyParkEmployeePowerInfoDOMapper.selectByExample(example);
    }

    /**
     * 取消置顶员工的厂区权限
     * @param record 更新信息
     * @param empNo 员工号
     * @param factoryCodes 取消的厂区权限
     * @return
     */
    public int cancelEmployeePower(SafetyParkEmployeePowerInfoDO record,String empNo,String factoryCodes){
        SafetyParkEmployeePowerInfoDOExample example = new SafetyParkEmployeePowerInfoDOExample();
        SafetyParkEmployeePowerInfoDOExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(empNo)){
            List<String> empNoList = Arrays.asList(factoryCodes.split(","));
            criteria.andEmployeeCodeIn(empNoList);
        }
        if(!StringUtils.isEmpty(factoryCodes)){
            List<String> factoryIdList = Arrays.asList(factoryCodes.split(","));
            criteria.andFactoryIdIn(factoryIdList);
        }

        return safetyParkEmployeePowerInfoDOMapper.updateByExampleSelective(record,example);
    }

    /**
     * 修改某个人的多个厂区编码数据
     * @param record 更新的信息
     * @param empNo 员工号
     * @param factoryCodes 厂区编码
     * @return 返回修改的数量
     */
    public int cancelEmployeePower(SafetyParkEmployeePowerInfoDO record,String empNo,List<String> factoryCodes){
        SafetyParkEmployeePowerInfoDOExample example = new SafetyParkEmployeePowerInfoDOExample();
        SafetyParkEmployeePowerInfoDOExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(empNo)){
            criteria.andEmployeeCodeEqualTo(empNo);
        }
        if(!Collections.isEmpty(factoryCodes)){
            criteria.andFactoryIdIn(factoryCodes);
        }
        return safetyParkEmployeePowerInfoDOMapper.updateByExampleSelective(record,example);
    }

    public List<SafetyParkEmployeePowerInfoDO> getList(SafetyParkEmployeePowerInfoDO param){
        return safetyParkEmployeePowerInfoDOBizMapper.getList(param);
    }

    public List<ParamDTO> getUserList( List<String> list){
        return safetyParkEmployeePowerInfoDOBizMapper.getUserList(list);
    }

    /**
     * 获取正式工下发权限的数据
     * @return 数据集
     */
    public List<ParamDTO> selectListByDownMark(List<String> empNos) {
        return safetyParkEmployeePowerInfoDOBizMapper.selectListByDownMark(empNos);
    }

    /**
     * 批量修改下发状态
     * @param zsOpen
     * @param type 0:下发数据， 1：注销数据
     */
    public void updateZsStatus(List<String> zsOpen, String type) {
        safetyParkEmployeePowerInfoDOBizMapper.updateZsStatus(zsOpen, type);
    }

    public void deleteByExample(SafetyParkEmployeePowerInfoDOExample example,String remark){
        SafetyParkEmployeePowerInfoDO record = new SafetyParkEmployeePowerInfoDO();
        record.setIsDelete(Short.valueOf("1"));
        record.setUpdateTime(new Date());
        record.setRemark(remark);
        safetyParkEmployeePowerInfoDOMapper.updateByExampleSelective(record,example);
    }

    /**
     * 获取指定员工的所有厂区权限信息
     * @param zsEmpNos 员工号
     * @return 集合
     */
    public List<ParamDTO> selectByEmpNoList(List<String> zsEmpNos) {
        return safetyParkEmployeePowerInfoDOBizMapper.selectByEmpNoList(zsEmpNos);
    }
}
