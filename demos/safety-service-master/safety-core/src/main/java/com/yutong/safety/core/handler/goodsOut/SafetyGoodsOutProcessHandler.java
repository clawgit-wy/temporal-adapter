package com.yutong.safety.core.handler.goodsOut;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.common.vo.EipToDoVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.bpm.BpmCurNodeVO;
import com.yutong.safety.core.db.entity.bpm.BpmProcessParam;
import com.yutong.safety.core.db.entity.goodsOut.*;
import com.yutong.safety.core.db.entity.goodsOut.param.*;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutDetailGoodsVO;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutProcessDTO;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsProcessGooutVO;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsProcessNodeVO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutExceptionDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyValidateEnvironmentDOMapper;
import com.yutong.safety.core.dto.UrlMultipartFile;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutEIPDTO;
import com.yutong.safety.core.handler.bpm.BpmServiceHandler;
import com.yutong.safety.core.service.SafetyBpmProcessService;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.SafetyWaterFlowNoService;
import com.yutong.safety.core.service.eip.EipCommonService;
import com.yutong.safety.core.service.goodsOut.*;
import com.yutong.safety.core.service.pub.PubFileService;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import com.yutong.safety.core.service.wechat.PushWxMesService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyGoodsOutProcessHandler {

    private static final String GOODS_OUT_MODEL_ID = "goods_outsider";
    private static final String GOODS_OUT_PROCESS_FORM_ID = "safety_goods_out_process";
    //BPM流程放行节点ID
    private static final String GOODS_OUT_RELEASE_NODE_ID = "N7";

    private static final String RELEASE_ROLE_NAME = "出门证放行";

    private static String FROM_SYSTEM_NAME = "safety";
    private static String TO_SYSTEM_NAME = "dms";
    private static String REQUEST_TYPE = "0";
    private static String OUT_FACTORY_ID = "155";
    private static String OUT_TYPE_NAME = "车辆出厂";
    private static String EXAMINE_DEPT = "轻卡事业部";

    @Autowired
    private SafetyGoodsOutProcessService safetyGoodsOutProcessService;

    @Autowired
    private SafetyBpmProcessService safetyBpmProcessService;

    @Autowired
    private SafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyGoodsOutExceptionService safetyGoodsOutExceptionService;

    @Autowired
    private SafetyGoodsOutDetailReceiveService safetyGoodsOutDetailReceiveService;

    @Autowired
    private SafetyGoodsOutDetailGoodsService safetyGoodsOutDetailGoodsService;

    @Autowired
    private PushWxMesService pushWxMesService;

    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Autowired
    private SafetySpotCheckTaskService safetySpotCheckTaskService;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Autowired
    PubFileService pubFileService;

    @Autowired
    SafetyGoodsOutExceptionRecordService safetyGoodsOutExceptionRecordService;

    @Autowired
    BpmServiceHandler bpmServiceHandler;

    @Value("${dms.crz}")
    private String dmsCrzUrl;

    @Value("${cmz.goods.receiver}")
    private String RECEIVER;

    @Autowired
    SafetyGoodsOutExceptionDOMapper safetyGoodsOutExceptionDOMapper;
    @Autowired
    private SafetyValidateEnvironmentDOMapper safetyValidateEnvironmentDOMapper;


    @Value("${bpm.backCall.formUrl}")
    private String bpmBackCallFormUrl;
    @Autowired
    private EipCommonService eipCommonService;

    /**
     * 创建流程-保存信息
     * @param dto 入参
     * @return 返回值
     */
    public Object saveAndCreateProcessByUserName(GoodsOutProcessAddParam dto) throws Exception{
        String flowId = "";
        //流程提交人用户名，不能为空
        String creator = dto.getCreator();
        if(StringUtils.isBlank(creator)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"提交人用户名“creator”不能为空");
        }
        if(dto.getOutFactoryId()==null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"出厂厂区“OutFactoryId”不能为空");
        }

        //保存最近选择
        saveRecentSelect(dto);

        //根据质检人员工号获取质检人信息
        String qaEmpNo = dto.getQaExamineEmpNo();
        if(StringUtils.isNotBlank(qaEmpNo) && StringUtils.isBlank(dto.getQaExamineEmpEip())){
            if(qaEmpNo.startsWith("01")){
                qaEmpNo =  safetySpotCheckTaskService.employeeConvertWorkNo(qaEmpNo);
            }
            SafetyEmployeeMessageDO employeeMessageDO = safetyEmployeeMessageService.getByNo(qaEmpNo);
            if(ObjectUtils.isEmpty(employeeMessageDO)){
                throw new BizException(ResponseEnum.EXCEPTION.getCode(),"质保部审批人员工号"+qaEmpNo+"不存在");
            }
            dto.setQaExamineEmp(employeeMessageDO.getEmpName());
            if(StringUtils.isNotBlank(employeeMessageDO.getEmail())){
                dto.setQaExamineEmpEip(employeeMessageDO.getEmail().split("@")[0]);
            }
        }
        //根据出厂厂区获取对应门卫审批人
        String releasePerson = getPersonByRole(RELEASE_ROLE_NAME,dto.getOutFactoryId());
        dto.setReleaseEmpEip(releasePerson);

        String uuid = UUID.randomUUID().toString().replace("-","");
        dto.setId(uuid);
        dto.setStatus("20");
        dto.setFlowNo(getFlowNo());
        //是否超时 1-质保部审批超时；2-审批人超时；3-审核人上级审批超时；4-门卫放行超时
        dto.setIsTimeOut("1");

        //如果是eip传过来的，文件格式协议需要将http转为https
        httpToHttps(dto);

        int row = safetyGoodsOutProcessService.save(dto);
        if(row>0){
            //手动提交事务
            String returnFlowId = safetyBpmProcessService.createAndApproveProcessByUserName(dto.getSubject(),
                    GOODS_OUT_MODEL_ID, GOODS_OUT_PROCESS_FORM_ID, creator,uuid);
            if(StringUtils.isNotBlank(returnFlowId)){
                flowId = returnFlowId.split(",")[1];
            }
            dto.setFlowId(flowId);
            GoodsOutNodeUpdateParam nodeParam = new GoodsOutNodeUpdateParam(uuid,flowId,"","","");
            safetyGoodsOutProcessService.modifyFlowId(nodeParam);
        }

        //eip触发出门证
        if("4".equals(dto.getSystemFlag())){
            //审核通过后给出门人推送微信小程序消息
            String mobile = StringUtils.isBlank(dto.getGooutPhone()) ? dto.getOutsidePhone() : dto.getGooutPhone();
            LogUtils.info("eip生成流程发送消息-mobile="+mobile);
            if (StringUtils.isNotBlank(mobile)) {
                try {
                    String endTime = DateUtils.format(DateUtils.addDays(DateUtils.parse(dto.getCreateTime(),2), 1), 2);
                    String msgName = StringUtils.isBlank(dto.getGooutEmpName()) ? dto.getOutsideName() : dto.getGooutEmpName();
                    pushWxMesService.goodsPushWxMes(mobile, dto.getId(), "4", msgName, endTime,dto.getFlowNo(),dto.getFlowId());
                } catch (Exception e1) {
                    LogUtils.error("物资出厂-eip生成流程发送小程序消息异常", e1);
                }
            }
        }
        return uuid+","+flowId;
    }

    /**
     * 文件格式协议需要将http转为https
     * @param dto
     */
    public void httpToHttps(GoodsOutProcessAddParam dto){
        if(!"4".equals(dto.getSystemFlag()) || StringUtils.isEmpty(dto.getSceneImg())){
            return;
        }
        String sceneImg = dto.getSceneImg();
        String fileName = dto.getFileName();
        if(StringUtils.isBlank(sceneImg) || StringUtils.isBlank(fileName)){
            LogUtils.info("流程{}，接收到的现场图片信息不全sceneImg={}，fileName={}",dto.getSubject(),dto.getSceneImg(),dto.getFileName());
            return;
        }
        String httpsUrl = "";
        try {
            httpsUrl = convert(sceneImg,fileName.substring(fileName.lastIndexOf("=")+1));
        }catch (Exception e){
            try {
                httpsUrl = convert(sceneImg,fileName.substring(fileName.lastIndexOf("=")+1));
            } catch (Exception ex) {
                LogUtils.info("eip传的文件在转换过程中出错，信息{}",ex.getMessage());
            }
        }
        dto.setSceneImg(httpsUrl);
    }


    public String convert(String fileUrl,String fileName) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // 检查响应状态码
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LogUtils.info("Failed to download file: HTTP error code = {}", connection.getResponseCode());
            return null;
        }

        // 读取响应内容
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        // 关闭流
        inputStream.close();
        byteArrayOutputStream.close();

        // 将字节数组转换为MultipartFile
        byte[] bytes = byteArrayOutputStream.toByteArray();

        UrlMultipartFile mulFile = new UrlMultipartFile(fileName, "application/octet-stream", bytes);
        return pubFileService.picUpload(mulFile, "");
    }

    /**
     * 保存最近选择
     * @param dto
     */
    public void saveRecentSelect(GoodsOutProcessAddParam dto){
        if(!StringUtils.isBlank(dto.getSubmitNo()) && dto.getAuthId() != null && dto.getAuthId() != 0){
            safetyGoodsOutProcessService.saveRecentSelect(dto);
        }
    }

    /**
     * 更新流程
     * @param param 入参
     * @return 返回值
     */
    public BpmNodeReturnVO updateAndAuditByUserName(GoodsOutProcessUpdateParam param) throws Exception{
        //流程提交人用户名，不能为空
        String curPersonNo = param.getCurPersonLogin();
        if(StringUtils.isBlank(curPersonNo)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"当前流程处理人用户名不能为空");
        }
        this.modifyProcessInfo(param);
        BpmProcessParam processParam = safetyBpmProcessService.initProcessParam(param.getId(),param.getFlowId(),
                GOODS_OUT_MODEL_ID,GOODS_OUT_PROCESS_FORM_ID,curPersonNo,false,param.getAuditNote(),"");
        String status = safetyBpmProcessService.approveProcessByUserName(param.getId(), param.getFlowId(),
                GOODS_OUT_MODEL_ID, GOODS_OUT_PROCESS_FORM_ID,processParam.getProcessParam(), curPersonNo);

        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        assert status != null;
        returnVO.setStatus(status.substring(0,1));
        returnVO.setMessage(status);

        return returnVO;
    }


    /**
     * 更新流程
     * @param param 入参
     * @return 返回值
     */
    public BpmNodeReturnVO updateAndAuditForProcess(GoodsOutProcessUpdateDTO param) {
        //流程提交人用户名，不能为空
        String curPersonNo = param.getCurPersonLogin();
        if(StringUtils.isBlank(curPersonNo) || ObjectUtils.isEmpty(param.getProcessParam())
                || StringUtils.isBlank(param.getProcessParam().getFdParameterJson())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"当前流程处理人用户名/流程审批信息不能为空");
        }
        JSONObject flowParams = JSONObject.parseObject(param.getProcessParam().getFdParameterJson());
        GoodsOutProcessUpdateParam updatedParam = new GoodsOutProcessUpdateParam();
        BeanUtils.copyProperties(param,updatedParam);
        this.modifyProcessInfo(updatedParam);
        BpmProcessParam processParam = new BpmProcessParam();
        try{
            processParam = safetyBpmProcessService.initProcessParam(param.getId(),param.getFlowId(),
                    GOODS_OUT_MODEL_ID,GOODS_OUT_PROCESS_FORM_ID,curPersonNo,false,param.getAuditNote(),"");
            if(!ObjectUtils.isEmpty(processParam) && StringUtils.isNotBlank(processParam.getProcessParam())){
                JSONObject paramterJson = JSONObject.parseObject(processParam.getProcessParam());
                JSONObject paramterObj = paramterJson.getJSONObject("sysWfBusinessForm.fdParameterJson");
                String taskId = paramterObj.getString("taskId");
                flowParams.getJSONObject("sysWfBusinessForm.fdParameterJson").put("taskId",taskId);
            }
        }catch (Exception e){
            LogUtils.info("根据登录人{}，获取的processParam={},入参ParameterJson={}",
                    curPersonNo,processParam.getProcessParam(),param.getProcessParam().getFdParameterJson());
        }
        //处理流程
        String status = safetyBpmProcessService.approveProcessByUserName(param.getId(),
                param.getProcessParam().getFlowId(),
                GOODS_OUT_MODEL_ID, GOODS_OUT_PROCESS_FORM_ID,
                flowParams.toString(), param.getCurPersonLogin());

        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        assert status != null;
        returnVO.setStatus(status.substring(0,1));
        returnVO.setMessage(status);

        return returnVO;
    }

    public void modifyProcessInfo(GoodsOutProcessUpdateParam param){
        SafetyGoodsOutExceptionDO exceptionDO = new SafetyGoodsOutExceptionDO();
        switch (param.getNodeFlag()){
            case 1:
                //废料销售审批节点配置
                param.setIsTimeOut("2");
                break;
            case 3:
                //质保部审批节点配置
                param.setIsTimeOut("3");
                break;
            case 4:
                //审核人节点配置
                param.setIsTimeOut("5");
                //审批人审批后获取是否有历史异常记录
                exceptionDO.setIsException("1");
                exceptionDO.setProcessId(param.getId());
                List<SafetyGoodsOutExceptionDO> safetyGoodsOutExceptionDOS = safetyGoodsOutExceptionService.selectByExampleList(exceptionDO);
                if(!CollectionUtils.isEmpty(safetyGoodsOutExceptionDOS)){
                    param.setIsHistoryExce("1");
                }else{
                    param.setIsHistoryExce("0");
                }
                break;
            case 5:
                //审核人上级节点配置
                param.setIsTimeOut("4");
                break;
            case 6:
                guardRelease(param,exceptionDO);
                break;
            case 7:
                recipConfir(param);
                break;
            default:break;
        }
        //直接下一个工作日的日期
        param.setPublishTime(DateUtils.getDaysWithOutWeekend(1));

        //更新表单数据
        safetyGoodsOutProcessService.update(param);
    }

    public void recipConfir(GoodsOutProcessUpdateParam param){
        //接收人确认
        SafetyGoodsOutDetailReceiveDO receiveDO = new SafetyGoodsOutDetailReceiveDO();
        receiveDO.setProcessId(param.getId());
        receiveDO.setReceiveEmpEip(param.getCurPersonLogin());
        receiveDO.setReceiveStatus(param.getReceiveStatus());
        receiveDO.setRemark(param.getUnReceiveReason());
        if("1".equals(param.getReceiveStatus())){
            receiveDO.setIsException("0");
        }else{
            receiveDO.setIsException("1");

            //生成未接收/部分接收的异常信息
            SafetyGoodsOutDetailReceiveDO record = safetyGoodsOutDetailGoodsService.getRecord(receiveDO);
            SafetyGoodsOutExceptionRecordsDO exceptionRecordsDO = new SafetyGoodsOutExceptionRecordsDO();
            exceptionRecordsDO.setExitPassId(param.getId());
            exceptionRecordsDO.setDetailedDescription(param.getUnReceiveReason());
            exceptionRecordsDO.setExceptionType("未接收/部分接收");
            exceptionRecordsDO.setOperatorName(record.getReceiveEmp());
            exceptionRecordsDO.setOperatorEip(record.getReceiveEmpEip());
            exceptionRecordsDO.setOperatorCode(record.getReceiveEmpNo());
            safetyGoodsOutExceptionRecordService.save(exceptionRecordsDO);
        }
        receiveDO.setReceiveImg(param.getReceiveConfirmImg());
        receiveDO.setReceiveTime(DateUtils.format(new Date(),2));
        //保存接收人确认信息
        safetyGoodsOutDetailGoodsService.modifyReceive(receiveDO);

        //判断判定整个流程的接收状态
        Map<String, String> receiveMap = safetyGoodsOutDetailReceiveService.getProcessReceiveStatus(param.getId(), receiveDO);
        param.setReceiveStatus(receiveMap.get("receiveStatus"));
        param.setUnReceiveReason(receiveMap.get("unReceiveReason"));

        //接收节点审批通过后，如果所有人的接收状态都是全部接收，改超时未接收为超时接收
        if("1".equals(param.getReceiveStatus())){
            //查询已经在异常记录表中，并且异常类型为超时未接收的所有接收人
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("exitPassId", param.getId());
            paramMap.put("exceptionTypeNew", "超时接收");
            paramMap.put("exceptionTypeOld", "超时未接收");
            safetyGoodsOutExceptionRecordService.updateExceptionStatus(paramMap);
        }
    }

    public void guardRelease(GoodsOutProcessUpdateParam param,SafetyGoodsOutExceptionDO exceptionDO){
        //设置不超时
        param.setIsTimeOut("0");
        //门卫放行
        exceptionDO.setProcessId(param.getId());
        exceptionDO.setExceptionDesc(param.getExceptionDesc());
        exceptionDO.setExceptionRemark(param.getExceptionRemark());
        exceptionDO.setExceptionType("放行异常");
        exceptionDO.setPassUserEip(param.getCurPersonLogin());
        exceptionDO.setPassTime(new Date());
        param.setReleaseTime(DateUtils.format(new Date(),2)); //设置放行时间
        //门卫放行异常时，保存异常记录
        if("2".equals(param.getReleaseStatus()) || "1".equals(param.getIsHistoryExce())
                || StringUtils.isNotBlank(param.getExceptionDesc()) || "4".equals(param.getReleaseStatus())){
            exceptionDO.setIsException("1");
            if(StringUtils.isBlank(param.getReleaseStatus())){
                param.setReleaseStatus("2");
            }

            if("2".equals(param.getReleaseStatus())){
                SafetyGoodsOutExceptionRecordsDO exceptionRecordsDO = new SafetyGoodsOutExceptionRecordsDO();
                exceptionRecordsDO.setExitPassId(param.getId());
                exceptionRecordsDO.setDetailedDescription(param.getExceptionDesc());
                exceptionRecordsDO.setExceptionType("放行异常");
                exceptionRecordsDO.setOperatorName(param.getReleaseEmp());
                exceptionRecordsDO.setOperatorEip(param.getReleaseEmpEip());
                exceptionRecordsDO.setOperatorCode(param.getReleaseEmpNo());
                safetyGoodsOutExceptionRecordService.save(exceptionRecordsDO);
            }else if("4".equals(param.getReleaseStatus())){
                SafetyGoodsOutExceptionRecordsDO exceptionRecordsDO = new SafetyGoodsOutExceptionRecordsDO();
                exceptionRecordsDO.setExitPassId(param.getId());
                exceptionRecordsDO.setDetailedDescription(param.getExceptionDesc());
                exceptionRecordsDO.setExceptionType("抽查异常");
                exceptionRecordsDO.setOperatorName(param.getReleaseEmp());
                exceptionRecordsDO.setOperatorEip(param.getReleaseEmpEip());
                exceptionRecordsDO.setOperatorCode(param.getReleaseEmpNo());
                safetyGoodsOutExceptionRecordService.save(exceptionRecordsDO);
                exceptionDO.setExceptionType(param.getExceptionType());
                saveSpotCheckInfo(param);
            }
        }else {
            exceptionDO.setIsException("0");
            if(StringUtils.isBlank(param.getReleaseStatus())){
                param.setReleaseStatus("1");
            }
        }
        safetyGoodsOutExceptionService.insert(exceptionDO);

        /*下发DMS 安保系统取消DMS发运离厂时间节点 任务 96993
        //查询流程信息
        Map<String, Object> pMap = new HashMap<>();
        pMap.put("outFactoryId", OUT_FACTORY_ID);
        pMap.put("outTypeName", OUT_TYPE_NAME);
        pMap.put("examineDept", EXAMINE_DEPT);
        pMap.put("flowId", param.getId());

        List<SafetyGoodsOutDetailVehicleDO> carList = safetyGoodsOutProcessService.getCarList(pMap);
        if (!CollectionUtils.isEmpty(carList)) {
            sendMesToDMS(carList, param.getCurPersonLogin());
        }*/
    }

    /**
     * 保存业务抽查的数据
     * @param param
     */
    public void saveSpotCheckInfo(GoodsOutProcessUpdateParam param){
        SafetyValidateEnvironmentDO entity = new SafetyValidateEnvironmentDO();
        entity.setFlowNo(param.getId());
        entity.setCheckTime(DateUtils.format(new Date(),2));
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setBusinessType(param.getBusinessType());
        entity.setBusinessTypeCode(param.getBusinessType());
        entity.setCheckPerson(param.getReleaseEmp());
        entity.setCheckPersonNo(param.getReleaseEmpNo());
        entity.setCheckDept(param.getReleaseEmpDept());
        safetyValidateEnvironmentDOMapper.insertSelective(entity);
    }

    /**
     * 转派流程
     * @param param 入参
     * @return 返回值
     */
    public BpmNodeReturnVO updateTransferByUserName(GoodsOutProcessUpdateParam param) {
        //流程提交人用户名，不能为空
        String curPersonNo = param.getCurPersonLogin();
        if(StringUtils.isBlank(curPersonNo)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"当前流程处理人用户名不能为空！");
        }
        //更新表单数据
        //safetyGoodsOutProcessService.update(param);
        //处理拼装转办的表单数据
        BpmProcessParam processParam = safetyBpmProcessService.initProcessParam(param.getId(),param.getFlowId(),
                GOODS_OUT_MODEL_ID,GOODS_OUT_PROCESS_FORM_ID,param.getCurPersonLogin(),true,param.getAuditNote(),param.getTransferTo()
        );
        //提交
        String status = safetyBpmProcessService.approveProcessByUserName(param.getId(), param.getFlowId(),
                GOODS_OUT_MODEL_ID, GOODS_OUT_PROCESS_FORM_ID,processParam.getProcessParam(), curPersonNo);
        BpmNodeReturnVO returnVO = new BpmNodeReturnVO();
        returnVO.setStatus(status.substring(0,1));
        returnVO.setMessage(status);

        return returnVO;
    }

    /**
     * 驳回流程
     * @param param 入参
     * @return 返回值
     */
    public BpmNodeReturnVO rejectByUserName(GoodsOutProcessUpdateParam param) {
        //流程提交人用户名，不能为空
        String curPersonNo = param.getCurPersonLogin();
        if(StringUtils.isBlank(curPersonNo)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"当前流程处理人用户名不能为空！");
        }
        param.setStatus("11"); //状态设置为驳回
        //更新表单数据
        safetyGoodsOutProcessService.update(param);
        //处理拼装转办的表单数据
        BpmProcessParam processParam = safetyBpmProcessService.initProcessRejectParam(param.getId(),param.getFlowId(),
                GOODS_OUT_MODEL_ID,GOODS_OUT_PROCESS_FORM_ID,param.getCurNodeHandler(),false,
                param.getAuditNote(),param.getToNodeId());
        //提交
        String status = safetyBpmProcessService.approveProcessByUserName(param.getId(), param.getFlowId(),
                GOODS_OUT_MODEL_ID, GOODS_OUT_PROCESS_FORM_ID,processParam.getProcessParam(), curPersonNo);
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
        String nowNum = safetyWaterFlowNoService.getNowByType(GOODS_OUT_MODEL_ID);
        return Joiner.on("-").join("CMZ",date+nowNum);
    }


    //获取当前处理人待处理流程
    public PageVO<GoodsOutProcessDTO> selectReleaseList(GoodsOutAppListParam param){
        String searchType = param.getSearchType();
        GoodsOutProcessListParam processListParam = new GoodsOutProcessListParam();
        BeanUtils.copyProperties(param,processListParam);

        processListParam.setBegin(1); //设置分页标识
        SafetyGoodsOutExceptionDO exceptionDO = new SafetyGoodsOutExceptionDO();
        List<String> idList = new ArrayList<>();
        idList.add("0");
        switch (searchType){
            case "1":
                processListParam.setCurNodeId(GOODS_OUT_RELEASE_NODE_ID);
                processListParam.setReleaseEmpEip(param.getCurPersonLogin());
                processListParam.setUnNodeFlag("6");
                break;
            case "2":
                //可查看用户授权厂区的所有出厂厂区已放行的出门证
                processListParam.setReleaseStatus("1"); //已放行
                SafetySysUserDO userVo = safetySysUserService.getSysUserByUserName(param.getCurPersonLogin());
                if(userVo == null || StringUtils.isBlank(userVo.getPowerFactoryId())){
                    processListParam.setOutFactoryId("-1");
                }else{
                    processListParam.setOutFactoryId(userVo.getPowerFactoryId());
                }
                processListParam.setOrderByClause(" release_time desc ");
                break;
            case "3":
                exceptionDO.setPassUserEip(param.getCurPersonLogin());
                exceptionDO.setIsException("1");
                exceptionDO.setExceptionType("放行异常");
                List<SafetyGoodsOutExceptionDO> exceptionDOS1 = safetyGoodsOutExceptionService.selectByExampleList(exceptionDO);
                if(!CollectionUtils.isEmpty(exceptionDOS1)){
                    idList = exceptionDOS1.stream().map(SafetyGoodsOutExceptionDO::getProcessId).collect(Collectors.toList());
                }
                processListParam.setIdList(idList);
                break;
            default:
                break;
        }

        List<GoodsOutProcessDTO> list = safetyGoodsOutProcessService.getList(processListParam);
        long total = safetyGoodsOutProcessService.getListCount(processListParam);
        LogUtils.info("SafetyGoodsOutProcessHandler[processListParam]:"+processListParam+";[total]:"+total);
        return new PageVO<>(total,list);
    }

    /**
     * 获取详情
     * @param id 入参ID
     * @return 返回值
     */
    public GoodsOutProcessDTO getDetailById(String id) {
        return safetyGoodsOutProcessService.getDetailById(id);
    }

    /**
     * 获取列表
     * @param processDTO 查询参数
     * @return 返回值
     */
    public PageVO<GoodsOutProcessDTO> getPageList(GoodsOutProcessListParam processDTO,String token) {
        //处理截止日期逻辑
        if(!StringUtils.isBlank(processDTO.getCreateEnd())){
            processDTO.setCreateEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(processDTO.getCreateEnd(),1),1),1));
        }
        processDTO.setBegin(1); //设置分页标识
        //不是超级管理员时，过滤授权厂区
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        String userName = TokenUtils.getUserName(token);
        boolean filterFlag = safetySysUserService.checkRoleByUserName(userName,"超级管理员");
        if(!filterFlag){
            String factoryId = safetySysUserService.getPowerFactory(workNo);
            processDTO.setOutFactoryIds(factoryId);
        }
        //查询
        List<GoodsOutProcessDTO> list = safetyGoodsOutProcessService.getList(processDTO);
        long total = safetyGoodsOutProcessService.getListCount(processDTO);
        Map<String,List<SafetyGoodsOutDetailReceiveDO>> receiveMap = new HashMap<>();
        Map<String,List<SafetyGoodsOutDetailGoodsDO>> goodsMap = new HashMap<>();
        Map<String,List<BpmCurNodeVO>> curNodeMap = new HashMap<>();
        //分页后获取列表的明细表
        if(!CollectionUtils.isEmpty(list)){
            //从bpm获取当前节点处理人
            String flowIdStr = list.stream().map(GoodsOutProcessDTO::getFlowId).collect(Collectors.joining(","));
            List<BpmCurNodeVO> curNodeVOList;
            try{
                curNodeVOList = safetyBpmProcessService.getFlowInfoForSelf(flowIdStr);
                if(!CollectionUtils.isEmpty(curNodeVOList)){
                    curNodeMap = curNodeVOList.stream().collect(Collectors.groupingBy(BpmCurNodeVO::getModelId));
                }
            }catch (Exception e){
                LogUtils.error("批量获取当前流程信息异常:",e);
            }
            List<String> processIdList = list.stream().map(GoodsOutProcessDTO::getId).collect(Collectors.toList());
            GoodsOutDetailReceiveListParam receiveListParam = new GoodsOutDetailReceiveListParam(processIdList);
            List<SafetyGoodsOutDetailReceiveDO>  receiveDOList = safetyGoodsOutDetailReceiveService.selectList(receiveListParam);
            if(!CollectionUtils.isEmpty(receiveDOList)){
                receiveMap = receiveDOList.stream().collect(Collectors.groupingBy(SafetyGoodsOutDetailReceiveDO::getProcessId));
            }
            GoodsOutDetailGoodsListParam goodsListParam = new GoodsOutDetailGoodsListParam(processIdList);
            List<SafetyGoodsOutDetailGoodsDO>  goodsDOList = safetyGoodsOutDetailGoodsService.selectList(goodsListParam);
            if(!CollectionUtils.isEmpty(receiveDOList)){
                goodsMap = goodsDOList.stream().collect(Collectors.groupingBy(SafetyGoodsOutDetailGoodsDO::getProcessId));
            }

        } else {
            receiveMap = new HashMap<>();
        }
        Map<String, List<SafetyGoodsOutDetailGoodsDO>> finalGoodsMap = goodsMap;
        Map<String, List<SafetyGoodsOutDetailReceiveDO>> finalReceiveMap = receiveMap;
        Map<String, List<BpmCurNodeVO>> finalCurNodeMap = curNodeMap;
        list.forEach(element->{
            String processId = element.getId();
            element.setReceiveList(finalReceiveMap.get(processId));
            element.setGoodsList(finalGoodsMap.get(processId));
            List<BpmCurNodeVO> curList = finalCurNodeMap.get(element.getFlowId());
            if(!CollectionUtils.isEmpty(curList)){
                BpmCurNodeVO curNodeVO = curList.get(0);
                if(!ObjectUtils.isEmpty(curNodeVO)){
                    element.setCurNodeId(curNodeVO.getNodeIds());
                    element.setCurNodeName(curNodeVO.getNodeNames());
                    element.setCurNodeHandler(curNodeVO.getFdCurrLoginNames());
                    element.setCurHandlerName(curNodeVO.getFdCurrNames());
                }
            }
        });
        return new PageVO<>(total, list);
    }

    /**
     * 导出附件
     * @param processDTO 检索入参
     * @param token token
     * @return 附件
     * @throws Exception 异常
     */
    public HSSFWorkbook exportData(GoodsOutProcessListParam processDTO, String token) throws Exception {
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        //不是超级管理员时，过滤授权厂区
        boolean filterFlag = safetySysUserService.checkRole(token,"超级管理员");
        if(!filterFlag){
            String factoryId = safetySysUserService.getPowerFactory(workNo);
            processDTO.setOutFactoryIds(factoryId);
        }
        if(!StringUtils.isBlank(processDTO.getCreateEnd())){
            processDTO.setCreateEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(processDTO.getCreateEnd(),1),1),1));
        }
        List<GoodsOutProcessDTO> list = safetyGoodsOutProcessService.getList(processDTO);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        int pageToal = list.size()/1000+(list.size()%1000>0?1:0);
        for(int page=0;page<pageToal;page++){

            List<GoodsOutProcessDTO> subList = list.subList(page*1000, Math.min((page + 1) * 1000, list.size()));
            Map<String,List<SafetyGoodsOutDetailReceiveDO>> receiveMap = new HashMap<>();
            //分页后获取列表的明细表
            if(!CollectionUtils.isEmpty(subList)){
                List<String> processIdList = subList.stream().map(GoodsOutProcessDTO::getId).collect(Collectors.toList());
                GoodsOutDetailReceiveListParam receiveListParam = new GoodsOutDetailReceiveListParam(processIdList);
                List<SafetyGoodsOutDetailReceiveDO>  receiveDOList = safetyGoodsOutDetailReceiveService.selectList(receiveListParam);
                if(!CollectionUtils.isEmpty(receiveDOList)){
                    receiveMap = receiveDOList.stream().collect(Collectors.groupingBy(SafetyGoodsOutDetailReceiveDO::getProcessId));
                }
            } else {
                receiveMap = new HashMap<>();
            }
            Map<String,List<BpmCurNodeVO>> curNodeMap = new HashMap<>();

            //从bpm获取当前节点处理人
            String flowIdStr = subList.stream().map(GoodsOutProcessDTO::getFlowId).collect(Collectors.joining(","));
            List<BpmCurNodeVO> curNodeVOList;
            try{
                curNodeVOList = safetyBpmProcessService.getFlowInfoForSelf(flowIdStr);
                if(!CollectionUtils.isEmpty(curNodeVOList)){
                    curNodeMap = curNodeVOList.stream().collect(Collectors.groupingBy(BpmCurNodeVO::getModelId));
                }
            }catch (Exception e){
                LogUtils.error("批量获取当前流程信息异常:",e);
            }

            Map<String, List<SafetyGoodsOutDetailReceiveDO>> finalReceiveMap = receiveMap;
            Map<String, List<BpmCurNodeVO>> finalCurNodeMap = curNodeMap;
            subList.forEach(element->{
                String processId = element.getId();
                element.setReceiveList(finalReceiveMap.get(processId));
                List<BpmCurNodeVO> curList = finalCurNodeMap.get(element.getFlowId());
                if(!CollectionUtils.isEmpty(curList)){
                    BpmCurNodeVO curNodeVO = curList.get(0);
                    if(!ObjectUtils.isEmpty(curNodeVO)){
                        element.setCurNodeId(curNodeVO.getNodeIds());
                        element.setCurNodeName(curNodeVO.getNodeNames());
                        element.setCurNodeHandler(curNodeVO.getFdCurrLoginNames());
                        element.setCurHandlerName(curNodeVO.getFdCurrNames());
                    }
                }
            });
        }

        String[] title = new String[]{"流程编码","主题","提交时间","状态","申请人姓名","申请人员工号/手机号","申请人部门","出厂厂区","出门类型","物资类别","原因类别",
                "审核人","审核人员工号","审核人部门","出门人类型","出门人姓名","出门人手机号/员工号","出门人部门","是否车辆运输","车牌号","是否需要接收人确认","接收人信息",
                "现场照片","接收照片","物资是否接收","接收异常原因","过磅单/证明材料","原因"};
        String[][] values = new String[list.size()][];
        List<String> processIdList = new ArrayList<>();
        GoodsOutProcessDTO goodsOutProcessDTO;
        for(int i=0; i<list.size(); i++) {
            goodsOutProcessDTO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = goodsOutProcessDTO.getFlowNo();
            values[i][1] = goodsOutProcessDTO.getSubject();
            values[i][2] = DateUtils.format(goodsOutProcessDTO.getCreateTime(),2);
            values[i][3] = goodsOutProcessDTO.getStatus();
            values[i][4] = goodsOutProcessDTO.getSubmitName();
            values[i][5] = StringUtils.isBlank(goodsOutProcessDTO.getSubmitNo())?goodsOutProcessDTO.getSubmitPhone():goodsOutProcessDTO.getSubmitNo();
            values[i][6] = goodsOutProcessDTO.getSubmitDept();
            values[i][7] = goodsOutProcessDTO.getOutFactoryName();
            values[i][8] = goodsOutProcessDTO.getOutTypeName();
            values[i][9] = goodsOutProcessDTO.getGoodsType();
            values[i][10] = goodsOutProcessDTO.getReasonType();
            values[i][11] = goodsOutProcessDTO.getExamineEmpName();
            values[i][12] = goodsOutProcessDTO.getExamineEmpNo();
            values[i][13] = goodsOutProcessDTO.getExamineDept();
            values[i][14] = "1".equals(goodsOutProcessDTO.getOutPersonType())?"公司内部人员":"外部人员";
            values[i][15] = goodsOutProcessDTO.getGooutEmpName();
            values[i][16] = StringUtils.isBlank(goodsOutProcessDTO.getGooutEmpNo())?goodsOutProcessDTO.getGooutPhone():goodsOutProcessDTO.getGooutEmpNo();
            values[i][17] = goodsOutProcessDTO.getGooutEmpDept();
            values[i][18] = goodsOutProcessDTO.getIsVehicle();
            values[i][19] = goodsOutProcessDTO.getVehicleNo();
            values[i][20] = goodsOutProcessDTO.getIsSetAccept();
            String receiveStr = CollectionUtils.isEmpty(goodsOutProcessDTO.getReceiveList())?"":formatReceiveList(goodsOutProcessDTO.getReceiveList());
            values[i][21] = receiveStr;
            values[i][22] = goodsOutProcessDTO.getSceneImg();
            values[i][23] = goodsOutProcessDTO.getReceiveStatus();
            values[i][24] = goodsOutProcessDTO.getUnReceiveReason();
            values[i][25] = StringUtils.isBlank(goodsOutProcessDTO.getLeaveData())?goodsOutProcessDTO.getWeightDataUrl():goodsOutProcessDTO.getLeaveData();
            values[i][26] = goodsOutProcessDTO.getRemark();
            processIdList.add(goodsOutProcessDTO.getId());
        }
        String sheetName = "出门证流程明细";
        HSSFWorkbook hssfWorkbook = ExcelUtils.getHSSFWorkbook(sheetName, title, values);
        HSSFWorkbook sheets = exportDetailData(hssfWorkbook, processIdList);
        return sheets;
    }

    public HSSFWorkbook exportDetailData(HSSFWorkbook hssfWorkbook,List<String> processIdList) {
        GoodsOutDetailListParam en = new GoodsOutDetailListParam();
        en.setProcessIdList(processIdList);
        List<GoodsOutDetailGoodsVO> list = safetyGoodsOutDetailGoodsService.selectDetailList(en);

        if(CollectionUtils.isEmpty(list)){
            return hssfWorkbook;
        }

        String[] title = new String[]{"流程编号","主题","物资编号","物资名称","单位","数量","车工号","VIN号","提交时间","出厂人","出厂厂区",
                "审核人","状态","出厂类型","备注"};
        String[][] values = new String[list.size()][];
        GoodsOutDetailGoodsVO goodsOutProcessDTO;
        for(int i=0; i<list.size(); i++) {
            goodsOutProcessDTO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = goodsOutProcessDTO.getFlowNo();
            values[i][1] = goodsOutProcessDTO.getSubject();
            values[i][2] = goodsOutProcessDTO.getGoodsCode();
            values[i][3] = goodsOutProcessDTO.getGoodsName();
            values[i][4] = goodsOutProcessDTO.getGoodsUnit();
            values[i][5] = goodsOutProcessDTO.getGoodsNum()+"";
            values[i][6] = goodsOutProcessDTO.getVehicleNo();
            values[i][7] = goodsOutProcessDTO.getVinNo();
            values[i][8] = goodsOutProcessDTO.getCreateTimeStr();
            values[i][9] = goodsOutProcessDTO.getGooutEmpName();
            values[i][10] = goodsOutProcessDTO.getOutFactoryName();
            values[i][11] = goodsOutProcessDTO.getExamineEmpName();
            values[i][12] = goodsOutProcessDTO.getStatus();
            values[i][13] = goodsOutProcessDTO.getOutTypeName();
            values[i][14] = goodsOutProcessDTO.getRemark();
        }
        String sheetName = "出门证物资明细";

        return ExcelUtils.getHSSFWorkbookWithSheets(hssfWorkbook, sheetName, title, values);
    }

    private String formatReceiveList(List<SafetyGoodsOutDetailReceiveDO> receiveDOList){
        StringBuilder builder = new StringBuilder();
        for(SafetyGoodsOutDetailReceiveDO detailReceiveDO:receiveDOList){
            if(!StringUtils.isBlank(builder.toString())){
                builder.append(",");
            }
            builder.append(detailReceiveDO.getTargetFactory()).append("-").append(detailReceiveDO.getReceiveEmp())
                    .append("-").append(detailReceiveDO.getReceiveEmpNo());
        }
        return builder.toString();
    }

    //更新流程当前节点处理人信息
    public int modifyCurDealHandler(GoodsOutNodeUpdateParam param){
        //获取审批后当前节点审批人
        Object retObj = safetyBpmProcessService.getCurrentNodesInfo(param.getId(),param.getFlowId(),GOODS_OUT_PROCESS_FORM_ID);
        JSONArray jsonArray = JSONArray.parseArray(String.valueOf(retObj));
        StringBuilder curHandler = new StringBuilder();
        StringBuilder curNodeId = new StringBuilder();
        StringBuilder curNodeName = new StringBuilder();
        if(!jsonArray.isEmpty()){
            for(int i=0;i<jsonArray.size();i++){
                JSONObject node = jsonArray.getJSONObject(i);
                if(!curNodeId.toString().isEmpty()){
                    curNodeId.append(".");
                    curNodeName.append(",");
                }
                curNodeId.append(node.get("nodeId"));
                curNodeName.append(node.get("nodeName"));
                JSONArray items = node.getJSONArray("workitems");
                if(items != null && !items.isEmpty()){
                    for (int j=0;j<items.size();j++) {
                        JSONObject item = items.getJSONObject(j);
                        if (!curHandler.toString().isEmpty()) {
                            curHandler.append(",");
                        }
                        curHandler.append(item.get("expecter"));
                    }
                }
            }
        }
        param.setCurDealHandler(curHandler.toString());
        param.setCurNodeId(curNodeId.toString());
        param.setCurNodeName(curNodeName.toString());

        return safetyGoodsOutProcessService.modifyCurDealHandler(param);
    }

    /**
     * 出门证流程回调处理逻辑
     * @param processId 流程ID
     * @param docStatus 流程状态
     * @param processData 流程数据  格式 {"processId":"1926fe35c53c3ba8a8fa1f34218b907d","nodeFactId":"N70","docStatus":"20"}
     */
    public void bpmCallBackGoodsOut(String processId, String docStatus, String processData){
        LogUtils.info(String.format("bpm回调参数processId=%s;docStatus=%s;processData=%s", processId, docStatus, processData));
        if(StringUtils.isBlank(processId) || StringUtils.isBlank(processData)){
            LogUtils.info("出门证更新节点信息异常-流程ID或实体类ID为空");
            return;
        }
        GoodsOutProcessListParam listParam = new GoodsOutProcessListParam();
        listParam.setFlowId(processId);
        try {
            List<GoodsOutProcessDTO> processList = safetyGoodsOutProcessService.getList(listParam);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("出门证更新节点信息异常-未找到对应的流程信息");
                return;
            }
            JSONObject processDataObj = JSONObject.parseObject(processData);
            String nodeFactId = processDataObj.getString("nodeFactId");
            GoodsOutProcessDTO processDTO = processList.get(0);
            GoodsOutNodeUpdateParam nodeParam = new GoodsOutNodeUpdateParam();
            switch (nodeFactId) {
                case "N74":
                    nodeParam.setCurNodeId("N74");
                    nodeParam.setCurNodeName("审核人确认物资接收信息");
                    nodeParam.setId(processDTO.getId());
                    safetyGoodsOutProcessService.modifyCurDealHandler(nodeParam);
                    try{
                        //消除接收人的EIP代办
                        GoodsOutDetailReceiveListParam receiveListParam = new GoodsOutDetailReceiveListParam();
                        receiveListParam.setProcessId(processDTO.getId());
                        List<SafetyGoodsOutDetailReceiveDO> receiveList =  safetyGoodsOutDetailReceiveService.selectList(receiveListParam);
                        for (SafetyGoodsOutDetailReceiveDO e:receiveList) {
                            String modelId = "SAFETY-safety_goods_out_detail_receive-" + e.getId();
                            EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                            eipCommonService.setEipToDoDone(eipToDoVO);
                        }
                    }catch (Exception e){
                        LogUtils.error("删除接收人的EIP代办异常",e);
                    }
                    break;
                case "N75":
                    nodeParam.setCurNodeId("N75");
                    nodeParam.setCurNodeName("接收人接收确认");
                    nodeParam.setId(processDTO.getId());
                    safetyGoodsOutProcessService.modifyCurDealHandler(nodeParam);

                    //查询该流程是否存在接收人，如果存在就触发EIP代办
                    GoodsOutDetailReceiveListParam param = new GoodsOutDetailReceiveListParam();
                    param.setProcessId(processDTO.getId());
                    List<SafetyGoodsOutDetailReceiveDO> receiveList = safetyGoodsOutDetailReceiveService.selectList(param);
                    if(!CollectionUtils.isEmpty(receiveList)){
                        createEIPTodo(receiveList, processDTO);
                    }

                    break;
                case "N7":
                    //审核通过后给出门人推送微信小程序消息
                    String mobile = StringUtils.isBlank(processDTO.getGooutPhone()) ? processDTO.getOutsidePhone() : processDTO.getGooutPhone();
                    LogUtils.info("回调发送消息-mobile="+mobile);
                    if (StringUtils.isNotBlank(mobile)) {
                        try {
                            String endTime = DateUtils.format(DateUtils.addDays(processDTO.getCreateTime(), 1), 2);
                            String msgName = StringUtils.isBlank(processDTO.getGooutEmpName()) ? processDTO.getOutsideName() : processDTO.getGooutEmpName();
                            pushWxMesService.goodsPushWxMes(mobile, processDTO.getId(), "4", msgName, endTime,processDTO.getFlowNo(),processId);
                        } catch (Exception e1) {
                            LogUtils.error("物资出厂-发送小程序消息异常", e1);
                        }
                    }

                    nodeParam.setCurNodeId("N7");
                    nodeParam.setCurNodeName("门卫放行");
                    nodeParam.setCurDealHandler(processDTO.getReceiveEipStr());
                    nodeParam.setId(processDTO.getId());
                    nodeParam.setReleaseStatus("0");
                    safetyGoodsOutProcessService.modifyCurDealHandler(nodeParam);

                    break;
                case "N3":
                    nodeParam.setCurNodeId("N3");
                    nodeParam.setCurNodeName("结束节点");

                    //判断是否是流程超时
                    nodeParam.setFdStatus("30");
                    isTimeOut(processDTO,nodeParam);

                    nodeParam.setCurDealHandler("");
                    nodeParam.setNodeFlag("-1");
                    nodeParam.setId(processDTO.getId());
                    safetyGoodsOutProcessService.modifyCurDealHandler(nodeParam);
                    //todo  结束时超时标记<>0,需要保存超时异常记录
                    if(!"0".equals(processDTO.getIsTimeOut())){
                        //todo  结束时超时标记<>0,需要保存超时异常记录
                    }
                    break;
                case "N2":
                    nodeParam.setCurNodeId(nodeFactId);
                    nodeParam.setCurNodeName("起草节点");
                    nodeParam.setFdStatus("11");
                    nodeParam.setCurDealHandler("");
                    nodeParam.setNodeFlag("-1");
                    nodeParam.setId(processDTO.getId());
                    safetyGoodsOutProcessService.modifyCurDealHandler(nodeParam);
                default:
                    break;
            }
        }catch (Exception e){
            LogUtils.error("物资出厂流程回调-处理逻辑异常", e);
        }

    }

    /**
     * 创建EIP待办
     * @param receiveList
     * @param processDTO
     */
    public void createEIPTodo(List<SafetyGoodsOutDetailReceiveDO> receiveList,GoodsOutProcessDTO processDTO){
        for(SafetyGoodsOutDetailReceiveDO receiveDO:receiveList){
            String operatePage = "物资出门证-接收人确认";
            try {
                String taragets = "{\"PersonNo\":\"" + receiveDO.getReceiveEmpNo() + "\"}";
                String docCreator = "{\"PersonNo\":\"" + processDTO.getSubmitNo() + "\"}";
                String subject = "请对出门证-" + processDTO.getSubject() + "的出厂物资进行接收";
                String modelId = "SAFETY-safety_goods_out_detail_receive-" + receiveDO.getId();
                String modelName = "安保平台" + operatePage;
                String link = "<![CDATA[" + bpmBackCallFormUrl + "?BPM_NotifyTodoId=a&sysId=SAFETY&modelId=" + GOODS_OUT_MODEL_ID + "&templateFormId=" + GOODS_OUT_PROCESS_FORM_ID + "&formInstanceId=" + processDTO.getId() + "&processId=" + processDTO.getFlowId() + "&receiveId="+receiveDO.getId()+"]]>";
                EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, null, taragets, null, docCreator, null);
                eipToDoVO.setOthers("{\"isOpenInMobile\":\"1\"}");
                eipCommonService.createEipToDo(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【出门证N75节点发送代办失败】{},失败原因{}",processDTO.getId(),e.getMessage());
            }
        }
    }

    /**
     * EIP待办转办
     * @param enList
     */
    public void changeEIPTodoSelf(List<SafetyGoodsOutEIPDTO> enList){
        if(CollectionUtils.isEmpty(enList)){return;}

        //根据流程ID查询原接收人信息
        String processId = enList.get(0).getProcessId();
        GoodsOutDetailReceiveListParam param = new GoodsOutDetailReceiveListParam();
        param.setProcessId(processId);
        List<SafetyGoodsOutDetailReceiveDO> list = safetyGoodsOutDetailReceiveService.selectList(param);
        if(CollectionUtils.isEmpty(list)){
            LogUtils.info("方法：changeEIPTodoSelf，processId={}不存在对应的物资接收人信息",processId);
            return;
        }
        Map<Integer, SafetyGoodsOutDetailReceiveDO> oldList = list.stream().collect(Collectors.toMap(SafetyGoodsOutDetailReceiveDO::getId,
                safetyGoodsOutDetailReceiveDO -> safetyGoodsOutDetailReceiveDO,
                (oldValue, newValue) -> newValue));

        String operatePage = "物资出门证-接收人确认";
        List<SafetyGoodsOutDetailReceiveDO> changeList = new ArrayList<>();
        for(SafetyGoodsOutEIPDTO en:enList){
            try {
                if(StringUtils.isEmpty(en.getReceiveId())){continue;}
                if(StringUtils.isEmpty(en.getReceiveId())){
                    LogUtils.info("替换待办过程中，流程ID={}，转换人={}，接收人={}，为空，不进行转换",processId,en.getSubmitNo(),en.getToEmpNo());
                    continue;
                }
                SafetyGoodsOutDetailReceiveDO oldValue = oldList.get(Integer.parseInt(en.getReceiveId()));
                if(ObjectUtils.isEmpty(oldValue)){
                    LogUtils.info("替换待办过程中，流程ID={}，接收人信息ID={}，没查找到对应数据，不转换",processId,en.getReceiveId());
                    continue;
                }
                //只针对未接收的
                if(!StringUtils.isEmpty(oldValue.getReceiveStatus())){
                    continue;
                }

                //接收人员工号一致就不转办
                if(oldValue.getReceiveEmpNo().equals(en.getToEmpNo())){continue;}

                String taragets = "{\"PersonNo\":\"" + en.getToEmpNo() + "\"}";
                String docCreator = "{\"PersonNo\":\"" + en.getSubmitNo() + "\"}";
                String subject = "请对出门证-" + en.getSubject() + "的出厂物资进行接收";
                String modelId = "SAFETY-safety_goods_out_detail_receive-" + en.getReceiveId();
                String modelName = "安保平台" + operatePage;
                String link = "<![CDATA[" + bpmBackCallFormUrl + "?BPM_NotifyTodoId=a&sysId=SAFETY&modelId=" + GOODS_OUT_MODEL_ID + "&templateFormId=" + GOODS_OUT_PROCESS_FORM_ID + "&formInstanceId=" + processId + "&processId=" + en.getFlowId() + "&receiveId="+en.getReceiveId()+"]]>";

                //删除自己的EIP待办
                EipToDoVO eipToDoVOMyself = new EipToDoVO(modelId, "", "", "", "", "", "", "", "");
                eipCommonService.setEipToDoDone(eipToDoVOMyself);

                //给他人创建待办
                EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, null, taragets, null, docCreator, null);
                eipToDoVO.setOthers("{\"isOpenInMobile\":\"1\"}");
                eipCommonService.createEipToDo(eipToDoVO);

                //转办完成后记录转办过的数据
                oldValue.setReceiveEmpNo(en.getToEmpNo());
                oldValue.setReceiveEmpEip(en.getToEmpEip());
                oldValue.setReceiveEmp(en.getToEmpName());
                oldValue.setReceiveEmpDept(en.getToEmpDept());
                oldValue.setReceivePhone(en.getToEmpPhone());
                changeList.add(oldValue);
            } catch (Exception e) {
                LogUtils.error("{\"code\":\"9999\",msg:【出门证N75节点转办EIP待办失败】流程ID={},原EIP待办人={}，转后EIP待办人={}，失败原因{},", en.getProcessId(), en.getFromEIPEmpNo(), en.getToEmpNo(), e.getMessage());
            }
        }

        //更新转办后的数据
        if(!CollectionUtils.isEmpty(changeList)){
            safetyGoodsOutDetailReceiveService.updateReceiveInfo(changeList);
        }
    }

    public void isTimeOut(GoodsOutProcessDTO processDTO,GoodsOutNodeUpdateParam nodeParam){
        String isTimeOut = processDTO.getIsTimeOut();
        Integer nodeFlag = processDTO.getNodeFlag();

        if(nodeFlag < 4 && nodeFlag >= 0 && !"5".equals(isTimeOut)){
            nodeParam.setFdStatus("60");
        }else if(nodeFlag == 4 && "5".equals(isTimeOut)){
            nodeParam.setFdStatus("60");
        }else if(nodeFlag == 5 && "4".equals(isTimeOut)){
            nodeParam.setFdStatus("60");
        }

    }

    /**
     * 获取治安管理员角色
     * @param roleName  周期任务ID
     * @param factoryId 事件工厂
     * @return 返回值
     */
    private String getPersonByRole(String roleName,Integer factoryId){
        String personLoginNameStr = "";
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName(roleName);
        param.setPowerFactoryId(String.valueOf(factoryId));
        List<SafetySysUserDO> sysUserDOList = safetySysUserService.getList(param);
        if(!CollectionUtils.isEmpty(sysUserDOList)){
            personLoginNameStr = sysUserDOList.stream().map(SafetySysUserDO::getUserName).collect(Collectors.joining(","));
        }
        return personLoginNameStr;
    }

    /**
     * 解析附件excel
     * @param file excel文件
     * @return 数据集合
     */
    public List<Map<String,String>> importDetailExcel(MultipartFile file){
        List<Map<String,String>> dataList = new ArrayList<>();
        Workbook workbook = ExcelUtils.getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        Row row1 = sheet.getRow(firstRowNum);
        //获取最大列
        int column = row1.getLastCellNum();
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            Map<String,String> curMap = new HashMap<>();
            for(int j=0;j<column;j++){
                curMap.put(j+"",ExcelUtils.getValue(row.getCell(j)));
            }
            dataList.add(curMap);
        }
        return dataList;
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
            safetyGoodsOutProcessService.batchModifyCurDeal(curNodeVOList);
        }catch (Exception e){
            LogUtils.error("批量更新流程信息异常:{}",e);
        }
        return status;
    }

    /**
     * 获取流程出门人历史数据
     * @param listParam-检索清单
     * @return 返回值
     */
    public PageVO<GoodsProcessGooutVO> getProcessGooutList(GoodsProcessGooutListParam listParam){
        listParam.setBegin(1);
        List<GoodsProcessGooutVO> list = safetyGoodsOutProcessService.getProcessGooutList(listParam);
        long rows =safetyGoodsOutProcessService.getProcessGooutCount(listParam);
        return new PageVO<>(rows,list);
    }

    /**
     * BPM流程超时废弃流程回调
     * @param processId
     */
    public void bpmCallBackGoodsOutTimeOut(String processId){
        LogUtils.info("bpm超时回调参数processId={}", processId);
        if(StringUtils.isBlank(processId)){
            LogUtils.info("出门证超时更新节点信息异常-流程ID为空");
            return;
        }
        GoodsOutProcessListParam listParam = new GoodsOutProcessListParam();
        listParam.setFlowId(processId);
        try {
            List<GoodsOutProcessDTO> processList = safetyGoodsOutProcessService.getList(listParam);
            if (CollectionUtils.isEmpty(processList)) {
                LogUtils.info("出门证超时更新节点信息异常-未找到对应的流程信息");
                return;
            }
            processList.forEach(en -> {
                //超时废弃
                en.setStatus("60");
                //超时
                en.setIsTimeOut("100");
            });

            safetyGoodsOutProcessService.batchUpdate(processList);
        }catch (Exception e){
            LogUtils.info("出门证流程超时结束异常:{}",e.getMessage());
        }
    }

    /**
     * 任务-批量同步更新数据
     * @param flowIdStr-流程ID字符串
     * @return 返回值
     */
    public String taskBatchModifyProcessHandler(String flowIdStr){
        if(StringUtils.isBlank(flowIdStr)){
            GoodsOutProcessListParam listParam = new GoodsOutProcessListParam();
            listParam.setUnNodeFlag("-1");
            listParam.setUnCurNodeId("N3");
            List<GoodsOutProcessDTO> processList = safetyGoodsOutProcessService.getList(listParam);
            if(!CollectionUtils.isEmpty(processList)){
                int totalPage = processList.size()/500+(processList.size()%500>0?1:0);
                for(int i=0;i<totalPage;i++){
                    List<GoodsOutProcessDTO> subList = processList.subList(i*500, Math.min((i + 1) * 500, processList.size()));
                    flowIdStr = subList.stream().map(GoodsOutProcessDTO::getFlowId).collect(Collectors.joining(","));
                    batchModifyGoodsProcess(flowIdStr);
                }
            }
        }else{
            batchModifyGoodsProcess(flowIdStr);
        }
        return "";
    }

    //获取流程当前节点处理人信息
    public GoodsProcessNodeVO getProcessCurDealHandler(GoodsOutNodeUpdateParam param){
        //获取审批后当前节点审批人
        GoodsProcessNodeVO nodeVO = new GoodsProcessNodeVO();
        nodeVO.setId(param.getId());
        nodeVO.setFlowId(param.getFlowId());

        Object retObj = safetyBpmProcessService.getCurrentNodesInfo(param.getId(),param.getFlowId(),GOODS_OUT_PROCESS_FORM_ID);
        JSONArray jsonArray = JSONArray.parseArray(String.valueOf(retObj));
        StringBuilder curHandler = new StringBuilder();
        StringBuilder curNodeId = new StringBuilder();
        StringBuilder curNodeName = new StringBuilder();
        if(!jsonArray.isEmpty()){
            for(int i=0;i<jsonArray.size();i++){
                JSONObject node = jsonArray.getJSONObject(i);
                if(!curNodeId.toString().isEmpty()){
                    curNodeId.append(".");
                    curNodeName.append(",");
                }
                curNodeId.append(node.get("nodeId"));
                curNodeName.append(node.get("nodeName"));
                JSONArray items = node.getJSONArray("workitems");
                if(items != null && !items.isEmpty()){
                    for (int j=0;j<items.size();j++) {
                        JSONObject item = items.getJSONObject(j);
                        if (!curHandler.toString().isEmpty()) {
                            curHandler.append(",");
                        }
                        curHandler.append(item.get("expecter"));
                    }
                }
            }
        }
        nodeVO.setCurNodeHandler(curHandler.toString());
        nodeVO.setCurNodeId(curNodeId.toString());
        nodeVO.setCurNodeName(curNodeName.toString());
        return nodeVO;
    }

    /**
     * 出门证同步dms车辆离厂日期
     * @param vehicleList
     * @param curPersonNo
     */
    public void sendMesToDMS(List<SafetyGoodsOutDetailVehicleDO> vehicleList, String curPersonNo) {
        List<Map<String, Object>> list = new ArrayList<>();
        String date = DateUtils.format(new Date(), 2);

        vehicleList.forEach(en -> {
            if (StringUtils.isNotBlank(en.getVinNo())) {
                Map<String, Object> map = new HashMap<>();
                map.put("vin", en.getVinNo());
                map.put("actualDepartureTime", date);
                list.add(map);
            }
        });

        Map<String, Object> headMap = new HashMap<>();
        headMap.put("createBy", curPersonNo);
        headMap.put("createTime", date);
        headMap.put("requestSource", FROM_SYSTEM_NAME);
        headMap.put("requestTarget", TO_SYSTEM_NAME);
        headMap.put("requestType", REQUEST_TYPE);

        Map headParam = new HashMap();
        headParam.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("head", headMap);
        bodyParam.put("list", list);

        try {
            //调接口
            String tokenResult = webserviceUtil.sendHttp(dmsCrzUrl, "2", bodyParam.toString(), headParam, "POST");
            LogUtils.info( "{}获取sendMesToDMS的接口调用,返回结果{}",curPersonNo, tokenResult);
        } catch (Exception e) {
            LogUtils.info( "{}获取sendMesToDMS的接口调用,错误描述{}",curPersonNo, e.getMessage());
        }
    }

    public SafetyGoodsOutDetailReceiveDO getReceiveDetail(GoodsOutDetailReceiveListParam en){
        List<SafetyGoodsOutDetailReceiveDO> list = safetyGoodsOutDetailReceiveService.selectList(en);
        return CollectionUtils.isEmpty(list)?null:list.get(0);
    }

    public void saveReceiveDetail(SafetyGoodsOutDetailReceiveDO en){
        if(StringUtils.isEmpty(en.getProcessId())){
            throw new BizException("9999","processId必填");
        }
        //保存接收信息
        en.setReceiveTime(DateUtils.format(new Date(),2));
        safetyGoodsOutDetailReceiveService.updateByCondition(en);

        String processId = en.getProcessId();
        //查询该流程对应的所有的接收情况
        GoodsOutDetailReceiveListParam param = new GoodsOutDetailReceiveListParam();
        param.setProcessId(processId);
        List<SafetyGoodsOutDetailReceiveDO> list = safetyGoodsOutDetailReceiveService.selectList(param);
        if(CollectionUtils.isEmpty(list)){return;}
        Map<Integer, SafetyGoodsOutDetailReceiveDO> map = list.stream()
                .collect(Collectors.toMap(SafetyGoodsOutDetailReceiveDO::getId, item -> item));


        //当全部都接收完，开始更新流程的节点
        boolean allReceive = true;
        String receiveImageUrl = "";
        for (SafetyGoodsOutDetailReceiveDO e:list) {
            if(allReceive && StringUtils.isEmpty(e.getReceiveStatus())){
                allReceive = false;
            }
            receiveImageUrl += (StringUtils.isEmpty(e.getReceiveImg()) ? "" : e.getReceiveImg());

            //多個接收人，接收人中只要有一个全部接收就结束
            if(!StringUtils.isEmpty(e.getReceiveStatus()) && "1".equals(e.getReceiveStatus())){
                allReceive = true;
                break;
            }
        }

        //更新流程的接收状态
        SafetyGoodsOutProcessDO entity = new SafetyGoodsOutProcessDO();
        entity.setPublishTime(new Date());
        entity.setId(processId);
        entity.setReceiveConfirmImg(receiveImageUrl);
        Map<String, String> receiveMap = safetyGoodsOutDetailReceiveService.getProcessReceiveStatus(processId, null);
        entity.setReceiveStatus(receiveMap.get("receiveStatus"));
        entity.setUnReceiveReason(receiveMap.get("unReceiveReason"));

        if(allReceive) {
            entity.setNodeFlag(7);
        }
        //更新流程的信息
        safetyGoodsOutProcessService.updateByPrimaryKeySelective(entity);

        try {
            sendProcessAndCancelTodo(allReceive,processId,list,map.get(en.getId()));
        }catch (Exception e){
            LogUtils.error("出门证物资接收节点删除代办出错，流程id={}，信息={}",en.getProcessId(),e);
        }
    }

    /**
     * 走流程和删除代办
     * @param allReceive
     * @param processId
     * @param list
     * @param en
     */
    public void sendProcessAndCancelTodo(boolean allReceive,String processId,List<SafetyGoodsOutDetailReceiveDO> list,SafetyGoodsOutDetailReceiveDO en){
        //如果全部处理完，就推流程到下个节点，并且删除所有相关的EIP代办;否则，只删除自己的代办
        if(allReceive){
            LogUtils.info("接收完前删除代办，参数={}",processId);
            SafetyGoodsOutProcessDO entityByCondition = safetyGoodsOutProcessService.getEntityByCondition(processId);
            if(!ObjectUtils.isEmpty(entityByCondition)){
                BpmProcessParam processParam = safetyBpmProcessService.initProcessParam(entityByCondition.getId(),entityByCondition.getFlowId(),
                        GOODS_OUT_MODEL_ID,GOODS_OUT_PROCESS_FORM_ID,RECEIVER,false,"同意","");
                safetyBpmProcessService.approveProcessByUserName(entityByCondition.getId(), entityByCondition.getFlowId(),
                        GOODS_OUT_MODEL_ID, GOODS_OUT_PROCESS_FORM_ID,processParam.getProcessParam(), RECEIVER);
                LogUtils.info("接收完前删除代办，方法内部，参数={}",list);

                //消除EIP待办
                for (SafetyGoodsOutDetailReceiveDO e:list) {
                    String modelId = "SAFETY-safety_goods_out_detail_receive-" + e.getId();
                    EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                    eipCommonService.setEipToDoDone(eipToDoVO);
                }
            }
        }else{
            if(ObjectUtils.isEmpty(en)){return;}
            LogUtils.info("未接收完前删除代办，参数={}",en.getId());
            String modelId = "SAFETY-safety_goods_out_detail_receive-" + en.getId();
            EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
            eipCommonService.setEipToDoDone(eipToDoVO);
        }
    }

    public String abandonProcess(String processId,String flowId,String userName){
        if(StringUtils.isEmpty(processId)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "主键ID不能为空");
        }
        if(StringUtils.isEmpty(flowId)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "流程ID不能为空");
        }
        if(StringUtils.isEmpty(userName)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "当前登录名不能为空");
        }

        BpmProcessParam processParam = new BpmProcessParam();
        processParam.setProcessId(flowId);
        processParam.setUserName(userName);
        processParam.setFormInstanceId(processId);
        processParam.setModelId(GOODS_OUT_MODEL_ID);
        processParam.setTemplateFormId(GOODS_OUT_PROCESS_FORM_ID);

        //废弃流程
        String rt = bpmServiceHandler.discardBpmFlow(processParam);

        //废弃失败
        if(!"T:执行成功".equals(rt)){
            LogUtils.info("出门证流程废弃失败，失败原因：{}",rt);
            return "废弃失败！请联系管理员";
        }

        //更新流程状态
        SafetyGoodsOutProcessDO en = new SafetyGoodsOutProcessDO();
        en.setId(processId);
        en.setStatus("40");
        safetyGoodsOutProcessService.updateByEntity(en);
        return "废弃成功！";
    }

    /**
     * 获取物资出门证参考附件列表
     * @return
     */
    public List getGoodsTemplateFile(){
        List<Map<String,String>> templateFiles = new ArrayList<>();
        Map<String,String> oneMap = new HashMap<>();
        oneMap.put("fileId","GOODS_TEMP_FILE01");
        oneMap.put("fileName","出门证审核权限调整对接人清单.xlsx");
        oneMap.put("fileUrl","https://img.yutong.com/winered/app/oss/pull/mc-im/a4632dd38dedd0adc787fd64761a8b3b.xlsx");
        templateFiles.add(oneMap);

        Map<String,String> twoMap = new HashMap<>();
        twoMap.put("fileId","GOODS_TEMP_FILE02");
        twoMap.put("fileName","物资出厂信息导入模板.xls");
        twoMap.put("fileUrl","https://img.yutong.com/winered/app/oss/pull/mc-im/19af46d29c2f513ecee0c225cf5f45e2.xls");
        templateFiles.add(twoMap);

        Map<String,String> threeMap = new HashMap<>();
        threeMap.put("fileId","GOODS_TEMP_FILE03");
        threeMap.put("fileName","出门证审核人清单.xlsx");
        threeMap.put("fileUrl","https://img.yutong.com/winered/app/oss/pull/mc-im/6a00680f99928aeab3a55bf8854b0008.xlsx");
        templateFiles.add(threeMap);

        return templateFiles;
    }

    /**
     * 获取物资出门证参考附件
     * @return 返回单个对象
     */
    public Map<String,String> getGoodsTemplateFileById(String fileId){
        Map<String,String> oneMap = new HashMap<>();
        switch (fileId){
            case "GOODS_TEMP_FILE02":
                oneMap.put("fileId","GOODS_TEMP_FILE02");
                oneMap.put("fileName","物资出厂信息导入模板.xls");
                oneMap.put("fileUrl","https://img.yutong.com/winered/app/oss/pull/mc-im/19af46d29c2f513ecee0c225cf5f45e2.xls");
                break;
            case "GOODS_TEMP_FILE03":
                oneMap.put("fileId","GOODS_TEMP_FILE03");
                oneMap.put("fileName","出门证审核人清单.xlsx");
                oneMap.put("fileUrl","https://img.yutong.com/winered/app/oss/pull/mc-im/6a00680f99928aeab3a55bf8854b0008.xlsx");
                break;
            default:
                oneMap.put("fileId","GOODS_TEMP_FILE01");
                oneMap.put("fileName","出门证审核权限调整对接人清单.xlsx");
                oneMap.put("fileUrl","https://img.yutong.com/winered/app/oss/pull/mc-im/a4632dd38dedd0adc787fd64761a8b3b.xlsx");
                break;
        }
        return oneMap;
    }
}
