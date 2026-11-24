package com.yutong.safety.core.service.spot;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.vo.web.spot.PatrolTaskReportVO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolTaskDO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolTaskDOExample;
import com.yutong.safety.core.db.mapper.login.LoginMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolTaskDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolTaskDOMapper;
import com.yutong.safety.core.service.SafetySysUserService;
import net.bytebuddy.asm.Advice;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetyPatrolTaskService {

    @Autowired
    private SafetyPatrolTaskDOMapper safetyPatrolTaskDOMapper;

    @Autowired
    private SafetyPatrolTaskDOBizMapper safetyPatrolTaskDOBizMapper;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private LoginMapper loginMapper;

    /**
     * 查询统计治安巡查任务
     * @param param 入参
     * @return 返回值
     */
    public List<PatrolTaskReportVO> selectPatrolTaskReport(PatrolTaskListParam param) {
        String patrolEmpNo = param.getPatrolEmpNo();
        if(StringUtils.isNotEmpty(patrolEmpNo)){
            String factoryStr =  safetySysUserService.getPowerFactory(patrolEmpNo);
            param.setFactoryStr(factoryStr);
        }
        return safetyPatrolTaskDOBizMapper.selectPatrolTaskReport(param);
    }

    /**
     * 查询统计治安巡查任务
     * @param param 入参
     * @return 返回值
     */
    public List<PatrolTaskReportVO> selectPatrolTODODetail(PatrolTaskListParam param) {
        String patrolEmpNo = param.getPatrolEmpNo();
        if(StringUtils.isNotEmpty(patrolEmpNo)){
            String factoryStr =  safetySysUserService.getPowerFactory(patrolEmpNo);
            param.setFactoryStr(factoryStr);
        }
        return safetyPatrolTaskDOBizMapper.selectPatrolTODODetail(param);
    }

    /**
     * 查询统计治安巡查任务
     * @param param 入参
     * @return 返回值
     */
    public List<PatrolTaskReportVO> selectDetailSummary(PatrolTaskListParam param) {
        String patrolEmpNo = param.getPatrolEmpNo();
        if(StringUtils.isNotEmpty(patrolEmpNo)){
            String factoryStr =  safetySysUserService.getPowerFactory(patrolEmpNo);
            param.setFactoryStr(factoryStr);
        }
        return safetyPatrolTaskDOBizMapper.selectDetailSummary(param);
    }


    public List<SafetyPatrolTaskDO> getList(PatrolTaskListParam param){
        SafetyPatrolTaskDOExample example = new SafetyPatrolTaskDOExample();
        SafetyPatrolTaskDOExample.Criteria criteria = example.createCriteria();
        if(param.getPlanId() != null){
            criteria.andPlanIdEqualTo(param.getPlanId());
        }
        if(StringUtils.isNotEmpty(param.getFactoryId())){
            criteria.andFactoryIdEqualTo(Integer.parseInt(param.getFactoryId()));
        }
        if(StringUtils.isNotEmpty(param.getTaskBeginDate())){
            criteria.andTaskBeginDateEqualTo(param.getTaskBeginDate());
        }
        if(StringUtils.isNotEmpty(param.getTaskEndDate())){
            criteria.andTaskEndDateEqualTo(param.getTaskEndDate());
        }
        if(StringUtils.isNotEmpty(param.getPatrolEmpNo())){
            criteria.andUserCodeEqualTo(param.getPatrolEmpNo());
        }
        if(StringUtils.isNotEmpty(param.getPatrolEmpNo())){
            criteria.andUserCodeEqualTo(param.getPatrolEmpNo());
        }
        return safetyPatrolTaskDOMapper.selectByExample(example);
    }
}
