package com.yutong.modules.user.service.impl;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.enums.ProcessOpTypeEnum;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.IYtsafetyResponsibleProcessDetailsService;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import com.yutong.modules.user.dto.YtsafetyResponsibleProcessDTO;
import com.yutong.modules.user.entity.YtsafetyResponsibleProcess;
import com.yutong.modules.user.mapper.YtsafetyResponsibleProcessMapper;
import com.yutong.modules.user.model.SysDictSelect;
import com.yutong.modules.user.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 安全责任人流程信息表
 * @Author: lvss
 * @Date:   2026-06-22
 * @Version: V1.0
 */
@Service
public class YtsafetyResponsibleProcessServiceImpl extends ServiceImpl<YtsafetyResponsibleProcessMapper, YtsafetyResponsibleProcess> implements IYtsafetyResponsibleProcessService {

    @Autowired
    private IBpmProcessService bpmProcessService;

    @Autowired
    private IYtsafetyResponsibleProcessDetailsService responsibleProcessDetailsService;

    @Autowired
    private YtsafetyResponsibleProcessMapper responsibleProcessMapper;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private ISafetyCommonService safetyCommonService;


    @Value("${ytsafety.custom.constant.safetySupervisor}")
    private String safetySupervisor;

    @Override
    public YtsafetyResponsibleProcess save(YtsafetyResponsibleProcessDTO safetyResponsibleProcessDTO) {
        YtsafetyResponsibleProcess responsibleProcess = new YtsafetyResponsibleProcess();
        BeanUtils.copyProperties(safetyResponsibleProcessDTO,responsibleProcess);
        Long id = IdWorker.getId();
        LoginUser user = BusUtits.getLoginUser();

        responsibleProcess.setId(id);
        responsibleProcess.setApplyDepartCode(user.getOrgCode());
        responsibleProcess.setApplyDepartName(user.getOrgName());
        responsibleProcess.setApplyName(user.getRealname());
        responsibleProcess.setApplyNo(user.getEmpNo());
        responsibleProcess.setApplyPhone(user.getPhone());
        responsibleProcess.setChangeType(safetyResponsibleProcessDTO.getChangeType());
        responsibleProcess.setFlowCode(generateTaskCode());
        responsibleProcessMapper.insert(responsibleProcess);
        responsibleProcessDetailsService.save(responsibleProcess.getId(),safetyResponsibleProcessDTO.getDetails());
        submitProcess(responsibleProcess,safetyResponsibleProcessDTO.getOpType());
        return responsibleProcess;
    }

    private void submitProcess(YtsafetyResponsibleProcess responsibleProcess,Integer opType){
        LoginUser user = BusUtits.getLoginUser();
        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(responsibleProcess.getModelFlag());
        dto.setFormInstanceId(responsibleProcess.getId());
        dto.setFlowCode(responsibleProcess.getFlowCode());
        dto.setFlowSubject(responsibleProcess.getFlowSubject());
        dto.setCurNodeHandler(responsibleProcess.getSuperApproval());
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
        dto.setApplyName(user.getRealname());
        dto.setLoginName(user.getEipName());
        dto.setApplyDepartCode(user.getOrgCode());
        dto.setApplyDepartName(user.getOrgName());
        dto.setPreNodeHandler(user.getEipName());
        dto.setPreNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        dto.setFlowCode(responsibleProcess.getFlowCode());
        if(opType== null||opType== ProcessOpTypeEnum.draft.getCode()){
            safetySummaryProcessService.saveDraft(dto);
        }else{
            safetySummaryProcessService.createAndApproveProcess(dto);
        }
    }

