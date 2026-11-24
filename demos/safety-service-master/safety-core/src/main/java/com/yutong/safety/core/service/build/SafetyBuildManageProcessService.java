package com.yutong.safety.core.service.build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutEventProcessDO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildManageProcessDOBizMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildManageProcessDOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildPowerDOBizMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildPowerDOMapper;
import com.yutong.safety.core.dto.build.SafetyBuildManageProcessDTO;
import com.yutong.safety.core.dto.build.SafetyBuildPowerDTO;
import com.yutong.safety.core.service.SafetyBpmWebServiceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SafetyBuildManageProcessService {
    @Autowired
    SafetyBuildManageProcessDOMapper safetyBuildManageProcessDOMapper;
    @Autowired
    SafetyBuildManageProcessDOBizMapper safetyBuildManageProcessDOBizMapper;
    @Autowired
    SafetyBuildPowerDOMapper safetyBuildPowerDOMapper;
    @Autowired
    SafetyBpmWebServiceService safetyBpmWebServiceService;

    public Integer save(SafetyBuildManageProcessDO process) {
        return safetyBuildManageProcessDOMapper.insert(process);
    }

    public void update(SafetyBuildManageProcessDO process) {
        safetyBuildManageProcessDOMapper.updateByPrimaryKeySelective(process);
    }

    public List<SafetyBuildManageProcessDO> getList(SafetyBuildManageProcessDO process){
        SafetyBuildManageProcessDOExample example = new SafetyBuildManageProcessDOExample();
        SafetyBuildManageProcessDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(process.getId())){
            criteria.andIdEqualTo(process.getId());
        }
        if (StringUtils.isNotBlank(process.getBpmFlowId())){
            criteria.andBpmFlowIdEqualTo(process.getBpmFlowId());
        }

        return safetyBuildManageProcessDOMapper.selectByExample(example);
    }

    public List<SafetyBuildPowerDO> getGuardInfoListByDept(String dutyDeptCode){
        SafetyBuildPowerDOExample example = new SafetyBuildPowerDOExample();
        SafetyBuildPowerDOExample.Criteria criteria = example.createCriteria();
        criteria.andDutyDeptCodeLike("%"+dutyDeptCode);
        criteria.andIsDeleteEqualTo("0");

        return safetyBuildPowerDOMapper.selectByExample(example);
    }

    public SafetyBuildManageProcessDO getDetailById(String id){
        return safetyBuildManageProcessDOMapper.selectByPrimaryKey(id);
    }

    public List<SafetyBuildManageProcessDTO> selectList(SafetyBuildManageProcessDTO param){
        //加入流程链接前缀
        String bpmUrlPre = getBpmPreUrl("safety_build_manage_process");
        param.setBpmUrlPre(bpmUrlPre);
        List<SafetyBuildManageProcessDTO> list = safetyBuildManageProcessDOBizMapper.selectList(param);

        if (CollectionUtils.isEmpty(list)) {return Collections.emptyList();}
        for (SafetyBuildManageProcessDTO dto : list) {
            String newDeviceName = dto.getNewDeviceName();
            String combFactoryName = dto.getCombFactoryName();
            String combBuild = dto.getCombBuild();

            if(StringUtils.isBlank(newDeviceName)){continue;}
            List<SafetyBuildDeviceDO> en = new ArrayList<>();
            List<String> list1 = Arrays.asList(newDeviceName.split(","));

            List<String> list2 = new ArrayList<>();
            List<String> list3 = new ArrayList<>();
            if(!StringUtils.isBlank(combFactoryName)){
                list2 = Arrays.asList(combFactoryName.split(","));
            }
            if(!StringUtils.isBlank(combBuild)){
                list3 = Arrays.asList(combBuild.split(","));
            }

            for (int i = 0; i < list1.size(); i++) {
                SafetyBuildDeviceDO e = new SafetyBuildDeviceDO();
                e.setDeviceName(list1.get(i));
                e.setAnFactoryName(list2.size()==list1.size()?list2.get(i):"");
                e.setAnBuilding(list3.size()==list1.size()?list3.get(i):"");
                en.add(e);
            }
            dto.setBuildList(en);
        }

        return list;
    }

    /**
     * 拼接bpm链接
     * @param formId
     * @return 返回值
     */
    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }

    public long selectListCount(SafetyBuildManageProcessDTO param){
        return safetyBuildManageProcessDOBizMapper.selectListCount(param);
    }
}
