package com.yutong.safety.core.handler.visitor;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.SafetyHikThresholdEnum;
import com.yutong.safety.common.constants.UserTypeEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.*;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDetailDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationDO;
import com.yutong.safety.core.db.entity.visitor.common.SafetyVisitorConstants;
import com.yutong.safety.core.db.entity.visitor.param.*;
import com.yutong.safety.core.db.entity.visitor.vo.SafetyVisitorAuthorizationVO;
import com.yutong.safety.core.db.entity.visitor.vo.SafetyVisitorIdCardVO;
import com.yutong.safety.core.dto.visitor.SafetyVisitorAuthorizationDto;
import com.yutong.safety.core.dto.visitor.SafetyVisitorAuthorizationEipDto;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.common.SafetySendYxtHandler;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import com.yutong.safety.core.service.SafetyBpmProcessService;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.eip.EipCommonService;
import com.yutong.safety.core.service.visitor.SafetyVisitorAuthorizationApprovalDetailService;
import com.yutong.safety.core.service.visitor.SafetyVisitorAuthorizationApprovalService;
import com.yutong.safety.core.service.visitor.SafetyVisitorAuthorizationService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yutong.safety.common.constants.Constants.*;

@Component
public class SafetyVisitorAuthorizationApprovalHandler extends VoUtil implements SafetyVisitorConstants {

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private EipCommonService eipCommonService;

    @Resource
    private SafetySendYxtHandler safetySendYxtHandler;

    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyBpmProcessService safetyBpmProcessService;

    @Resource
    private SafetyVisitorAuthorizationApprovalService safetyVisitorAuthorizationApprovalService;

    @Resource
    private SafetyVisitorAuthorizationService safetyVisitorAuthorizationService;

    @Resource
    private SafetyVisitorAuthorizationApprovalDetailService safetyVisitorAuthorizationApprovalDetailService;

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    //代办的发起人待定
    @Value("${an.eip.todo.from}")
    private String formEmpNo;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    private static String coresFormId = "visitorAuthorizationApproval";
    /**
     * 数据分页获取
     *
     * @param param 入参
     * @param token Token
     * @return 返回分页数据集
     */
    public VO pageTokenList(SafetyVisitorAuthorizationQueryParam param, String token) {
        // 数据权限
        if (StringUtils.isNotBlank(token)) {
            param.setRoleWorkNo(safetyBaseHandler.getListPermission(token, "超级管理员"));
        }
        // 默认的状态-正常
        if (param.getStatus() == null) {
            param.setStatus(1);
        }
        // 数据获取
        long total = safetyVisitorAuthorizationApprovalService.selectCount(param);
        List<SafetyVisitorAuthorizationDto> list = safetyVisitorAuthorizationApprovalService.selectList(param, true);
        PageVO<SafetyVisitorAuthorizationDto> pageInfo = new PageVO<>(total, list);
        return VoUtil.successData(pageInfo);
    }