    @Override
    public YtsafetyResponsibleProcess edit(YtsafetyResponsibleProcessDTO safetyResponsibleProcessDTO) {
        YtsafetyResponsibleProcess responsibleProcess = responsibleProcessMapper.selectById(safetyResponsibleProcessDTO.getId());
        Long id = IdWorker.getId();
        LoginUser user = BusUtits.getLoginUser();
        responsibleProcess.setApplyReason(safetyResponsibleProcessDTO.getApplyReason());
        responsibleProcess.setSuperApproval(safetyResponsibleProcessDTO.getSuperApproval());
        responsibleProcess.setSuperApprovalName(safetyResponsibleProcessDTO.getSuperApprovalName());
        responsibleProcessMapper.updateById(responsibleProcess);
        responsibleProcessDetailsService.save(responsibleProcess.getId(),safetyResponsibleProcessDTO.getDetails());
        submitProcess(responsibleProcess,safetyResponsibleProcessDTO.getOpType());
        return responsibleProcess;
    }

    @Override
    public List<YtsafetyResponsibleChangeVO> queryChangeUser(String userInfo) {
        if (StringUtils.isEmpty(userInfo)) return new ArrayList<YtsafetyResponsibleChangeVO>();
        List<YtsafetyResponsibleChangeVO> vos = responsibleProcessMapper.queryChangePerson(userInfo);
        if (vos == null || vos.isEmpty()) return new ArrayList<YtsafetyResponsibleChangeVO>();
        for (YtsafetyResponsibleChangeVO vo : vos) {
            String orgName = vo.getOrgName();
            if (StringUtils.isEmpty(orgName)) continue;
            orgName = BusUtits.reverseCommaStr(orgName);
            vo.setOrgName(orgName);
        }
        return vos;
    }

    @Override
    public YtsafetyResponsibleChangeDropVO queryChangeAuth(Long userId) {
        List<SysDictSelect> roles = responsibleProcessMapper.queryRoles(userId);
        List<SysDictSelect> departIds = responsibleProcessMapper.queryDeparts(userId);
        List<SysDictSelect> factoryIds = responsibleProcessMapper.queryFactorys(userId);
        YtsafetyResponsibleChangeDropVO vo = new YtsafetyResponsibleChangeDropVO();
        vo.setUserId(userId);
        vo.setRoleId(roles);
        vo.setDepartId(departIds);
        vo.setFactoryId(factoryIds);
        return vo;
    }

    @Override
    public YtsafetyResponsibleProcessVO queryResponsibleProcess(Long id) {
        YtsafetyResponsibleProcessVO vo = responsibleProcessMapper.queryById(id);
        LoginUser user = BusUtits.getLoginUser();
        if(StringUtils.isNotEmpty(vo.getSuperApproval()) ) {
            List<String> empLogins = Arrays.stream(vo.getSuperApproval().split(",")).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            List<SafetyEmployeeMessageVO> supperUserInfo = responsibleProcessMapper.querySuperApproves(empLogins);
            vo.setSuperApproves(supperUserInfo);
        }
        BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(vo.getId(),user.getEipName());
        BeanUtils.copyProperties(detailsVO,vo);
        List<YtsafetyResponsibleProcessDetailsVO> detailsVOS = responsibleProcessDetailsService.queryList(id);
        vo.setDetails(detailsVOS);
        return vo;
    }

    @Override
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO) {
        LoginUser user = BusUtits.getLoginUser();
        processParamDTO.setLoginName(user.getEipName());
        processParamDTO.setNodeHandler(user.getEipName());
        processParamDTO.setNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        return safetySummaryProcessService.executeProcess(processParamDTO);
    }

    @Override
    public List<YtsafetyResponsibleChangeVO> querySuperApprove() {
        LoginUser user = BusUtits.getLoginUser();
        return responsibleProcessMapper.querySuperApprove(safetySupervisor,user.getOrgCode());
    }

    @Lock4j(name = "ytsafety:task:aqzrr:cache")
    public String generateTaskCode() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateStr = dateFormat.format(new Date());
            String redisKey = "ytsafety:task:code:aqzrr" + dateStr;
            Long sequence = redisUtil.incr(redisKey,1) ;
            return String.format("AQZRR%s%04d", dateStr, sequence);
        } catch (Exception e) {
            log.error("生成任务编号时Redis操作失败", e);
            throw new RuntimeException("生成任务编号失败：" + e.getMessage(), e);
        }
    }

}
