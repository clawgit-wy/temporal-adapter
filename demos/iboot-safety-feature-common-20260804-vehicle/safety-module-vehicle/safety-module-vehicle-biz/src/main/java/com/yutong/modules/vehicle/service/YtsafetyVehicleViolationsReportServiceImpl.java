package com.yutong.modules.vehicle.service;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.FileService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.common.entity.bpm.FieldAuth;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.service.YTSafetyWaterFlowNoService;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.vo.YtsafetySysResponsibleVO;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleFreezeConfigService;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleViolationsReportService;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleViolationsReport;
import com.yutong.modules.vehicle.enums.VehicleViolationsStatus;
import com.yutong.modules.vehicle.mapper.YtsafetyVehicleViolationsReportMapper;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportAddParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportEditParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportQueryParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleViolationsReportVo;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 车辆违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public class YtsafetyVehicleViolationsReportServiceImpl extends ServiceImpl<YtsafetyVehicleViolationsReportMapper, YtsafetyVehicleViolationsReport> implements IYtsafetyVehicleViolationsReportService {

    @Value("${ytsafety.custom.constant.fireTeamLeader}")
    private String fireTeamLeader;


    @Value("${ytsafety.custom.constant.securityPatrolLeader}")
    private String securityPatrolLeader;


    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    private FileService fileService;

    @Autowired
    private YTSafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    private IYtsafetyVehicleFreezeConfigService ytsafetyVehicleFreezeConfigService;



    private static final String MODEL_ID = "vehicle_violations_report_process";
    /**
     * 违规车辆冻结时长配置列表
     * @param page
     * @param queryParam
     * @return
     */
    public IPage<YtsafetyVehicleViolationsReportVo> queryList(Page<YtsafetyVehicleViolationsReport> page, YtsafetyVehicleViolationsReportQueryParam queryParam){
        LambdaQueryWrapper<YtsafetyVehicleViolationsReport> queryWrapper = new LambdaQueryWrapper<YtsafetyVehicleViolationsReport>();
        queryWrapper.like(StrUtil.isNotBlank(queryParam.getVehicleNo()),YtsafetyVehicleViolationsReport::getVehicleNo,queryParam.getVehicleNo());
        queryWrapper.eq(StrUtil.isNotBlank(queryParam.getVehicleType()),YtsafetyVehicleViolationsReport::getVehicleType,queryParam.getVehicleType());
        queryWrapper.eq(StrUtil.isNotBlank(queryParam.getVehicleOwner()),YtsafetyVehicleViolationsReport::getVehicleOwner,queryParam.getVehicleOwner());
        queryWrapper.eq(StrUtil.isNotBlank(queryParam.getVehicleType()),YtsafetyVehicleViolationsReport::getVehicleType,queryParam.getVehicleType());
        queryWrapper.eq(StrUtil.isNotBlank(queryParam.getPlantId()),YtsafetyVehicleViolationsReport::getVehicleType,queryParam.getVehicleType());
        queryWrapper.eq(YtsafetyVehicleViolationsReport::getDelFlag,0);
        IPage<YtsafetyVehicleViolationsReport> pageList = this.page(page, queryWrapper);
        // 新建VO分页对象
        Page<YtsafetyVehicleViolationsReportVo> voPage = new Page<>();
        BeanUtils.copyProperties(pageList, voPage, "records");

        List<YtsafetyVehicleViolationsReportVo> voList = pageList.getRecords().stream()
                .map(entity -> {
                    YtsafetyVehicleViolationsReportVo vo = new YtsafetyVehicleViolationsReportVo();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    public YtsafetyVehicleViolationsReportVo saveVehicleViolations(YtsafetyVehicleViolationsReportAddParam addParam){
        YtsafetyVehicleViolationsReportVo ytsafetyVehicleViolationsReportVo=new YtsafetyVehicleViolationsReportVo();
        LoginUser user = BusUtits.getLoginUser();
        YtsafetyVehicleViolationsReport  entity=new YtsafetyVehicleViolationsReport();
        entity.setModelFlag(MODEL_ID);
        entity.setId(IdWorker.getId());
        BeanUtils.copyProperties(addParam, entity);
        entity.setCreateUser(user.getId());
        entity.setCreateName(user.getRealname());
        entity.setCreateTime(new DateTime());
        entity.setApplyNo(user.getEmpNo());
        entity.setApplyName(user.getRealname());
        entity.setFlowSubject(buildFlowSubject(entity, user));
        this.save(entity);
        BeanUtils.copyProperties(entity,ytsafetyVehicleViolationsReportVo);
        return ytsafetyVehicleViolationsReportVo;
    }

    public YtsafetyVehicleViolationsReportVo updateVehicleViolations(YtsafetyVehicleViolationsReportAddParam addParam){
        YtsafetyVehicleViolationsReportVo ytsafetyVehicleViolationsReportVo=new YtsafetyVehicleViolationsReportVo();
        LoginUser user = BusUtits.getLoginUser();
        YtsafetyVehicleViolationsReport  entity=getById(addParam.getId());
        BeanUtils.copyProperties(addParam, entity);
        entity.setCreateUser(user.getId());
        entity.setCreateName(user.getRealname());
        entity.setCreateTime(new DateTime());
        entity.setApplyNo(user.getEmpNo());
        entity.setApplyName(user.getRealname());
        entity.setFlowSubject(buildFlowSubject(entity, user));
        this.updateById(entity);
        BeanUtils.copyProperties(entity,ytsafetyVehicleViolationsReportVo);
        return ytsafetyVehicleViolationsReportVo;
    }


    /** 提交上报 */
    public YtsafetyVehicleViolationsReportVo submitReport(YtsafetyVehicleViolationsReportAddParam addParam){
        LoginUser user = BusUtits.getLoginUser();
        YtsafetyVehicleViolationsReportVo ytsafetyVehicleViolationsReportVo=new YtsafetyVehicleViolationsReportVo();
        YtsafetyVehicleViolationsReport report=new YtsafetyVehicleViolationsReport();
        BeanUtils.copyProperties(addParam,report);
        if(addParam.getId()==null){
            report.setId(IdWorker.getId());
        }
        if(!StringUtils.isNotEmpty(report.getFlowCode())){
            report.setFlowCode(safetyWaterFlowNoService.getNowByTypeDay("CLWGDJ",null));
        }
        getViolationsVehicleNo(report);
        if(report.getVehicleFreezeNumber().equals(0)){
            throw new RuntimeException("该类型没有配置违规信息");
        }
        report.setModelFlag(MODEL_ID);
        report.setDraftStatus(1);
        fillCommonFields(report,user, VehicleViolationsStatus.APPROVING.getCode());
        setReviewPerson(report,user);
        saveOrUpdate(report);
        submitProcess(report);
        BeanUtils.copyProperties(report,ytsafetyVehicleViolationsReportVo);
        return ytsafetyVehicleViolationsReportVo;
    }

    private void fillCommonFields(YtsafetyVehicleViolationsReport report, LoginUser user,
                                  int status) {
        report.setStatus(status);
        report.setApplyDepartCode(user.getOrgCode());
        report.setApplyDepartName(user.getOrgName());
        report.setApplyName(user.getRealname());
        report.setApplyNo(user.getEmpNo());
        report.setFlowSubject(buildFlowSubject(report, user));
    }

    private void setReviewPerson(YtsafetyVehicleViolationsReport report, LoginUser user) {
        // 审核节点审核人员
        List<String> roleCodeList=new ArrayList<>();
        roleCodeList.add(fireTeamLeader);
        roleCodeList.add(securityPatrolLeader);
        List<YtsafetySysResponsibleVO> list=safetyCommonService.queryResponsibleByFactory(Long.valueOf(report.getPlantId()),roleCodeList);
        String userNames = list.stream()
                .map(YtsafetySysResponsibleVO::getUserName)
                .collect(Collectors.joining(","));
        String empNames = list.stream()
                .map(YtsafetySysResponsibleVO::getEmpName)
                .collect(Collectors.joining(","));
        report.setViolationTeamLeader(userNames);
        report.setViolationTeamLeaderName(empNames);
        report.setReconsider(report.getVehicleOwnerNo());
        report.setReconsiderName(report.getReconsiderName());
    }

    @Async
    private void submitProcess(YtsafetyVehicleViolationsReport report){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LoginUser user = BusUtits.getLoginUser();
        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(report.getModelFlag());
        dto.setFormInstanceId(report.getId());
        dto.setFlowCode(report.getFlowCode());
        dto.setFlowSubject(report.getFlowSubject());
        dto.setCurNodeHandler(report.getViolationTeamLeader());
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
        dto.setApplyName(user.getRealname());
        dto.setApplyDepartCode(user.getOrgCode());
        dto.setApplyDepartName(user.getOrgName());
        dto.setPreNodeHandler(user.getEipName());
        dto.setLoginName(user.getEipName());
        dto.setApplyNo(user.getEmpNo());
        dto.setPreNodeHandlerName(user.getRealname()+"("+user.getEmpNo()+")");
        safetySummaryProcessService.createAndApproveProcess(dto);
    }

    /**
     * 构建流程标题：年月日+厂区+姓名/匿名+发起的+类型+问题来源+隐患上报
     */
    private String buildFlowSubject(YtsafetyVehicleViolationsReport report, LoginUser user) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String plantName =report.getPlantName();
        String nameStr = user.getRealname();
        return dateStr + plantName + nameStr + "发起的车辆违规";
    }

    /** 车辆违规上报-审核 */
    public void reviewReport(YtsafetyVehicleViolationsReportEditParam editParam){




    }

    /** 获取完整详情（主表+描述+结果+延期+历史） */
    public YtsafetyVehicleViolationsReportVo getReportDetail(Long id){
        YtsafetyVehicleViolationsReportVo ytsafetyVehicleViolationsReportVo=new YtsafetyVehicleViolationsReportVo();
        LoginUser user = BusUtits.getLoginUser();
        YtsafetyVehicleViolationsReport yVehicleViolationsReport =this.getById(id);
        if (yVehicleViolationsReport == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        BeanUtils.copyProperties(yVehicleViolationsReport,ytsafetyVehicleViolationsReportVo);
        List<String> fileIdList = Arrays.stream(yVehicleViolationsReport.getAttachmentIds().split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        List<YTSafetyFileInfo> list = fileService.listByIds(fileIdList);
        ytsafetyVehicleViolationsReportVo.setAttachmentList(list);
        if (ytsafetyVehicleViolationsReportVo.getDraftStatus().equals(1)){
            BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(Long.valueOf(ytsafetyVehicleViolationsReportVo.getId()),user.getEipName());
            if (detailsVO != null){
                ytsafetyVehicleViolationsReportVo.setIsCanApprove(detailsVO.getIsCanApprove());
                ytsafetyVehicleViolationsReportVo.setIsCanEdit(detailsVO.getIsCanEdit());
                ytsafetyVehicleViolationsReportVo.setFlowId(detailsVO.getFlowId());
            }
        }
        return ytsafetyVehicleViolationsReportVo;
    }

    /** 根据车牌号获取车辆违规记录 */
    public  List<YtsafetyVehicleViolationsReportVo> getVehicleViolationsByVehicleNo(String  vehicleNo){
        LambdaQueryWrapper<YtsafetyVehicleViolationsReport> queryWrapper = new LambdaQueryWrapper<YtsafetyVehicleViolationsReport>();
        queryWrapper.eq(YtsafetyVehicleViolationsReport::getVehicleNo, vehicleNo);
        // 查询全部匹配数据
        List<YtsafetyVehicleViolationsReport> list = this.baseMapper.selectList(queryWrapper);
        List<YtsafetyVehicleViolationsReportVo> voList = list.stream()
                .map(entity -> {
                    YtsafetyVehicleViolationsReportVo vo = new YtsafetyVehicleViolationsReportVo();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        return voList;
    }

    /**
     * 车辆违规上报批量删除
     * @param ids
     * @return
     */
    public void removeVehicleViolationsByIds(List<String> ids){
        List<YtsafetyVehicleViolationsReport> list=new ArrayList<>();
        for(String id:ids) {
            YtsafetyVehicleViolationsReport  entity=getById(id);
            if (entity != null) {
                entity.setDelFlag(1);
                list.add(entity);
            }
        }
        if(!list.isEmpty()){
            this.updateBatchById(list);
        }
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

    private void nodeApprovalStatusManage(BpmReponseParamsVO bpmReponseParamsVO,List<FieldAuth> fieldAuths) {
        YtsafetyVehicleViolationsReport report = this.getById(bpmReponseParamsVO.getFormInstanceId());
        if (isProcessEdit(fieldAuths,"vehicle_violations_draft")){
            if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
                report.setStatus(VehicleViolationsStatus.APPROVING.getCode());
            }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
                report.setStatus(VehicleViolationsStatus.DRAFT.getCode());
            }
        }else if (isProcessEdit(fieldAuths,"vehicle_violations_approval")){
            if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
                report.setStatus(VehicleViolationsStatus.RECONSIDER.getCode());
            }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
                report.setStatus(VehicleViolationsStatus.DRAFT.getCode());
            }
        }else if (isProcessEdit(fieldAuths,"vehicle_violations_reconsider")){
            if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
                report.setStatus(VehicleViolationsStatus.RECONSIDER_APPROVING.getCode());
            }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
                report.setStatus(VehicleViolationsStatus.APPROVING.getCode());
            }
        }else if (isProcessEdit(fieldAuths,"vehicle_violations_reconsider_approval")){
            if (ProcessStatusEnums.approve.getCode() == bpmReponseParamsVO.getFlowStatus()){
                if(report.getIsAccept().equals(1)) {
                    report.setStatus(VehicleViolationsStatus.PUNISHMENT_ENDS.getCode());
                }else{
                    report.setStatus(VehicleViolationsStatus.PUNISHMENT.getCode());
                }
            }else if (ProcessStatusEnums.deny.getCode() == bpmReponseParamsVO.getFlowStatus()){
                report.setStatus(VehicleViolationsStatus.RECONSIDER.getCode());
            }
        }
        this.updateById(report);
    }

    private boolean isProcessEdit(List<FieldAuth>  fieldAuths,String fieldName ){
        if(fieldAuths==null|| fieldAuths.isEmpty()) {
            return false;
        }
        FieldAuth fieldAuth= fieldAuths.stream().filter(x-> fieldName.equalsIgnoreCase(x.getFieldCode()) ).findFirst().orElse(null);
        return fieldAuth != null;
    }


    public void getViolationsVehicleNo( YtsafetyVehicleViolationsReport report) {
        LambdaQueryWrapper<YtsafetyVehicleViolationsReport> queryWrapper = new LambdaQueryWrapper<YtsafetyVehicleViolationsReport>();
        queryWrapper.eq( YtsafetyVehicleViolationsReport::getVehicleNo, report.getVehicleNo());
        queryWrapper.eq( YtsafetyVehicleViolationsReport::getViolationsTypeCode, report.getViolationsTypeCode());
        queryWrapper.eq(YtsafetyVehicleViolationsReport::getDraftStatus, 1);
        queryWrapper.ne(YtsafetyVehicleViolationsReport::getStatus, 30);
        queryWrapper.ne(YtsafetyVehicleViolationsReport::getStatus, 5);
        int i =this.baseMapper.selectList(queryWrapper).size();
        int vehicleFreezeNumber= ytsafetyVehicleFreezeConfigService.getVehicleFreeze((i+1),report.getViolationsTypeCode());
        report.setVehicleFreezeNumber(vehicleFreezeNumber);
        report.setVehicleFreezeConfig("第"+(i+1)+"次，封禁"+vehicleFreezeNumber+"天");
    }
}
