package com.yutong.safety.core.handler.spot;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.param.web.spot.PatrolNodeUpdateParam;
import com.yutong.safety.common.param.web.spot.PatrolRecordAddParam;
import com.yutong.safety.common.param.web.spot.PatrolRecordListParam;
import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.spot.PatrolRecordDTO;
import com.yutong.safety.common.vo.web.spot.PatrolRecordVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.bpm.BpmCurNodeVO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.bpm.operation.BpmUserVO;
import com.yutong.safety.core.db.entity.spot.*;
import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import com.yutong.safety.core.dto.SafetyProcessPatrolDTO;
import com.yutong.safety.core.dto.spot.SafetyProcessPatrolHistoryDTO;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.spot.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 治安巡查-巡查记录
 * @author gwxiexj
 * @since 2024-05-29 16:48
 */
@Component
public class SafetyPatrolRecordHandler {

    private static final String PATROL_MODEL_ID = "security_patrol";
    private static final String PATROL_PROCESS_FORM_ID = "safety_process_patrol";
    private static final String NODE_ID_N14 = "N14";

    @Autowired
    private final SafetyBpmProcessService safetyBpmProcessService;

    @Autowired
    private final SafetyPatrolRecordService safetyPatrolRecordService;

    @Autowired
    private final SafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    private final SafetySysUserService safetySysUserService;

    @Autowired
    private final SafetyPatrolTaskService safetyPatrolTaskService;

    @Autowired
    private final SafetyPatrolPlanService safetyPatrolPlanService;

    @Autowired
    private final SafetyPatrolRecordHistoryService safetyPatrolRecordHistoryService;

    @Autowired
    private final SafetyPatrolDealHistoryService safetyPatrolDealHistoryService;

    @Autowired
    private SafetyBpmWebServiceService safetyBpmWebServiceService;

    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    public SafetyPatrolRecordHandler(SafetyBpmProcessService safetyBpmProcessService, SafetyPatrolRecordService safetyPatrolRecordService, SafetyWaterFlowNoService safetyWaterFlowNoService, SafetySysUserService safetySysUserService, SafetyPatrolTaskService safetyPatrolTaskService, SafetyPatrolPlanService safetyPatrolPlanService, SafetyPatrolRecordHistoryService safetyPatrolRecordHistoryService, SafetyPatrolDealHistoryService safetyPatrolDealHistoryService) {
        this.safetyBpmProcessService = safetyBpmProcessService;
        this.safetyPatrolRecordService = safetyPatrolRecordService;
        this.safetyWaterFlowNoService = safetyWaterFlowNoService;
        this.safetySysUserService = safetySysUserService;
        this.safetyPatrolTaskService = safetyPatrolTaskService;
        this.safetyPatrolPlanService = safetyPatrolPlanService;
        this.safetyPatrolRecordHistoryService = safetyPatrolRecordHistoryService;
        this.safetyPatrolDealHistoryService = safetyPatrolDealHistoryService;
    }

    /**
     * 创建流程-保存信息
     * @param dto 入参
     * @param token TOKEN
     * @return 返回值
     */
    public Object saveAndCreateProcess(PatrolRecordAddParam dto, String token) {
        String flowId = "";
        dto.setCurNodeId("N2"); //提交起草节点
        //判断是否周期任务
        Integer taskId = null;
        if(dto.getEventSource()==1){
            PatrolTaskListParam param = new PatrolTaskListParam();
            param.setFactoryId(dto.getFactoryId()!=null?dto.getFactoryId().toString():"");
            param.setPlanId(dto.getPlanId());
            param.setTaskBeginDate(dto.getTaskBeginDate());
            param.setTaskEndDate(dto.getTaskEndDate());
            List<SafetyPatrolTaskDO> taskDOS = safetyPatrolTaskService.getList(param);
            if(!CollectionUtils.isEmpty(taskDOS)){
                taskId = taskDOS.get(0).getId();
            }
        }
        dto.setCreateTime(new Date());
        dto.setTaskId(taskId);
        int isException = dto.getIsException();
        //巡查异常发起流程
        if(isException == 1){
            flowId = safetyBpmProcessService.createAndApproveProcess(dto.getTitle(),
                    PATROL_MODEL_ID, PATROL_PROCESS_FORM_ID, token);
            dto.setFlowId(flowId.split(",")[1]);
            dto.setStatus("20");
        }else{
            dto.setStatus("30");
        }

        String flowNo = getFlowNo();
        dto.setFlowNo(flowNo);
        safetyPatrolRecordService.save(dto);
        return flowId;
    }

