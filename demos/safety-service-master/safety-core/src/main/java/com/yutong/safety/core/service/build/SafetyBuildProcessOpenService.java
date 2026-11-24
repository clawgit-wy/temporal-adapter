package com.yutong.safety.core.service.build;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessCloseDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessCloseDOExample;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessOpenDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessOpenDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildProcessOpenListParam;
import com.yutong.safety.core.db.mapper.build.SafetyBuildProcessCloseDOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildProcessOpenDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyBuildProcessOpenService {

    @Autowired
    private SafetyBuildProcessOpenDOMapper safetyBuildProcessOpenDOMapper;

    @Autowired
    private SafetyBuildProcessCloseDOMapper safetyBuildProcessCloseDOMapper;


    public List<SafetyBuildProcessOpenDO> selectOpenList(String processId,String appluStatus) {
        SafetyBuildProcessOpenDOExample example = new SafetyBuildProcessOpenDOExample();
        SafetyBuildProcessOpenDOExample.Criteria criteria = example.createCriteria();
        criteria.andProcessIdEqualTo(processId);
        //排除不通过的
        if(StringUtils.isNotBlank(appluStatus)){
            criteria.andApplyStatusNotEqualTo("2");
        }

        return safetyBuildProcessOpenDOMapper.selectByExample(example);
    }

    public List<SafetyBuildProcessCloseDO> selectCloseList(String processId) {
        SafetyBuildProcessCloseDOExample example = new SafetyBuildProcessCloseDOExample();
        example.createCriteria().andProcessIdEqualTo(processId);
        return safetyBuildProcessCloseDOMapper.selectByExample(example);
    }

    /**
     * 根据设备ID和员工号获取楼宇门禁权限申请开通明细中的部门信息
     * @param deviceCode  设备ID
     * @param employeeCode 员工号
     * @return 返回开通明细的设备信息
     */
    public SafetyBuildProcessOpenDO selectProcessDeptInfo(String deviceCode, String employeeCode) {
        return safetyBuildProcessOpenDOMapper.selectProcessDeptInfo(deviceCode, employeeCode);
    }
}
