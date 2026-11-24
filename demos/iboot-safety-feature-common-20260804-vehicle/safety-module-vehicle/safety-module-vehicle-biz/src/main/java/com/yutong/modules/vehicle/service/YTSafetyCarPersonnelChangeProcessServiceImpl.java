package com.yutong.modules.vehicle.service;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.bpm.FieldAuth;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.api.YTSafetyWaterFlowNoServiceApi;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.vo.YtsafetySysResponsibleVO;
import com.yutong.modules.vehicle.api.ISafetyEmployeePrivateCarService;
import com.yutong.modules.vehicle.api.IYTSafetyCarPersonnelChangeInfoService;
import com.yutong.modules.vehicle.api.IYTSafetyCarPersonnelChangeProcessService;
import com.yutong.modules.vehicle.constants.CarPersonnelChangeConstant;
import com.yutong.modules.vehicle.dto.YTSafetyCarPersonnelChangeInfoDTO;
import com.yutong.modules.vehicle.dto.YTSafetyCarPersonnelChangeProcessDTO;
import com.yutong.modules.vehicle.entity.SafetyEmployeePrivateCar;
import com.yutong.modules.vehicle.entity.YTSafetyCarPersonnelChangeInfo;
import com.yutong.modules.vehicle.entity.YTSafetyCarPersonnelChangeProcess;
import com.yutong.modules.vehicle.enums.CarPersonnelChangeProcessStatus;
import com.yutong.modules.vehicle.mapper.YTSafetyCarPersonnelChangeProcessMapper;
import com.yutong.modules.vehicle.vo.YTSafetyCarPersonnelChangeInfoVO;
import com.yutong.modules.vehicle.vo.YTSafetyCarPersonnelChangeProcessVO;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 车辆人员变更流程表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public class YTSafetyCarPersonnelChangeProcessServiceImpl extends ServiceImpl<YTSafetyCarPersonnelChangeProcessMapper, YTSafetyCarPersonnelChangeProcess> implements IYTSafetyCarPersonnelChangeProcessService {

    @Autowired
    IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    ISafetyEmployeePrivateCarService safetyEmployeePrivateCarService;

    @Autowired
    IYTSafetyCarPersonnelChangeInfoService safetyCarPersonnelChangeInfoService;

    @Autowired
    YTSafetyWaterFlowNoServiceApi ytSafetyWaterFlowNoService;

    @Autowired
    ISafetyCommonService commonService;

    @Autowired
    RoleCodeConstant roleCodeConstant;

    @Autowired
    ISafetyEmployeeMessageService employeeMessageService;

    void carTurnover(String employeeNo,String personnelChangeType){
        List<SafetyEmployeePrivateCar> carList = safetyEmployeePrivateCarService.queryOfficialCarByEmployeeNo(employeeNo);
        String clrybg = ytSafetyWaterFlowNoService.getNowByTypeDay("CLRYBG", null);
        YTSafetyCarPersonnelChangeProcess carPersonnelChangeProcess = new YTSafetyCarPersonnelChangeProcess();
        carPersonnelChangeProcess.setId(IdWorker.getId());
        carPersonnelChangeProcess.setCarPersonnelChangeCode(clrybg);
        carPersonnelChangeProcess.setPersonnelChangeType(CarPersonnelChangeConstant.PersonnelChangeType.DIMISSION);
        carPersonnelChangeProcess.setModelFlag(CarPersonnelChangeConstant.MODEL_ID);
        carPersonnelChangeProcess.setFlowCode(clrybg);
        carPersonnelChangeProcess.setFlowSubject("公务车人员变更");
//        carPersonnelChangeProcess.setApplyNo();
//        carPersonnelChangeProcess.setApplyName();
//        carPersonnelChangeProcess.setApplyDepartCode();
//        carPersonnelChangeProcess.setApplyDepartName();
        SafetyEmployeeMessage employee = employeeMessageService.getByNo(employeeNo);
        if (CarPersonnelChangeConstant.PersonnelChangeType.DIMISSION.equals(personnelChangeType)){
            List<String> roleCodeList = Arrays.asList(roleCodeConstant.getSafetyOfficer().split(","));

            List<YtsafetySysResponsibleVO> responsibleVOList = commonService.queryResponsibleByOrgCode(employee.getOrgCode(),roleCodeList);
            String userNames = responsibleVOList.stream()
                    .map(YtsafetySysResponsibleVO::getUserName)
                    .collect(Collectors.joining(","));
            String empNames = responsibleVOList.stream()
                    .map(YtsafetySysResponsibleVO::getEmpName)
                    .collect(Collectors.joining(","));
            carPersonnelChangeProcess.setSetupPersonEmpUser(userNames);
            carPersonnelChangeProcess.setSetupPersonEmpName(empNames);
        }else if (CarPersonnelChangeConstant.PersonnelChangeType.TRANSFER_POSITION.equals(personnelChangeType)){
            carPersonnelChangeProcess.setSetupPersonEmpUser(employee.getEmpLogin());
            carPersonnelChangeProcess.setSetupPersonEmpName(employee.getEmpName());
        }
//        carPersonnelChangeProcess.setReviewPersonEmpUser();
//        carPersonnelChangeProcess.setReviewPersonEmpName();
        carPersonnelChangeProcess.setIsReplacePersonnel(CarPersonnelChangeConstant.ReplacePersonnel.NO_CHANGE);
        carPersonnelChangeProcess.setStatus(1);
        carPersonnelChangeProcess.setHandlerType(CarPersonnelChangeConstant.HandlerType.UNCORRECTED);
        carPersonnelChangeProcess.setDelFlag(0);
        this.save(carPersonnelChangeProcess);

        for (SafetyEmployeePrivateCar car : carList) {
            YTSafetyCarPersonnelChangeInfo personnelChangeInfo = new YTSafetyCarPersonnelChangeInfo();
            personnelChangeInfo.setId(IdWorker.getId());
            personnelChangeInfo.setProcessId(carPersonnelChangeProcess.getId());
            personnelChangeInfo.setEmployeeCarId(car.getId());
            personnelChangeInfo.setCarNo(car.getCarNo());
            personnelChangeInfo.setCarType(car.getVehicleType());
            personnelChangeInfo.setCarColor(car.getCarColor());
            personnelChangeInfo.setCurrentAffiliatedPersonnel(car.getEmployeeNo());
            personnelChangeInfo.setIsReplacePersonnel(CarPersonnelChangeConstant.ReplacePersonnel.NO_CHANGE);
            personnelChangeInfo.setChangePersonnel("");
            safetyCarPersonnelChangeInfoService.add(personnelChangeInfo);
        }
        submitProcess(carPersonnelChangeProcess);
    }

    @Override
    public YTSafetyCarPersonnelChangeProcessVO queryById(Long id) {
        YTSafetyCarPersonnelChangeProcess carPersonnelChangeProcess = this.getById(id);
        YTSafetyCarPersonnelChangeProcessVO carPersonnelChangeProcessVO = new YTSafetyCarPersonnelChangeProcessVO();
        carPersonnelChangeProcessVO.setChangeProcessId(String.valueOf(carPersonnelChangeProcess.getId()));
        carPersonnelChangeProcessVO.setCarPersonnelChangeCode(carPersonnelChangeProcess.getCarPersonnelChangeCode());
        carPersonnelChangeProcessVO.setPersonnelChangeType(carPersonnelChangeProcess.getPersonnelChangeType());
        carPersonnelChangeProcessVO.setIsReplacePersonnel(carPersonnelChangeProcess.getIsReplacePersonnel());
        carPersonnelChangeProcessVO.setStatus(carPersonnelChangeProcess.getStatus());
        carPersonnelChangeProcessVO.setHandlerType(carPersonnelChangeProcess.getHandlerType());
        List<YTSafetyCarPersonnelChangeInfoVO> carPersonnelChangeInfoList =safetyCarPersonnelChangeInfoService.queryByProcessId(id);
        carPersonnelChangeProcessVO.setCarPersonnelChangeInfoList(carPersonnelChangeInfoList);
        return carPersonnelChangeProcessVO;
    }

    @Override
    public void turnover(YTSafetyCarPersonnelChangeProcessDTO changeProcessDTO) {
        List<YTSafetyCarPersonnelChangeInfoDTO> carPersonnelChangeInfoList = changeProcessDTO.getCarPersonnelChangeInfoList();
        if (carPersonnelChangeInfoList == null){
            return;
        }
        safetyCarPersonnelChangeInfoService.updateList(carPersonnelChangeInfoList);
        YTSafetyCarPersonnelChangeProcess changeProcess = this.getById(changeProcessDTO.getId());
        for (YTSafetyCarPersonnelChangeInfoDTO info : carPersonnelChangeInfoList){
            if (info.getIsReplacePersonnel().equals(CarPersonnelChangeConstant.ReplacePersonnel.CHANGE)){
                changeProcess.setIsReplacePersonnel(CarPersonnelChangeConstant.ReplacePersonnel.CHANGE);
                changeProcess.setHandlerType(CarPersonnelChangeConstant.HandlerType.ADJUSTMENT);
            }
        }
        this.updateById(changeProcess);
    }

    @Async
    private void submitProcess(YTSafetyCarPersonnelChangeProcess process){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LoginUser user = BusUtits.getLoginUser();
        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(process.getModelFlag());
        dto.setFormInstanceId(process.getId());
        dto.setFlowCode(process.getFlowCode());
        dto.setFlowSubject(process.getFlowSubject());
        dto.setCurNodeHandler(process.getSetupPersonEmpUser());
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
        dto.setApplyName(user.getRealname());
        dto.setApplyDepartCode(user.getOrgCode());
        dto.setApplyDepartName(user.getOrgName());
        dto.setPreNodeHandler(user.getEipName());
        dto.setLoginName(user.getEipName());
        dto.setPreNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        safetySummaryProcessService.createAndApproveProcess(dto);
    }

    @Override
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO) {
        List<FieldAuth> fieldAuths = safetySummaryProcessService.queryFieldAuth(processParamDTO.getFormInstanceId());
        LoginUser user = BusUtits.getLoginUser();
        processParamDTO.setLoginName(user.getEipName());
        processParamDTO.setNodeHandler(user.getEipName());
        processParamDTO.setNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        BpmReponseParamsVO bpmReponseParamsVO = safetySummaryProcessService.executeProcess(processParamDTO);
        nodeApprovalStatusManage(bpmReponseParamsVO,fieldAuths);
        return bpmReponseParamsVO;
    }

    private boolean isProcessEdit(List<FieldAuth>  fieldAuths,String fieldName ){
        if(fieldAuths==null|| fieldAuths.isEmpty()) {
            return false;
        }
        FieldAuth fieldAuth= fieldAuths.stream().filter(x-> fieldName.equalsIgnoreCase(x.getFieldCode()) ).findFirst().orElse(null);
        return fieldAuth != null;
    }

    private void nodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO,List<FieldAuth> fieldAuths) {
        if (isProcessEdit(fieldAuths,"setup_person_emp_user")){
            adjustNodeApprovalStatusManage(bpmReponseParamsVO);
        }else if (isProcessEdit(fieldAuths,"review_person_emp_user")){
            checkNodeApprovalStatusManage(bpmReponseParamsVO);
        }
    }

    private void adjustNodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO) {
        YTSafetyCarPersonnelChangeProcess changeProcess = this.getById(bpmReponseParamsVO.getFormInstanceId());
        if (changeProcess == null) {
            return;
        }
        changeProcess.setStatus(CarPersonnelChangeProcessStatus.CHECK.getCode());
        updateById(changeProcess);
    }

    private void checkNodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO) {
        YTSafetyCarPersonnelChangeProcess changeProcess = this.getById(bpmReponseParamsVO.getFormInstanceId());
        if (changeProcess == null) {
            return;
        }
        changeProcess.setStatus(CarPersonnelChangeProcessStatus.DONE.getCode());
        if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
            changeProcess.setStatus(CarPersonnelChangeProcessStatus.DONE.getCode());
            List<YTSafetyCarPersonnelChangeInfoVO> infoVOS = safetyCarPersonnelChangeInfoService.queryByProcessId(changeProcess.getId());
            //缺少出入口车辆注销新增逻辑
        }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
            changeProcess.setStatus(CarPersonnelChangeProcessStatus.ADJUST.getCode());
        }
        updateById(changeProcess);
    }
}