    public void export(HttpServletResponse response, SafetyVisitorAuthorizationQueryParam param, String token) {
        try {
            String fileName = new String(("访客审批授权" + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");
            // 数据查询的权限条件，跟据角色判断，如果有要求角色则为空，没有则返回登录人信息
            if (StringUtils.isNotBlank(token)) {
                param.setRoleWorkNo(safetyBaseHandler.getListPermission(token, "超级管理员"));
            }
            // 数据查询
            long total = safetyVisitorAuthorizationApprovalService.selectCount(param);
            if (total > Constants.FILE_SIZE) {
                throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
            }

            List<SafetyVisitorAuthorizationDto> list = safetyVisitorAuthorizationApprovalService.selectList(param, false);

            // 数据整合
            Pair<String[], String[][]> pair = processExportData(list);
            String[] left = pair.getLeft();
            String[][] right = pair.getRight();
            if (left == null || right == null) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据导出异常，请检查重写的processExportData方法！");
            }

            HSSFWorkbook hssfWorkbook = ExcelUtils.getHSSFWorkbook("访客审批授权", left, right);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            hssfWorkbook.write(os);
            os.flush();
            os.close();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出列表异常");
        }
    }

    public Pair<String[], String[][]> processExportData(List<SafetyVisitorAuthorizationDto> dataList) {
        String[] title = {"员工号", "被授权人姓名", "组织架构", "岗位", "职种", "授权原因", "授权时间-始", "授权时间-终", "授权状态", "注销原因",
                "授权员人员工号", "授权人姓名", "授权人组织架构", "授权人岗位", "授权人职种", "授权流程"};

        // 使用流操作简化填充数据
        String[][] values = dataList.stream()
                .map(info -> new String[]{
                        info.getGranteeNo(),
                        info.getGranteeName(),
                        info.getGranteeOrgName(),
                        info.getGranteePosition(),
                        info.getGranteeJobName(),
                        info.getAuthorizationReason(),
                        info.getAuthorizationBeginTime(),
                        info.getAuthorizationEndTime(),
                        info.getStatus() != null ? SafetyVisitorConstants.STATUS_INFO.get(info.getStatus()) : "未知状态",
                        info.getCancelReason(),
                        info.getGrantorNo(),
                        info.getGrantorName(),
                        info.getGrantorOrgName(),
                        info.getGrantorPosition(),
                        info.getGrantorJobName(),
                        StringUtils.isBlank(info.getBpmFlowId()) ? "" : anEipTodoUrl + "registration/visitorApprovalApply?formInstanceId=" + info.getAuthorizationApprovalId() + "&processId=" + info.getBpmFlowId()
                })
                .toArray(String[][]::new);
        return Pair.of(title, values);
    }


    public VO saveAndUpdate(SafetyVisitorAuthorizationAddParam info, String token) {
        SafetyVisitorAuthorizationApprovalDetailDO detailInfo = new SafetyVisitorAuthorizationApprovalDetailDO();
        if (info.getId() != null) {
            detailInfo = safetyVisitorAuthorizationApprovalDetailService.selectById(info.getId());
        }

        String granteeNo = info.getGranteeNo();
        SafetyEmployeeMessageDO granteeInfo = safetyEmployeeMessageService.getByNo(granteeNo);
        if (granteeInfo == null) {
            return VoUtil.fail("被授权人不存在");
        }
        String grantorNo = info.getGrantorNo();
        SafetyEmployeeMessageDO grantorInfo = safetyEmployeeMessageService.getByNo(grantorNo);
        if (grantorInfo == null) {
            return VoUtil.fail("授权人不存在");
        }

        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        String workNo = tokenUser.get("workNo");
        if (StringUtils.isBlank(workNo)) {
            return fail("Token失效");
        }
        SafetyEmployeeMessageDO submitInfo = safetyEmployeeMessageService.getByNo(workNo);
        if (submitInfo == null) {
            return VoUtil.fail("创建人不存在");
        }

        SafetyVisitorAuthorizationApprovalDO mainInfo = disDataInfo(submitInfo, grantorInfo);
        mainInfo.setAuthorizationBeginTime(info.getAuthorizationBeginTime());
        mainInfo.setAuthorizationEndTime(info.getAuthorizationEndTime());
        mainInfo.setAuthorizationReason(info.getAuthorizationReason());
        mainInfo.setId(detailInfo.getAuthorizationApprovalId());
        safetyVisitorAuthorizationApprovalService.saveAndUpdate(mainInfo, token);

        detailInfo.setAuthorizationApprovalId(mainInfo.getId());
        detailInfo.setGranteeName(granteeInfo.getEmpName());
        detailInfo.setGranteeNo(granteeInfo.getEmpNo());
        detailInfo.setGranteeOrgCode(granteeInfo.getOrgCode());
        detailInfo.setGranteeOrgName(granteeInfo.getOrgName());
        detailInfo.setGranteePosition(granteeInfo.getPositionName());
        detailInfo.setGranteeJobName(granteeInfo.getJobTypeName());
        detailInfo.setGranteeEipLogin(granteeInfo.getEmpLogin());
        String format = DateUtils.format(new Date(), 1);
        if (StringUtils.equals(format, info.getAuthorizationEndTime()) || DateUtils.isBeforeStr(format, info.getAuthorizationEndTime())) {
            detailInfo.setStatus(1);
        } else {
            detailInfo.setStatus(2);
        }
        safetyVisitorAuthorizationApprovalDetailService.saveAndUpdate(detailInfo, token);

        return VoUtil.success();
    }

    private SafetyVisitorAuthorizationApprovalDO disDataInfo(SafetyEmployeeMessageDO submitInfo, SafetyEmployeeMessageDO grantorInfo) {
        SafetyVisitorAuthorizationApprovalDO mainInfo = new SafetyVisitorAuthorizationApprovalDO();
        mainInfo.setSubmitName(submitInfo.getEmpName());
        mainInfo.setSubmitNo(submitInfo.getEmpNo());
        mainInfo.setSubmitOrgCode(submitInfo.getOrgCode());
        mainInfo.setSubmitOrgName(submitInfo.getOrgName());
        mainInfo.setGrantorName(grantorInfo.getEmpName());
        mainInfo.setGrantorNo(grantorInfo.getEmpNo());
        mainInfo.setGrantorOrgCode(grantorInfo.getOrgCode());
        mainInfo.setGrantorOrgName(grantorInfo.getOrgName());
        mainInfo.setGrantorPosition(grantorInfo.getPositionName());
        mainInfo.setGrantorJobName(grantorInfo.getJobTypeName());
        mainInfo.setGrantorEipLogin(grantorInfo.getEmpLogin());
        return mainInfo;
    }



    /**
     * 根据授权人查询被授权人
     *
     * @param empNo 授权人员工号或者姓名
     * @return 返回被授权人信息
     */
    public VO getVisitorAuthorizationForEip(String empNo) {
        if (StringUtils.isBlank(empNo)) {
            return fail("员工号为空");
        }
        SafetyVisitorAuthorizationQueryParam param = new SafetyVisitorAuthorizationQueryParam();
        param.setGrantorInfo(empNo);
        param.setStatus(1);
        param.setAuthorizationEndTime(DateUtils.format(new Date(), 1));
        List<SafetyVisitorAuthorizationDto> list = safetyVisitorAuthorizationApprovalService.selectList(param, false);
        if (!CollectionUtils.isEmpty(list)) {
            List<SafetyVisitorAuthorizationEipDto> collect = list.stream().map(li -> {
                SafetyVisitorAuthorizationEipDto info = new SafetyVisitorAuthorizationEipDto();
                BeanUtils.copyProperties(li, info);
                return info;
            }).collect(Collectors.toList());
            return VoUtil.successData(collect);
        }
        return VoUtil.successData(list);
    }

    /**
     * 定时任务：访客授权审批
     */
    public VO taskVisitorApproval(String type) {
        // 1.授权到期自动注销
        // 状态为正常并且授权日期终小于当前日期的数据
        SafetyVisitorAuthorizationQueryParam param = new SafetyVisitorAuthorizationQueryParam();
        param.setLtAuthorizationEndTime(DateUtils.format(new Date(), 1));
        param.setStatus(1);
        List<SafetyVisitorAuthorizationDto> list = safetyVisitorAuthorizationApprovalService.selectList(param, false);
        if (!CollectionUtils.isEmpty(list)) {
            List<Integer> ids = list.stream().map(SafetyVisitorAuthorizationApprovalDO::getId).collect(Collectors.toList());
            safetyVisitorAuthorizationApprovalDetailService.cancelAuthorizationInfo(ids, "超出有效期");
            // 消代办-审批的日期大于7 天的才会消除代办
            List<SafetyVisitorAuthorizationDto> dtoList = list.stream().filter(li -> DateUtils.daysBetween(li.getAuthorizationBeginTime(), li.getAuthorizationEndTime()) + 1 > 7).collect(Collectors.toList());

            // 消除代办
            sendEipToDo(dtoList, false);
        }

        // 2.EHR数据同步
        List<SafetyEmployeeMessageDO> messageDOList = safetyEmployeeMessageService.selectListByJobName(EHR_INTERVIEWEE_NAME, type);
        SafetyVisitorAuthorizationQueryParam param3 = new SafetyVisitorAuthorizationQueryParam();
        param3.setStatus(STATUS_NORMAL);
        param3.setBpmFlowIdNull("T");
        List<SafetyVisitorAuthorizationDto> listByBpmFlowId = safetyVisitorAuthorizationApprovalService.selectList(param3, false);
        if (!CollectionUtils.isEmpty(listByBpmFlowId)) {
            Set<String> grantorList = listByBpmFlowId.stream().map(SafetyVisitorAuthorizationDto::getGrantorNo).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            List<SafetyVisitorAuthorizationApprovalDO> saveList = messageDOList.stream().filter(li -> !grantorList.contains(li.getEmpNo())).map(li -> {
                SafetyVisitorAuthorizationApprovalDO mainInfo = new SafetyVisitorAuthorizationApprovalDO();
                mainInfo.setGrantorName(li.getEmpName());
                mainInfo.setGrantorNo(li.getEmpNo());
                mainInfo.setGrantorOrgCode(li.getOrgCode());
                mainInfo.setGrantorOrgName(li.getOrgName());
                mainInfo.setGrantorPosition(li.getPositionName());
                mainInfo.setGrantorJobName(li.getJobTypeName());
                mainInfo.setGrantorEipLogin(li.getEmpLogin());
                mainInfo.setCreates("", "EHR同步");
                return mainInfo;
            }).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(saveList)) {
                safetyVisitorAuthorizationApprovalService.batchSave(saveList);
                List<SafetyVisitorAuthorizationApprovalDetailDO> detailList = saveList.stream().map(li -> {
                    SafetyVisitorAuthorizationApprovalDetailDO info = new SafetyVisitorAuthorizationApprovalDetailDO();
                    info.setAuthorizationApprovalId(li.getId());
                    info.setStatus(1);
                    info.setCreates("", "EHR同步");
                    return info;
                }).collect(Collectors.toList());
                safetyVisitorAuthorizationApprovalDetailService.batchSave(detailList);
            }
        }


        // 3.临近七天发送代办和消息
        SafetyVisitorAuthorizationQueryParam param2 = new SafetyVisitorAuthorizationQueryParam();
        param2.setEqAuthorizationEndTime(DateUtils.format(DateUtils.addDays(new Date(), 6), 1));
        param2.setStatus(1);
        list = safetyVisitorAuthorizationApprovalService.selectList(param2, false);
        list = list.stream().filter(li -> StringUtils.isNotBlank(li.getGranteeNo())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            // 发送消息
            safetySendYxtHandler.sendYxtMsgByVisitor(list);

            // 发送代办
            sendEipToDo(list, true);
        }
        return VoUtil.success();
    }