    /**
     * 更新事件信息-不涉及流程流转
     * @param updateParam 入参
     * @return 更新状态
     */
    public Object modifyProcess(SafetyProcessPatrolHistoryDTO updateParam) throws Exception{
        int rows = 0;
        PatrolRecordAddParam patrolRecordAddParam = new PatrolRecordAddParam();
        BeanUtils.copyProperties(updateParam,patrolRecordAddParam);
        patrolRecordAddParam.setId(updateParam.getPatrolId());
        rows = safetyPatrolRecordService.update(patrolRecordAddParam);

        SafetyPatrolRecordHistoryDO recordHistoryDO = new SafetyPatrolRecordHistoryDO();
        BeanUtils.copyProperties(updateParam,recordHistoryDO);
        recordHistoryDO.setCreateTime(new Date());
        recordHistoryDO.setProcessId(updateParam.getPatrolId()); //赋值安保流程记录ID
        recordHistoryDO.setEventFlowNo(updateParam.getFlowNo()); //事件编号
        recordHistoryDO.setCreateperson(updateParam.getUpdateUser());
        safetyPatrolRecordHistoryService.insert(recordHistoryDO);

        //记录记录
        SafetyProcessPatrolHistoryDO en = new SafetyProcessPatrolHistoryDO();
        BeanUtils.copyProperties(updateParam,en);
        en.setCreateTime(new Date());

        //获取上次的数据进行比较，得出改动的内容
        List<SafetyProcessPatrolHistoryDO> recordList = safetyPatrolRecordService.getRecordList(en);
        if(!CollectionUtils.isEmpty(recordList)){
            SafetyProcessPatrolHistoryDO s = recordList.get(0);
            StringBuilder sb = new StringBuilder();

            //发生时间
            if(!s.getOccurTime().equals(en.getOccurTime())){
                sb.append("occurTime,");
            }

            //厂区
            if(!s.getFactoryId().equals(en.getFactoryId())){
                sb.append("factoryId,factory,");
            }

            //事件类型
            if(!s.getEventType().equals(en.getEventType())){
                sb.append("eventType,");
            }

            //事项
            if(!s.getTeamId().equals(en.getTeamId())){
                sb.append("teamId,teamName,");
            }

            //事件等级
            if(!s.getEventLevel().equals(en.getEventLevel())){
                sb.append("eventLevel,");
            }

            //责任部门
            if(!s.getDutyDept().equals(en.getDutyDept())){
                sb.append("dutyDept,");
            }

            //地理位置
            if(!s.getEventAddress().equals(en.getEventAddress())){
                sb.append("eventAddress,");
            }

            //是否重点部位
            if(!s.getIsKeynote().equals(en.getIsKeynote())){
                sb.append("isKeynote,");
            }

            //是否重点部位
            if(!s.getProblemDesc().equals(en.getProblemDesc())){
                sb.append("problemDesc,");
            }

            //是否当场处理
            if(!s.getIsScene().equals(en.getIsScene())){
                sb.append("isScene,");
            }

            en.setModifyColumn(sb!=null?sb.toString():"");
        }
        safetyPatrolRecordService.saveUpdateHistory(en);

        return rows;
    }


