package com.yutong.safety.core.service.park;

import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDOExample;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerInfoDOBizMapper;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerInfoDOMapper;
import com.yutong.safety.core.db.mapper.park.SafetyParkPowerProcessDOBizMapper;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.park.SafetyParkPowerProcessDTO;
import com.yutong.safety.core.service.SafetyBpmWebServiceService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SafetyParkPowerProcessService {
    @Autowired
    SafetyParkPowerProcessDOBizMapper safetyParkPowerProcessDOMapper;

    @Autowired
    SafetyBpmWebServiceService safetyBpmWebServiceService;

    @Autowired
    SafetyParkEmployeePowerInfoDOBizMapper safetyParkEmployeePowerInfoDOBizMapper;

    public Integer save(SafetyParkPowerProcessDO process) {
        return safetyParkPowerProcessDOMapper.insert(process);
    }

    public void update(SafetyParkPowerProcessDO process) {
        safetyParkPowerProcessDOMapper.updateByPrimaryKeySelective(process);
    }

    public List<SafetyParkPowerProcessDO> getDetailById(SafetyParkPowerProcessDO en){
        SafetyParkPowerProcessDOExample ex = new SafetyParkPowerProcessDOExample();
        SafetyParkPowerProcessDOExample.Criteria criteria = ex.createCriteria();

        if (StringUtils.isNotBlank(en.getId())){
            criteria.andIdEqualTo(en.getId());
        }

        if (StringUtils.isNotBlank(en.getBpmFlowId())){
            criteria.andBpmFlowIdEqualTo(en.getBpmFlowId());
        }

        return safetyParkPowerProcessDOMapper.selectByExample(ex);
    }

    public List<SafetyParkPowerProcessDTO> selectList(SafetyParkPowerProcessDTO param){
        //加入流程链接前缀
        String bpmUrlPre = getBpmPreUrl("safety_park_power_process");
        param.setBpmUrlPre(bpmUrlPre);

        return safetyParkPowerProcessDOMapper.selectList(param);
    }

    public long selectListCount(SafetyParkPowerProcessDTO param){
        return safetyParkPowerProcessDOMapper.selectListCount(param);
    }

    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }

    public List<SafetyEmployeePrivateCarDO> getListByUserCode(List<String> userCodeList) {
        return safetyParkPowerProcessDOMapper.getListByUserCode(userCodeList);
    }

    public List<ParamDTO> getUserList(String processId){
        return safetyParkPowerProcessDOMapper.getUserList(processId);
    }

    public List<ParamDTO> getCarList(List<ParamDTO> userList){
        return safetyParkPowerProcessDOMapper.getCarList(userList);
    }

    public List<ParamDTO> getCarListForEmpNos(List<String> empNoList){
        return safetyParkPowerProcessDOMapper.getCarListForEmpNos(empNoList);
    }

    public List<ParamDTO> getUserListByEmpNo(String empNos,String userType){
        return safetyParkPowerProcessDOMapper.getUserListByEmpNo(empNos,userType);
    }

    public List<ParamDTO> getTimeOutData(List<String> userCodeList){
        return safetyParkEmployeePowerInfoDOBizMapper.getUserList(userCodeList);
    }

    public void dealPower(List<SafetyParkEmployeePowerInfoDO> successList,String userType){
        if("ZS".equals(userType)){
            List<List<SafetyParkEmployeePowerInfoDO>> partition = ListUtils.partition(successList, 200);
            partition.forEach(li -> safetyParkEmployeePowerInfoDOBizMapper.saveOrUpdate(li));
        }
    }

    public void dealPowerTemp(List<SafetyParkEmployeePowerInfoDO> successList){
        List<List<SafetyParkEmployeePowerInfoDO>> partition = ListUtils.partition(successList, 200);
        partition.forEach(li -> safetyParkEmployeePowerInfoDOBizMapper.saveOrUpdateTemp(li));
    }

    public SafetyParkPowerProcessDO selectByPrimaryKey(String processId){
        return safetyParkPowerProcessDOMapper.selectByPrimaryKey(processId);
    }
}