    /**
     * 发送和消除代办
     * @param list 发送代办的数据
     * @param type true 发送和消除， false：消除
     */
    public void sendEipToDo(List<SafetyVisitorAuthorizationDto> list, boolean type){
        //发送代办 - 被授权人去重后的数据
        List<SafetyVisitorAuthorizationDto> dtoList = list.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(SafetyVisitorAuthorizationDto::getGranteeNo, Function.identity(), (existing, replacement) -> existing),
                        map -> new ArrayList<>(map.values())));

        for (SafetyVisitorAuthorizationDto info : dtoList) {
            String granteeNo = info.getGranteeNo();
            if (StringUtils.isBlank(granteeNo)) {
                continue;
            }
            String modelId = "SAFETY-" + coresFormId + "-" + granteeNo;
            // 消除代办
            eipCommonService.eipToDoDone(modelId, granteeNo);
            // 发送代办
            if (type) {
                String targets = "[{\"PersonNo\":\"" + granteeNo + "\"}]";
                String docCreator = "{\"PersonNo\":\"" + formEmpNo + "\"}";
                String subject = "[安保管理平台]" + info.getGrantorOrgName() + "-" + info.getGranteeName() + "-访客审批权限续期流程";
                String modelName = "访客授权审批";
                String link = anEipTodoUrl + "registration/visitorApprovalApply?historyId=" + info.getAuthorizationApprovalId();
                //others = isOpenInMobile 标识禁止移动端预览
                try {
                    EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, null, targets, null, docCreator, null);
                    eipCommonService.createEipToDo(eipToDoVO);
                } catch (Exception e) {
                    LogUtils.error("【发送代办失败】访客授权审批流程  to " + granteeNo + ":", e);
                }
            }
        }
    }

    /**
     * 流程的创建
     *
     * @param info  新增的数据实体类
     * @param token 员工信息
     */
    public VO saveAndUpdateProcess(SafetyVisitorAuthorizationCreateParam info, String token) {
        try {
            // 数据校验
            List<String> detailList = info.getDetailList();
            if (CollectionUtils.isEmpty(detailList)) {
                return VoUtil.fail("请添加被授权人信息");
            }
            Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
            String workNo = tokenUser.get("workNo");
            SafetyEmployeeMessageDO submitInfo = safetyEmployeeMessageService.getByNo(workNo);
            if (submitInfo == null) {
                return VoUtil.fail("创建人不存在");
            }
            SafetyEmployeeMessageDO grantorInfo = safetyEmployeeMessageService.getByNo(info.getGrantorNo());
            if (grantorInfo == null) {
                return fail("授权人不存在");
            }
            List<SafetyVisitorAuthorizationDto> dtoList = safetyVisitorAuthorizationApprovalService.selectBpmFlowList(info.getGrantorNo());
            Integer configNum = getConfigNum(grantorInfo);
            if (dtoList.size() >= configNum || (configNum-dtoList.size()) < detailList.size()) {
                return fail("被授权人数超限");
            }
            if (info.getSectionChiefType() == 0) {
                SafetyEmployeeMessageDO applyEmpNo = safetyEmployeeMessageService.getByNo(info.getApplyEmpNo());
                if (applyEmpNo == null) {
                    return fail("审批人不存在");
                }
                if (!EHR_APPLY_EMP_JOB_NAME.contains(applyEmpNo.getJobTypeName())) {
                    return fail("请选择科长级以上人员");
                }
            }

            // 创建流程
            if (info.getId() == null) {
                LogUtils.info("getBpmFlowId={}", info.getBpmFlowId());
                // 数据持久化
                Integer save = save(info, submitInfo, grantorInfo, token);
                // 创建流程
                createBpmFlow(info, grantorInfo, save, token);
                return successData(save);
            }

            // 审核人提交节点
            return reviewBpmFlow(info.getFdParameterJson(),info.getId(), info.getBpmFlowId(), submitInfo);
        } catch (BizException e) {
            return fail(e.getMsg());
        }
    }


    /**
     * 保存流程数据
     * @param info 前端返回的数据
     * @param submitInfo 提交人信息
     * @param grantorInfo 授权人信息
     * @param token Token信息
     * @return 返回主表数据ID
     */
    public Integer save(SafetyVisitorAuthorizationCreateParam info, SafetyEmployeeMessageDO submitInfo, SafetyEmployeeMessageDO grantorInfo, String token) {
        // 主表数据
        SafetyVisitorAuthorizationApprovalDO mainInfo = disDataInfo(submitInfo, grantorInfo);
        mainInfo.setAuthorizationReason(info.getAuthorizationReason());
        mainInfo.setAuthorizationBeginTime(info.getAuthorizationBeginTime());
        mainInfo.setAuthorizationEndTime(info.getAuthorizationEndTime());
        mainInfo.setApplyEmpLogin(info.getApplyEmpLogin());
        mainInfo.setApplyEmpName(info.getApplyEmpName());
        mainInfo.setApplyEmpNo(info.getApplyEmpNo());
        mainInfo.setSectionChiefType(info.getSectionChiefType());
        safetyVisitorAuthorizationApprovalService.saveAndUpdate(mainInfo, token);
        Integer id = mainInfo.getId();

        // 明细表数据
        List<String> detailList = info.getDetailList();
        List<SafetyEmployeeMessageDO> listByStaffNo = safetyEmployeeMessageService.getListByStaffNo(detailList);
        if (CollectionUtils.isEmpty(listByStaffNo) || listByStaffNo.size() != detailList.size()) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "被授权人信息不存在");
        }
        List<SafetyVisitorAuthorizationApprovalDetailDO> saveDetailList = listByStaffNo.stream().map(li -> {
            SafetyVisitorAuthorizationApprovalDetailDO detailInfo = new SafetyVisitorAuthorizationApprovalDetailDO();
            detailInfo.setAuthorizationApprovalId(id);
            detailInfo.setGranteeNo(li.getEmpNo());
            detailInfo.setGranteeName(li.getEmpName());
            detailInfo.setGranteeOrgCode(li.getOrgCode());
            detailInfo.setGranteeOrgName(li.getOrgName());
            detailInfo.setGranteePosition(li.getPositionName());
            detailInfo.setGranteeJobName(li.getJobTypeName());
            detailInfo.setGranteeEipLogin(li.getEmpLogin());
            detailInfo.setStatus(0);
            detailInfo.setCreates(token);
            return detailInfo;
        }).collect(Collectors.toList());

        safetyVisitorAuthorizationApprovalDetailService.batchSave(saveDetailList);
        return id;
    }


    /**
     * 创建流程
     * @param info 前端返回数据信息
     * @param grantorInfo 授权人信息
     * @param id 数据ID
     * @param token Token信息
     */
    private void createBpmFlow(SafetyVisitorAuthorizationCreateParam info, SafetyEmployeeMessageDO grantorInfo, Integer id, String token) {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        String processSubject = year + "年" + month + "月" + day + "日-" + grantorInfo.getOrgName() + "-" + grantorInfo.getEmpNo() + "-" + grantorInfo.getEmpName() + "的访客审批授权流程";

        //手动提交事务
        String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(processSubject,
                VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, info.getSubmitEmpLogin(), id.toString());
        if (org.apache.commons.lang3.StringUtils.isBlank(returnFlowId)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "创建流程异常返回值" + returnFlowId);
        }
        String bpmFlowId = returnFlowId.split(",")[1];

        // 保存流程ID;
        SafetyVisitorAuthorizationApprovalDO dto = new SafetyVisitorAuthorizationApprovalDO();
        dto.setId(id);
        dto.setBpmFlowId(bpmFlowId);
        safetyVisitorAuthorizationApprovalService.saveAndUpdate(dto, token);
    }

    /**
     * 审核流程
     */
    private VO reviewBpmFlow(String fdParameterJson, Integer id, String bpmFlowId, SafetyEmployeeMessageDO empInfo) {
        LogUtils.info("getFdParameterJson={}", fdParameterJson);
        BpmOperationVO bpmOperationVO = JSON.parseObject(fdParameterJson, BpmOperationVO.class);
        String operationType = bpmOperationVO.getOperationType();
        if (StringUtils.isBlank(operationType)) {
            return fail("流程审批异常");
        }
        // 获取审核意见
        String auditNote = bpmOperationVO.getParam().getAuditNote();

        String userName = empInfo.getEmpLogin();
        if (StringUtils.isBlank(userName)) {
            return fail("用户名不存在");
        }

        // 初始化流程参数
        BpmProcessParam processParam;
        LogUtils.info("审核流程={}", operationType);
        int dataStatus = 0;
        switch (operationType) {
            case BPM_HANDLER_SUBMIT:
                // 起草人提交
            case BPM_HANDLER_PASS:
                // 审批通过
                processParam = safetyBpmProcessService.initProcessParam(id.toString(), bpmFlowId,
                        VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, userName, false, auditNote, "");
                break;

            case BPM_HANDLER_REFUSE:
                // 审批驳回
                processParam = safetyBpmProcessService.initProcessRejectParam(id.toString(), bpmFlowId,
                        VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, userName, false, auditNote,
                        bpmOperationVO.getParam().getJumpToNodeId());
                break;

            case BPM_HANDLER_ABANDON:
                // 流程废弃
                processParam = safetyBpmProcessService.initProcessAbandonParam(id.toString(), bpmFlowId,
                        VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, userName, auditNote);
                dataStatus = 3;
                break;

            default:
                return fail("流程审批失败，不支持的操作类型: " + operationType);
        }

        String status = safetyBpmProcessService.approveProcessByUserName(id.toString(), bpmFlowId,
                VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, processParam.getProcessParam(), userName);
        // 处理返回状态
        String code = status.substring(0, 1);
        if ("T".equals(code)) {
            update(id, dataStatus, empInfo.getEmpNo());
            return successData(id);
        } else {
            return fail(status);
        }
    }

    private void update(Integer id, Integer status, String updateBy) {
        safetyVisitorAuthorizationApprovalDetailService.updateStatus(id, status, updateBy);
    }

    /**
     * 授权人信息校验
     *
     * @param empNo 员工号
     * @return 返回值 lastNum 剩余被授权人的数量；sectionChief；是否科长审批，true 是，false：不需要
     */
    public VO checkEmpNo(String empNo) {
        SafetyEmployeeMessageDO grantorInfo = safetyEmployeeMessageService.getByNo(empNo);
        if (grantorInfo == null) {
            return fail("授权人不存在");
        }

        SafetyVisitorAuthorizationQueryParam queryParam = new SafetyVisitorAuthorizationQueryParam();
        queryParam.setGrantorInfo(empNo);
        List<SafetyVisitorAuthorizationDto> dataList = safetyVisitorAuthorizationApprovalService.selectList(queryParam, false);
        if (CollectionUtils.isEmpty(dataList)) {
            return fail("请选择主管级以上人员");
        }

        // 数据库中的流程授权数据
        List<SafetyVisitorAuthorizationDto> dtoList = safetyVisitorAuthorizationApprovalService.selectBpmFlowList(empNo);
        Integer configNum = getConfigNum(grantorInfo);

        if (dtoList.size() < configNum) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("lastNum", (configNum-dtoList.size()));
            result.put("sectionChief", EHR_APPLY_EMP_JOB_NAME.contains(grantorInfo.getJobTypeName()));
            return successData(result);
        } else {
            return fail("被授权人数超限");
        }
    }

    /**
     * 获取访客默认配置的人数
     * @param grantorInfo 员工信息
     * @return 返回配置的数量
     */
    private Integer getConfigNum(SafetyEmployeeMessageDO grantorInfo) {
        // 配置信息
        SafetyVisitorAuthorizationConfigQueryParam param = new SafetyVisitorAuthorizationConfigQueryParam();
        param.setEmpNo(grantorInfo.getEmpNo());
        param.setConfigType(1);
        List<SafetyVisitorAuthorizationDO> dataInfo = safetyVisitorAuthorizationService.selectList(param, false);
        Integer num = 4;
        if (!CollectionUtils.isEmpty(dataInfo)) {
            // 人员
            num = dataInfo.get(0).getIntervieweeNum();
        } else {
            // 组织
            param.setEmpNo(null);
            param.setConfigType(0);
            dataInfo = safetyVisitorAuthorizationService.selectList(param, false);
            List<SafetyVisitorAuthorizationDO> collect = dataInfo.stream().filter(li -> grantorInfo.getOrgCodePath().contains(li.getEmpOrgCodePath()) && !StringUtils.equals(li.getEmpOrgCodePath(), "0")).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                // 默认组织获取
                List<SafetyVisitorAuthorizationDO> defaultList = dataInfo.stream().filter(li -> StringUtils.equals(li.getEmpOrgCodePath(), "0")).collect(Collectors.toList());
                // 正常情况下，不存在为空的情况
                if (CollectionUtils.isEmpty(defaultList)) {
                    num = defaultList.get(0).getIntervieweeNum();
                }
            } else {
                // 指定组织- 取最下级的组织
                int length = 2;
                for (SafetyVisitorAuthorizationDO info : collect) {
                    int oldLength = info.getEmpOrgCodePath().length();
                    if (oldLength > length) {
                        length = oldLength;
                        num = info.getIntervieweeNum();
                    }
                }
            }
        }
        return num;
    }

    /**
     * 获取明细数据
     * @param processId 流程ID
     * @return 返回明细数据集
     */
    public VO getDetail(Integer processId) {
        if (processId == null) {
            return fail("流程ID不能为空");
        }
        SafetyVisitorAuthorizationVO info = new SafetyVisitorAuthorizationVO();
        // 主表数据
        SafetyVisitorAuthorizationApprovalDO mainInfo = safetyVisitorAuthorizationApprovalService.selectById(processId);
        BeanUtils.copyProperties(mainInfo, info);
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        String processSubject = year + "年" + month + "月" + day + "日-" + mainInfo.getGrantorOrgName() + "-" + mainInfo.getGrantorNo() + "-" + mainInfo.getGrantorName() + "的访客审批授权流程";
        info.setTitle(processSubject);

        List<SafetyVisitorAuthorizationApprovalDetailDO> list = safetyVisitorAuthorizationApprovalDetailService.selectList(processId);
        info.setDetailList(list);
        if (!CollectionUtils.isEmpty(list)) {
            info.setStatus(list.get(0).getStatus());
        }
        return successData(info);
    }

    /**
     * 访客审批授权状态变更
     * @param processId 流程ID
     * @param code 状态值
     */
    public void updateFlowStatus(String processId, String code) {
        LogUtils.info("SafetyVisitorAuthorizationApprovalHandler.updateFlowStatus:processId" +  processId + "code:" + code);
        // 获取流程信息
        SafetyVisitorAuthorizationQueryParam listParam = new SafetyVisitorAuthorizationQueryParam();
        listParam.setBpmFlowId(processId);
        List<SafetyVisitorAuthorizationDto> dataList = safetyVisitorAuthorizationApprovalService.selectList(listParam, false);
        if (CollectionUtils.isEmpty(dataList)) {
            LogUtils.error(ResponseEnum.FAIL.getCode(), "未找到对应ID=" + processId + "的BPM流程的员工权限调整信息");
            return;
        }
        // 判断流程状态
        SafetyVisitorAuthorizationDto processDO = dataList.get(0);
        //已废弃的流程直接退出
        if (processDO.getStatus() == 3) {
            return;
        }

        // 变更流程状态
        SafetyVisitorAuthorizationApprovalDetailDO param = new SafetyVisitorAuthorizationApprovalDetailDO();
        param.setId(processDO.getAuthorizationApprovalId());
        String format = DateUtils.format(new Date(), 1);
        int status = 2;
        if (StringUtils.equals(format, processDO.getAuthorizationEndTime()) || DateUtils.isBeforeStr(format, processDO.getAuthorizationEndTime())) {
            status = 1;
        }
        this.update(processDO.getAuthorizationApprovalId(), status, null);

        // 发送消息
        if (status == 1) {
            safetySendYxtHandler.sendYxtMsgByAgreeVisitor(dataList);
        }
    }

    /**
     * 组织变更的人员
     * @param empNos 组织变更的人员信息
     */
    public void positionChange(Set<String> empNos) {
        if (CollectionUtils.isEmpty(empNos)) {
            return;
        }
        // 被授权人组织变更
        List<String> list = new ArrayList<>(empNos);
        SafetyVisitorAuthorizationQueryParam listParam = new SafetyVisitorAuthorizationQueryParam();
        listParam.setGranteeNoList(list);
        listParam.setStatus(1);
        List<SafetyVisitorAuthorizationDto> dtoList = safetyVisitorAuthorizationApprovalService.selectList(listParam, false);
        if (!CollectionUtils.isEmpty(dtoList)) {
            // 存在数据，注销+发送消息
            List<Integer> ids = dtoList.stream().map(SafetyVisitorAuthorizationApprovalDO::getId).collect(Collectors.toList());
            safetyVisitorAuthorizationApprovalDetailService.cancelAuthorizationInfo(ids, "被授权人调岗/离职");
            safetySendYxtHandler.sendYxtMsgByPositionChangeOfVisitor(dtoList, "postBsq");
        }

        // 授权人组织变更
        SafetyVisitorAuthorizationQueryParam listParam2 = new SafetyVisitorAuthorizationQueryParam();
        listParam2.setGrantorNoList(list);
        listParam2.setStatus(1);
        dtoList = safetyVisitorAuthorizationApprovalService.selectList(listParam2, false);
        if (!CollectionUtils.isEmpty(dtoList)) {
            // 存在数据，注销+发送消息
            List<Integer> ids = dtoList.stream().map(SafetyVisitorAuthorizationApprovalDO::getId).collect(Collectors.toList());
            safetyVisitorAuthorizationApprovalDetailService.cancelAuthorizationInfo(ids, "授权人调岗/离职");
            safetySendYxtHandler.sendYxtMsgByPositionChangeOfVisitor(dtoList, "postSq");
        }
    }

    public VO checkSectionChief(String empNo) {
        SafetyEmployeeMessageDO grantorInfo = safetyEmployeeMessageService.getByNo(empNo);
        if (grantorInfo == null) {
            return fail("数据不存在");
        }
        if (!EHR_APPLY_EMP_JOB_NAME.contains(grantorInfo.getJobTypeName())) {
            return fail("请科长/经理以上人员审批");
        }

        return success();
    }

    /**
     * 审批流程
     */
    public VO approvalProcess(SafetyVisitorAuthorizationApprovalParam info) {
        try {
            if (info.getId() == null) {
                return VoUtil.fail("流程ID不存在");
            }
            SafetyVisitorAuthorizationApprovalDO mainInfo = safetyVisitorAuthorizationApprovalService.selectById(info.getId());
            if (mainInfo == null || StringUtils.isBlank(mainInfo.getBpmFlowId())) {
                return fail("流程数据不存在");
            }

            String applyEmpNo = mainInfo.getApplyEmpNo();
            String grantorNo = mainInfo.getGrantorNo();
            if (!info.getEmpNo().equals(grantorNo) && (!StringUtils.isNotBlank(applyEmpNo) || !info.getEmpNo().equals(applyEmpNo))) {
                return fail("当前操作人没有审批权限");
            }

            // 审核人提交节点
            return reviewBpmFlow(info, mainInfo.getBpmFlowId());
        } catch (BizException e) {
            return fail(e.getMsg());
        }
    }

    /**
     * 审核流程
     */
    private VO reviewBpmFlow(SafetyVisitorAuthorizationApprovalParam info, String bpmFlowId) {
        SafetyEmployeeMessageDO applyInfo = safetyEmployeeMessageService.getByNo(info.getEmpNo());
        if (applyInfo == null) {
            return fail("当前审批人不存在");
        }

        String userName = applyInfo.getEmpLogin();
        if (StringUtils.isBlank(userName)) {
            return fail("用户名不存在");
        }

        // 初始化流程参数
        BpmProcessParam processParam;

        switch (info.getApprovalType()) {
            case 0:
                // 审批通过
                processParam = safetyBpmProcessService.initProcessParam(info.getId().toString(), bpmFlowId,
                        VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, userName, false, "", "");
                break;

            case 1:
                // 审批驳回
                processParam = safetyBpmProcessService.initProcessRejectParam(info.getId().toString(), bpmFlowId,
                        VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, userName, false, "", "");
                break;

            default:
                return fail("流程审批失败，不支持的操作类型: " + info.getApprovalType());
        }

        String status = safetyBpmProcessService.approveProcessByUserName(info.getId().toString(), bpmFlowId,
                VISITOR_MODEL_ID, VISITOR_PROCESS_FORM_ID, processParam.getProcessParam(), userName);
        // 处理返回状态
        String code = status.substring(0, 1);
        if ("T".equals(code)) {
            return success();
        } else {
            return fail(status);
        }
    }

    /**
     * 校验被访人是否为主管及上级
     * @param workNo 员工号
     * @return true 是， false 不是
     */
    public VO checkInterviewee(String workNo) {
        if (StringUtils.isBlank(workNo)) {
            return fail("员工号为空");
        }
        // 授权人
        SafetyVisitorAuthorizationQueryParam listParam = new SafetyVisitorAuthorizationQueryParam();
        listParam.setGrantorInfo(workNo);
        listParam.setStatus(1);
        List<SafetyVisitorAuthorizationDto> dtoList = safetyVisitorAuthorizationApprovalService.selectList(listParam, false);
        if (!CollectionUtils.isEmpty(dtoList)) {
            return successData(true);
        }
        // 被授权人
        SafetyVisitorAuthorizationQueryParam listParam2 = new SafetyVisitorAuthorizationQueryParam();
        listParam2.setGranteeInfo(workNo);
        listParam2.setStatus(1);
        dtoList = safetyVisitorAuthorizationApprovalService.selectList(listParam2, false);
        if (!CollectionUtils.isEmpty(dtoList)) {
            return successData(true);
        }
        return successData(false);
    }

    /**
     * 1V1 海康校验， 校验结果返回给前端
     * @param visitorIdCardVO 照片校验信息
     * @return 返回校验结果和
     */
    public VO checkIdCardPhoto(SafetyVisitorIdCardVO visitorIdCardVO) {
        String facePhoto = visitorIdCardVO.getFacePhoto();
        String idCardPhoto = visitorIdCardVO.getIdCardPhoto();
        if (StringUtils.isBlank(facePhoto) || StringUtils.isBlank(idCardPhoto)) {
            return fail(ResponseEnum.PARAM_ERROR.getCode(),"人脸照片和身份证照片不能为空！");
        }

        // 1V1校验
        Boolean aBoolean = safetyArtemisHandler.checkOneToOne(facePhoto, idCardPhoto, UserTypeEnum.WX, SafetyHikThresholdEnum.VISIT_FACE_ID_CARD);
        return successData(aBoolean);
    }
}