    /**
     * 创建流程-保存信息
     * @param dto 入参
     * @return 返回值
     */
    public Object saveAndCreateProcessByUserName(PatrolRecordAddParam dto) {

        String flowId = "";
        //流程提交人用户名，不能为空
        String creator = dto.getCreator();
        if(StringUtils.isBlank(creator)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"“提交人用户名creator”不能为空");
        }
        Integer factoryId = dto.getFactoryId();
        if(factoryId == null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"“工厂factoryId”不能为空");
        }
        Integer eventSource = dto.getEventSource();
        if(eventSource == null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"“事件来源eventSource”不能为空");
        }
        //周期任务获取相关信息
        if(eventSource==1 && dto.getTaskId() == null){
            PatrolTaskListParam param = new PatrolTaskListParam();
            param.setFactoryId(dto.getFactoryId()!=null?dto.getFactoryId().toString():"");
            param.setPlanId(dto.getPlanId());
            param.setTaskBeginDate(dto.getTaskBeginDate());
            param.setTaskEndDate(dto.getTaskEndDate());
            param.setPatrolEmpNo(dto.getSubmitNo());
            List<SafetyPatrolTaskDO> taskDOS = safetyPatrolTaskService.getList(param);
            if(!CollectionUtils.isEmpty(taskDOS)){
                Integer taskId = taskDOS.get(0).getId();
                dto.setTaskId(taskId);
            }
        }
        dto.setCreateTime(new Date());
        Integer isException = dto.getIsException();
        //巡查异常发起流程
        if(isException == 1){
            //nodeFlag = -1 标识流程结束，保卫联系人由前端传值
            if(dto.getNodeFlag() != null && dto.getNodeFlag() != -1){
                //流程发起时，根据工厂和角色指定下个节点审批人
                String[] guardPerson = getGuardPerson(dto.getEventSource(),dto.getPlanId(),dto.getFactoryId());
                dto.setGuardPersonNo(guardPerson[0]);
                dto.setGuardPerson(guardPerson[1]);
            }
            dto.setStatus("20");
        }else{
            dto.setIsException(0);
            dto.setStatus("30");
        }

        String flowNo = getFlowNo();
        dto.setFlowNo(flowNo);
        int id = safetyPatrolRecordService.save(dto);
        //手动提交事务
        //创建流程
        if(isException == 1 && id>0){
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(dto.getTitle(),
                    PATROL_MODEL_ID, PATROL_PROCESS_FORM_ID, creator,String.valueOf(id));
            if(StringUtils.isNotBlank(returnFlowId)){
                flowId = returnFlowId.split(",")[1];
            }
            dto.setFlowId(flowId);
            PatrolNodeUpdateParam nodeParam = new PatrolNodeUpdateParam(id,flowId,"");
            safetyPatrolRecordService.modifyFlowId(nodeParam);
        }

        return id+","+flowId;
    }

    /**
     * 更新流程
     * @param param 入参
     * @param token TOKEN
     * @return 返回值
     */
    public Object updateAndAudit(PatrolRecordAddParam param, String token) {
        //更新表单数据
        safetyPatrolRecordService.update(param);
        BpmProcessParam processParam = safetyBpmProcessService.initProcessParam(param.getId()+"",param.getFlowId(),
                PATROL_MODEL_ID,PATROL_PROCESS_FORM_ID,param.getCurPersonNo(),false,param.getAuditNote(),"");
        String status = safetyBpmProcessService.approveProcess(param.getId()+"", param.getFlowId(),
                PATROL_MODEL_ID, PATROL_PROCESS_FORM_ID,processParam.getProcessParam(), token);
        return status.substring(0,1);
    }

    /**
     * 更新流程
     * @param param 入参
     * @return 返回值
     */
    public BpmNodeReturnVO updateAndAuditByUserName(PatrolRecordAddParam param) {
        //流程提交人用户名，不能为空
        String curPersonNo = param.getCurPersonNo();
        if(StringUtils.isBlank(curPersonNo) || param.getId() ==null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"当前流程处理人用户名不能为空");
        }

        SafetyProcessPatrolDO curProcess = safetyPatrolRecordService.getProcessInfoById(param.getId());
        if(curProcess == null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"未找到对应的流程，id="+param.getId());
        }
        //gwxiexj 解决问题：N7节点和N14节点为同一处理人时，用户在N7节点提交后刷新详情页，BPM还未流转到N14节点，导致用户重新提交的数据不是N14节点的数据
        if(NODE_ID_N14.equals(curProcess.getCurNodeId()) && StringUtils.isBlank(param.getDealTime())){
            throw new BizException(ResponseEnum.FAIL.getCode(),"处理信息缺失，请刷新后重新填写!");
        }

        //判断是否存在处理人
        if(StringUtils.isNotBlank(param.getDealPersonNo()) && StringUtils.isBlank(param.getDealPersonGroup())){
            SafetyEmployeeMessageDO employeeMessageDO = safetyEmployeeMessageService.getByNo(param.getDealPersonNo());
            if(!ObjectUtils.isEmpty(employeeMessageDO)){
                param.setDealPersonGroup(employeeMessageDO.getOrgName());
            }
        }
        //更新表单数据
        safetyPatrolRecordService.update(param);
        //手动提交事务
        BpmProcessParam processParam = safetyBpmProcessService.initProcessParam(param.getId()+"",param.getFlowId(),
                PATROL_MODEL_ID,PATROL_PROCESS_FORM_ID,curPersonNo,false,param.getAuditNote(),"");
        String status = safetyBpmProcessService.approveProcessByUserName(param.getId()+"", param.getFlowId(),
                PATROL_MODEL_ID, PATROL_PROCESS_FORM_ID,processParam.getProcessParam(), curPersonNo);
        //流程审批通过后再更新数据
        if(status!=null && status.startsWith("T")){
            //评分人不为空时保存处理结果
            if(StringUtils.isNotBlank(param.getResultPersonNo())){
                SafetyProcessPatrolDTO processPatrolDO = safetyPatrolRecordService.getDetailById(param.getId());

                SafetyPatrolDealHistoryDO dealHistoryDO = new SafetyPatrolDealHistoryDO();
                BeanUtils.copyProperties(processPatrolDO, dealHistoryDO);
                dealHistoryDO.setCreateTime(new Date());
                dealHistoryDO.setProcessId(param.getId());
                safetyPatrolDealHistoryService.save(dealHistoryDO);
            }
        }

        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        returnVO.setStatus(StringUtils.isBlank(status)?"":status.substring(0,1));
        returnVO.setMessage(status);

        return returnVO;
    }


    /**
     * 转派流程
     * @param param 入参
     * @return 返回值
     */
    public BpmNodeReturnVO updateTransferByUserName(PatrolRecordAddParam param) {
        //流程提交人用户名，不能为空
        String curPersonNo = param.getCurPersonNo();
        if(StringUtils.isBlank(curPersonNo)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"当前流程处理人用户名不能为空！");
        }
        String transferUserName = param.getDutyPersonNo();
        if(StringUtils.isBlank(transferUserName)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"未指定转派人！");
        }
        //更新表单数据
        safetyPatrolRecordService.update(param);
        //处理拼装转办的表单数据
        BpmProcessParam processParam = safetyBpmProcessService.initProcessParam(param.getId()+"",param.getFlowId(),
                PATROL_MODEL_ID,PATROL_PROCESS_FORM_ID,param.getCurPersonNo(),true,param.getAuditNote(),param.getDutyPersonNo());
        //提交
        String status = safetyBpmProcessService.approveProcessByUserName(param.getId()+"", param.getFlowId(),
                PATROL_MODEL_ID, PATROL_PROCESS_FORM_ID,processParam.getProcessParam(), curPersonNo);
        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        returnVO.setStatus(status.substring(0,1));
        returnVO.setMessage(status);

        return returnVO;
    }

    /**
     * 获取事件编号
     * @return 返回值
     */
    private String getFlowNo(){
        String date = DateUtils.format(new Date(),7);
        String nowNum = safetyWaterFlowNoService.getNowByType("patrol_record");
        return Joiner.on("-").join("ZAXC",date+nowNum);
    }

    /**
     * 获取详情
     * @param id 入参ID
     * @return 返回值
     */
    public SafetyProcessPatrolDTO getDetail(int id) {
        return safetyPatrolRecordService.getDetailById(id);
    }

    /**
     * 获取列表
     * @param processDTO 查询参数
     * @param token TOKEN
     * @return 返回值
     * @throws Exception
     */
    public Object getList(PatrolRecordListParam processDTO, String token) throws Exception{
        //获取登录用户授权工厂
        String workNo = TokenUtils.getWorkNo(token);
        String factoryId = safetySysUserService.getPowerFactory(workNo);
        processDTO.setFactoryIdStr(factoryId);
        processDTO.setPlanType(1); //只查看治安巡查

        if(!StringUtils.isBlank(processDTO.getCreateEnd())){
            String createEnd = processDTO.getCreateEnd();
            processDTO.setCreateEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(createEnd,1),1),1));
        }

        if(!StringUtils.isBlank(processDTO.getOccurEnd())){
            String occurEnd = processDTO.getOccurEnd();
            processDTO.setOccurEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(occurEnd,1),1),1));
        }

        if(!StringUtils.isBlank(processDTO.getDealFinishEnd())){
            String dealFinishEnd = processDTO.getDealFinishEnd();
            processDTO.setDealFinishEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(dealFinishEnd,1),1),1));
        }

        //查询流程前缀
        String bpmUrlPre = getBpmPreUrl("safety_process_patrol");
        processDTO.setBpmUrlPre(bpmUrlPre);

        //出门证异常上报事件的流程前缀
        String goodsExceptionBpmUrlPre = getBpmPreUrl("safety_goods_out_events_process");
        processDTO.setGoodsExceptionBpmUrlPre(goodsExceptionBpmUrlPre);

        //出门证异常上报事件的流程前缀
        String goodsOutBpmUrlPre = getBpmPreUrl("safety_goods_out_process");
        processDTO.setGoodsOutBpmUrlPre(goodsOutBpmUrlPre);

        //查询
        List<PatrolRecordVO> list = safetyPatrolRecordService.pageList(processDTO);
        long total = safetyPatrolRecordService.pageListCount(processDTO);
        return new PageVO<>(total, list);
    }

    /**
     * 拼接bpm链接
     * @param formId bpm的formId
     * @return 返回值
     */
    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }

    /**
     * 导出
     * @param processDTO 入参
     * @param token TOKEN
     * @return 返回值
     */
    public HSSFWorkbook exportData(PatrolRecordListParam processDTO, String token) {
        //获取登录用户授权工厂
        String workNo = TokenUtils.getWorkNo(token);
        String factoryId = safetySysUserService.getPowerFactory(workNo);
        if(processDTO.getFactory() == null){
            processDTO.setFactory(factoryId);
        }

        if(!StringUtils.isBlank(processDTO.getCreateEnd())){
            String createEnd = processDTO.getCreateEnd();
            processDTO.setCreateEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(createEnd,1),1),1));
        }

        if(!StringUtils.isBlank(processDTO.getOccurEnd())){
            String occurEnd = processDTO.getOccurEnd();
            processDTO.setOccurEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(occurEnd,1),1),1));
        }

        if(!StringUtils.isBlank(processDTO.getDealFinishEnd())){
            String dealFinishEnd = processDTO.getDealFinishEnd();
            processDTO.setDealFinishEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(dealFinishEnd,1),1),1));
        }

        processDTO.setPlanType(1); //只查看治安巡查
        List<PatrolRecordVO> list = safetyPatrolRecordService.getList(processDTO);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"事件编号","厂区","任务名称","事件类型","事项","是否存在异常","事件等级","责任部门","事件发生时间",
                "地理位置","是否重点部位","问题描述","事件现场图片","预计处理完成日期","计划处理措施","是否当场处理","是否转派","转派部门",
                "处理人","处理人部门班组","保卫联系人","处理完成时间","时间处理过程及结果","处理完成图片","时间处理结果及过程评分","得分原因",
                "评分人","是否延期处理","事件上报人","上报人部门班组","事件上报时间","是否指挥中心派单","指派人","是否抢单","抢单人","接单时间",
                "返工次数"};
        String[][] values = new String[list.size()][];
        PatrolRecordVO safetyProcessPatrolDO;
        for(int i=0; i<list.size(); i++) {
            safetyProcessPatrolDO = list.get(i);

            int isTranfer = safetyProcessPatrolDO.getIsTransfer()== null?0:safetyProcessPatrolDO.getIsTransfer();
            int isGrab = safetyProcessPatrolDO.getIsGrab()==null?0:safetyProcessPatrolDO.getIsGrab();
            int isDispatch = safetyProcessPatrolDO.getIsDispatch()==null?0:safetyProcessPatrolDO.getIsDispatch();
            int isDelay= safetyProcessPatrolDO.getIsDelay()==null?0:safetyProcessPatrolDO.getIsDelay();
            int isScene= safetyProcessPatrolDO.getIsScene()==null?0:safetyProcessPatrolDO.getIsScene();
            int isKeynote = safetyProcessPatrolDO.getIsKeynote()==null?0:safetyProcessPatrolDO.getIsKeynote();
            int isException = safetyProcessPatrolDO.getIsException()==null?0:safetyProcessPatrolDO.getIsException();

            values[i] = new String[title.length];
            values[i][0] = safetyProcessPatrolDO.getFlowNo();  //事件编号
            values[i][1] = safetyProcessPatrolDO.getFactory();  //厂区
            values[i][2] = safetyProcessPatrolDO.getPlanName();;  //任务名称
            values[i][3] = safetyProcessPatrolDO.getEventType();  //事件类型
            values[i][4] = safetyProcessPatrolDO.getTeamName();  //事项
            values[i][5] = isException==0?"否":"是";  //是否存在异常
            values[i][6] = safetyProcessPatrolDO.getEventLevel();  //事件等级
            values[i][7] = safetyProcessPatrolDO.getDutyDept();  //责任部门
            values[i][8] = safetyProcessPatrolDO.getOccurTime();  //事件发生时间
            values[i][9] = safetyProcessPatrolDO.getEventAddress();  //地理位置
            values[i][10] = isKeynote==0?"否":"是";  //是否重点部位
            values[i][11] = safetyProcessPatrolDO.getProblemDesc();  //问题描述
            values[i][12] = safetyProcessPatrolDO.getProblemImage();  //事件现场图片
            values[i][13] = safetyProcessPatrolDO.getPlanDealTime();  //预计处理完成日期
            values[i][14] = safetyProcessPatrolDO.getPlanMeasure();  //计划处理措施
            values[i][15] = isScene==1?"是":"否";  //是否当场处理
            values[i][16] = isTranfer==0?"否":"是";  //是否转派
            values[i][17] = safetyProcessPatrolDO.getTransferDept();  //转派部门
            values[i][18] = safetyProcessPatrolDO.getDealPerson();   //处理人
            values[i][19] = safetyProcessPatrolDO.getDealPersonGroup();  //处理人部门班组
            values[i][20] = safetyProcessPatrolDO.getGuardPerson();  //保卫联系人
            values[i][21] = safetyProcessPatrolDO.getDealTime();   //处理完成时间
            values[i][22] = safetyProcessPatrolDO.getDealRecord();  //时间处理过程及结果
            values[i][23] = safetyProcessPatrolDO.getDealImage();  //处理完成图片
            values[i][24] = safetyProcessPatrolDO.getResultScore();  //时间处理结果及过程评分
            values[i][25] = safetyProcessPatrolDO.getResultDesc();  //得分原因
            values[i][26] = safetyProcessPatrolDO.getResultPerson();  //评分人
            values[i][27] = isDelay==1?"是":"否";  //是否延期处理
            values[i][28] = safetyProcessPatrolDO.getSubmitName();  //事件上报人
            values[i][29] = safetyProcessPatrolDO.getSubmitDept();  //上报人部门班组
            values[i][30] = safetyProcessPatrolDO.getCreateTime();  //事件上报时间
            values[i][31] = isDispatch == 1?"是":"否";  //是否指挥中心派单
            values[i][32] = safetyProcessPatrolDO.getDispatchPerson();  //指派人
            values[i][33] = isGrab == 1?"是":"否";  //是否抢单
            values[i][34] = safetyProcessPatrolDO.getGrabPerson();  //抢单人
            values[i][35] = safetyProcessPatrolDO.getGrabTime();  //接单时间
            values[i][36] = safetyProcessPatrolDO.getReworkNum()+""; //返工次数
        }
        String sheetName = "巡查业务数据";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 获取治安管理员角色
     * @param eventSource 事件来源 1-周期 2-上报
     * @param planId  周期任务ID
     * @param factoryId 事件工厂
     * @return 返回值
     */
    private String[] getGuardPerson(Integer eventSource,Integer planId,Integer factoryId){
        String[] guardPerson = new String[2];
        Integer roleId = null;
        UserMessageListParam param = new UserMessageListParam();

        if(eventSource == 1 && planId != null){
            SafetyPatrolPlanDO planDO = safetyPatrolPlanService.getBeanById(planId);
            if(planDO != null){
                roleId = planDO.getEventRoleId();
            }
        }
        param.setRoleId(roleId);
        if(roleId == null){
            param.setRoleName("治安管理员");
        }
        param.setPowerFactoryId(String.valueOf(factoryId));
        List<SafetySysUserDO> sysUserDOList = safetySysUserService.getList(param);
        if(!CollectionUtils.isEmpty(sysUserDOList)){
            String personNo = sysUserDOList.stream().map(SafetySysUserDO::getUserName).collect(Collectors.joining(","));
            String personStr = sysUserDOList.stream().map(SafetySysUserDO::getEmpName).collect(Collectors.joining(","));
            guardPerson[0] = personNo;
            guardPerson[1] = personStr;
        }
        return guardPerson;
    }

    /**
     * 获取列表-不过滤工厂权限
     * @param processDTO 查询参数
     * @return 返回值
     */
    public List<PatrolRecordDTO> getListNoPower(PatrolRecordListParam processDTO) {
        List<PatrolRecordDTO> recordDTOs = new ArrayList<>();

        //查询
        List<PatrolRecordVO> reportVOS = safetyPatrolRecordService.getList(processDTO);

        if(CollectionUtils.isEmpty(reportVOS)){
            return recordDTOs;
        }
        Map<String,List<PatrolRecordVO>> voMap = reportVOS.stream().collect(Collectors.groupingBy(PatrolRecordVO::getEventType));
        for(Map.Entry<String, List<PatrolRecordVO>> entry:voMap.entrySet()){
            PatrolRecordDTO curDto = new PatrolRecordDTO();
            curDto.setTypeName(entry.getKey());
            curDto.setSubList(entry.getValue());

            recordDTOs.add(curDto);
        }
        return recordDTOs;
    }

    /**
     * 获取列表-根据角色过滤工厂权限
     * @param processDTO 查询参数
     * @return 返回值
     */
    public List<PatrolRecordDTO> getListByRolePower(PatrolRecordListParam processDTO) {
        //当前登录人
        String creator = processDTO.getCreator();
        if(StringUtils.isBlank(creator)){
            throw new BizException("2000","未取到登录人信息请给“creator”赋值");
        }
        List<PatrolRecordDTO> recordDTOs = new ArrayList<>();
        // 角色“领导”可查看（全部查看权限）；角色“主管”及“治安管理员”查看授权厂区下上报的事件
        Map<String,String> roleMap = new HashMap<>();
        roleMap.put("userName",creator);
        roleMap.put("roleName","领导");
        String adminRole = safetyPatrolPlanService.selectRoleUser(roleMap);
        if(StringUtils.isBlank(adminRole)){
            roleMap.put("roleName","主管,治安管理员");
            String showRole = safetyPatrolPlanService.selectRoleUser(roleMap);
            if(StringUtils.isBlank(showRole)){
                processDTO.setFactoryIdStr("0");
            }else{
                String factoryIds = "0";
                SafetySysUserDO sysUserDO = safetySysUserService.getSysUserByUserName(creator);
                if(!ObjectUtils.isEmpty(sysUserDO) && StringUtils.isNotBlank(sysUserDO.getPowerFactoryId())){
                    factoryIds = sysUserDO.getPowerFactoryId();
                }
                processDTO.setFactoryIdStr(factoryIds);
            }
        }
        //检索条件取消上报人条件过滤
        processDTO.setCreator("");
        List<PatrolRecordVO> reportVOS = safetyPatrolRecordService.getList(processDTO);
        if(CollectionUtils.isEmpty(reportVOS)){
            return recordDTOs;
        }
        Map<String,List<PatrolRecordVO>> voMap = reportVOS.stream().collect(Collectors.groupingBy(PatrolRecordVO::getEventType));
        for(Map.Entry<String, List<PatrolRecordVO>> entry:voMap.entrySet()){
            PatrolRecordDTO curDto = new PatrolRecordDTO();
            curDto.setTypeName(entry.getKey());
            curDto.setSubList(entry.getValue());

            recordDTOs.add(curDto);
        }
        return recordDTOs;
    }

    public int modifyCurDealHandler(PatrolNodeUpdateParam param){
        //获取审批后当前节点审批人
        List<BpmUserVO> bpmUserVOS = safetyBpmProcessService.getApproverListNew(param.getId().toString(),param.getFlowId(),PATROL_PROCESS_FORM_ID);
        String curHandler = "";
        if(!CollectionUtils.isEmpty(bpmUserVOS)){
            curHandler = bpmUserVOS.stream().map(BpmUserVO::getLoginName).collect(Collectors.joining(","));
        }
        param.setCurDealHandler(curHandler);

        return safetyPatrolRecordService.modifyCurDealHandler(param);
    }

    public int batchModifyProcessHandler(String flowIdStr){
        if(StringUtils.isBlank(flowIdStr)){
            PatrolRecordListParam listParam = new PatrolRecordListParam();
            listParam.setStatus(20);
            listParam.setNodeIdIsNull("1"); //获取nodeId是null的
            List<SafetyProcessPatrolDO> processList = safetyPatrolRecordService.selectByExample(listParam);
            if(!CollectionUtils.isEmpty(processList)){
                int totalPage = processList.size()/500+(processList.size()%500>0?1:0);
                for(int i=0;i<totalPage;i++){
                    List<SafetyProcessPatrolDO> subList = processList.subList(i*500, Math.min((i + 1) * 500, processList.size()));
                    flowIdStr = subList.stream().map(SafetyProcessPatrolDO::getFlowId).collect(Collectors.joining(","));
                    batchModifyGoodsProcess(flowIdStr);
                }
            }
        }else{
            batchModifyGoodsProcess(flowIdStr);
        }
        return 1;
    }

    /**
     * 批量更新出门证流程状态
     * @param flowIdStr-出门证ID拼接串
     * @return 返回值
     */
    public String batchModifyGoodsProcess(String flowIdStr){
        String status = "";
        List<BpmCurNodeVO> curNodeVOList = safetyBpmProcessService.getFlowInfoForSelf(flowIdStr);
        if(CollectionUtils.isEmpty(curNodeVOList)){
            return status;
        }
        try {
            safetyPatrolRecordService.batchModifyCurDeal(curNodeVOList);
        }catch (Exception e){
            LogUtils.error("批量更新流程信息异常:{}",e);
        }
        return status;
    }

    public PageVO<SafetyProcessPatrolHistoryDO> getPageList(SafetyProcessPatrolHistoryDTO param){
        //查询
        List<SafetyProcessPatrolHistoryDO> list = safetyPatrolRecordService.getHistoryPageList(param);
        long total = safetyPatrolRecordService.getHistoryPageListCount(param);
        return new PageVO<>(total, list);
    }

    public SafetyPersonCarOutRuleDTO getBaseInfo(SafetyPersonCarOutRuleDTO en){
        return safetyPatrolRecordService.getBaseInfo(en);
    }

    /**
     * bpm流程回调接口
     * @param bpmFlowId 流程ID
     * @param processData BPM流程表单数据
     */
    public void bpmCallBackProcess(String bpmFlowId, String processData) {
        LogUtils.info(String.format("治安巡查流程回写：bpm回调参数processId=%s;processData=%s", bpmFlowId, processData));
        if (com.alibaba.excel.util.StringUtils.isBlank(bpmFlowId) || com.alibaba.excel.util.StringUtils.isBlank(processData)) {
            LogUtils.info("治安巡查流程异常-流程ID或实体类ID为空");
            return;
        }
        try {
            JSONObject processDataObj = JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.getString("nodeFactId");
            SafetyProcessPatrolDO processDO = new SafetyProcessPatrolDO();
            processDO.setCurNodeId(nodeFactId);
            //根据bpm流程ID更新节点
            safetyPatrolRecordService.updateByExampleSelective(bpmFlowId,processDO);
        } catch (Exception e) {
            LogUtils.error("治安巡查流程流程回调-处理逻辑异常", e);
        }
    }
}
