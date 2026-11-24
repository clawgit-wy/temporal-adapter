package com.yutong.safety.core.handler.hot;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.FireStatusEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.hot.FireApprove;
import com.yutong.safety.core.db.entity.hot.FireApproveExtendDO;
import com.yutong.safety.core.db.entity.hot.param.SafetyHotWorkLensParam;
import com.yutong.safety.core.db.entity.hot.param.SafetyMonitorVideoParam;
import com.yutong.safety.core.db.entity.hot.param.SyncFireApproveListParam;
import com.yutong.safety.core.db.entity.hot.param.SyncFireApproveParam;
import com.yutong.safety.core.db.entity.hot.vo.SafetyHotWorkLensVO;
import com.yutong.safety.core.db.entity.hot.vo.SyncFireApproveListVO;
import com.yutong.safety.core.db.entity.hot.vo.SyncFireApproveTreeVO;
import com.yutong.safety.core.service.hot.FireApproveService;
import com.yutong.safety.core.service.hot.SafetyHotWorkService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

@Component
public class SafetyHotWorkHandler {
    @Autowired
    private FireApproveService fireApproveService;

    @Autowired
    private SafetyHotWorkService safetyHotWorkService;

    /**
     * 监控设备查询-web
     * @param param
     * @param tocken
     * @return
     */
    public Object getLensList(SafetyHotWorkLensParam param,String tocken) {
        return this.getLensList(param);
    }
    /**
     * 监控设备查询-app
     * @param param
     * @return
     */
    public Object getLensList(SafetyHotWorkLensParam param) {
        long total;
        List<SafetyHotWorkLensVO> safetyHotWorkLensList;
        try{
            total = safetyHotWorkService.pageListCount(param);
            safetyHotWorkLensList =safetyHotWorkService.getLensList(param);
        }catch (Exception e){
            LogUtils.error("动火作业-查询动火作业列表异常",e);
            return VoUtil.exception();
        }
        return  VoUtil.successData(new PageVO<>(total, safetyHotWorkLensList));
    }

    /**
     * web 动火列表查询
     * @param fireApproveListParam
     * @return
     */
    public Object getFireApproveList(SyncFireApproveListParam fireApproveListParam,String tocken) {
        List<SyncFireApproveTreeVO> fireApproveMap= Lists.newArrayList();
        try{
            fireApproveMap = safetyHotWorkService.getFireApproveList(fireApproveListParam,tocken);
        }catch (BizException e){
            return VoUtil.successMsg(e.getMsg(),fireApproveMap);
        }catch (Exception e){
            LogUtils.error("动火作业-查询动火作业列表异常",e);
            return VoUtil.exception(e.getMessage());
        }
        return VoUtil.successData(fireApproveMap);
    }

    /**
     * APP 动火列表查询
     * @param fireApproveListParam
     * @return
     */
    public Object getFireApproveList(SyncFireApproveListParam fireApproveListParam) {
        List<SyncFireApproveListVO>fireApproveList = Lists.newArrayList();
        try{
            fireApproveList = safetyHotWorkService.getFireApproveList(fireApproveListParam);
        }catch (Exception e){
            LogUtils.error("动火作业-查询动火作业列表异常",e);
            return VoUtil.exception();
        }
        return VoUtil.successData(fireApproveList);
    }

    /**
     * 根据eip流程编号查询动火作业审批
     * @param fireApproveParam
     * @return
     */
    public Object sync(SyncFireApproveParam fireApproveParam) {
        LogUtils.info("eip sync fire to us, documentNum={}, applicantNum={}, param={}", fireApproveParam.getDocumentNum(), fireApproveParam.getApplicantNum(), fireApproveParam);
        if(StringUtils.isBlank(fireApproveParam.getDocumentNum())||
                StringUtils.isBlank(fireApproveParam.getApplicantNum())||
                StringUtils.isBlank(fireApproveParam.getSource())||
                StringUtils.isBlank(fireApproveParam.getApproveStatus())){
            return VoUtil.success();
        }
        // 不存在新增
        FireApprove fireApproveDB = fireApproveService.selectFireApproveByDocumentNum(fireApproveParam.getDocumentNum());
        if (Objects.isNull(fireApproveDB)) {
            fireApproveDB = new FireApprove();
            BeanUtils.copyProperties(fireApproveParam, fireApproveDB);
            int record = fireApproveService.insertFireApprove(fireApproveDB);
            saveFireApproveExtend(fireApproveParam, fireApproveDB);
            LogUtils.info("新增成功，单据号={}, record={}", fireApproveParam.getDocumentNum(), record);
            return VoUtil.success();
        }
        // 已存在修改
        FireApprove updateApprove = new FireApprove();
        BeanUtils.copyProperties(fireApproveParam, updateApprove);
        //当前审批节点信息
        setFlowNodeMsg(fireApproveParam, updateApprove);
        updateApprove.setId(fireApproveDB.getId());
        LogUtils.info("修改前信息，documentNum={}, fireApproveDB={}", fireApproveParam.getDocumentNum(), fireApproveDB);
        int record = fireApproveService.updateFireApprove(updateApprove);
        saveFireApproveExtend(fireApproveParam, fireApproveDB);
        LogUtils.info("更新成功，documentNum={}, record={}", fireApproveParam.getDocumentNum(), record);
        return VoUtil.success();
    }

    /**
     * 保存动火作业扩展信息
     * @param fireApproveParam
     * @param fireApproveDB
     */
    private void saveFireApproveExtend(SyncFireApproveParam fireApproveParam,FireApprove fireApproveDB){
        FireApproveExtendDO fireApproveExtend = fireApproveService.selectFireApproveExtendByfireApproveId(fireApproveDB.getId());
        if(Objects.isNull(fireApproveExtend)){
            FireApproveExtendDO fireApproveExtendDO = new FireApproveExtendDO(fireApproveParam);
            fireApproveExtendDO.setFireApproveId(fireApproveDB.getId());
            fireApproveService.insertFireApproveExtend(fireApproveExtendDO);
        }else{
            fireApproveExtend.setLensNums(fireApproveParam.getLensNums());
            fireApproveExtend.setLensNames(fireApproveParam.getLensNames());
            fireApproveExtend.setHotWorkArea(fireApproveParam.getHotWorkArea());
            fireApproveExtend.setHotPointEnvironment(fireApproveParam.getHotPointEnvironment());
            fireApproveExtend.setFireproofClothProtection(fireApproveParam.getFireproofClothProtection());
            fireApproveExtend.setFireEquipmentAndBucket(fireApproveParam.getFireEquipmentAndBucket());
            fireApproveExtend.setHotWorkOther(fireApproveParam.getHotWorkOther());
            fireApproveService.updateFireApproveExtend(fireApproveExtend);
        }
    }

    private void setFlowNodeMsg(SyncFireApproveParam fireApproveParam, FireApprove updateApprove) {
        String nodeId = fireApproveParam.getNodeId()==null ? "" : "_"+fireApproveParam.getNodeId();
        String nodeName = fireApproveParam.getNodeName();
        String mobile = fireApproveParam.getNodeReviewerMobile()==null ? "" : fireApproveParam.getNodeReviewerMobile();
        String name = fireApproveParam.getNodeReviewerName()==null ? "" : fireApproveParam.getNodeReviewerName();
        String flowNodeMsg="";
        String approveStatus = updateApprove.getApproveStatus();
        //废弃或审批通过，流程节点信息置空
        if(approveStatus.equals(FireStatusEnum.DISCARD.getApproveStatus()) || approveStatus.equals(FireStatusEnum.PASS.getApproveStatus())){
            flowNodeMsg="";
        }else if(StringUtils.isNotBlank(nodeName)){
            flowNodeMsg="流程已经到达"+nodeName+nodeId+"("+name+"/"+mobile+")"+",请及时沟通";
        }
        updateApprove.setFlowNodeMsg(flowNodeMsg);
    }

    public Object deleteLens(SafetyHotWorkLensParam param, String tocken) {
        try{
            safetyHotWorkService.deleteLens(param,tocken);
        }catch (BizException e){
            return VoUtil.exception(e.getMsg());
        }catch (Exception e){
            LogUtils.error("动火作业-删除镜头信息异常",e);
            return VoUtil.exception();
        }
        return VoUtil.success();
    }

    /**
     * 获取视频流 PC 端调用
     * @param param
     * @param tocken
     * @return
     */
    public Object getMonitorVideoUrl(SafetyMonitorVideoParam param, String tocken) {
        String url = "";
        try{
            url = safetyHotWorkService.getMonitorVideoUrl(param,tocken);
        }catch (BizException e){
            return VoUtil.successMsg(e.getMsg(),url);
        }catch (Exception e){
            LogUtils.error("动火作业-删除镜头信息异常",e);
            return VoUtil.exception();
        }
        return VoUtil.successData(url);
    }

    /**
     * 获取视频流 APP 端调用
     * @param param
     * @return
     */
    public Object getMonitorVideoUrl(SafetyMonitorVideoParam param) {
        String url = "";
        try{
             url = safetyHotWorkService.getMonitorVideoUrl(param);
        }catch (BizException e){
            return VoUtil.successMsg(e.getMsg(),url);
        }catch (Exception e){
            LogUtils.error("动火作业-删除镜头信息异常",e);
            return VoUtil.exception();
        }
        return VoUtil.successData(url);
    }

    /**
     * 监控设备导出
     * @param tocken
     * @return
     */
    public HSSFWorkbook export(SafetyHotWorkLensParam param,String tocken) {
        if(ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() ==0){
            param.setPageSize(5000);
        }
        if(param.getPageNum() == 0 || param.getPageNum() == 1) {
            param.setPageNum(1);
        }
        List<SafetyHotWorkLensVO> safetyHotWorkLensList =safetyHotWorkService.getLensList(param);
        if (safetyHotWorkLensList.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+ Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"镜头编号", "镜头名称", "SN号", "IP地址","型号"};
        String[][] values = new String[safetyHotWorkLensList.size()][];
        for(int i = 0; i < safetyHotWorkLensList.size(); i++){
            values[i] = new String[title.length];
            values[i][0] = safetyHotWorkLensList.get(i).getLensNo();
            values[i][1] = safetyHotWorkLensList.get(i).getLensName();
            values[i][2] = safetyHotWorkLensList.get(i).getLensSn();
            values[i][3] = safetyHotWorkLensList.get(i).getLensIp();
            values[i][4] = safetyHotWorkLensList.get(i).getLensModel();
        }
        String sheetName = "监控设备列表信息";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * PC端查询镜头状态
     * @param param
     * @param tocken
     * @return
     */
    public Object getLensStatus(SafetyHotWorkLensParam param,String tocken) {
        JSONObject data;
        try{
            data =safetyHotWorkService.getLensStatus(param,tocken);
        }catch (BizException e){
            return VoUtil.successMsg(e.getMsg(),"");
        }catch (Exception e){
            LogUtils.error("动火作业-查询监控设备状态信息异常",e);
            return VoUtil.exception();
        }
        return VoUtil.successData(data);
    }

    /**
     * 非PC端查询镜头状态
     * @param param
     * @return
     */
    public Object getLensStatus(SafetyHotWorkLensParam param) {
        JSONObject data;
        try{
            data =safetyHotWorkService.getLensStatus(param);
        }catch (BizException e){
            return VoUtil.successMsg(e.getMsg(),"");
        }catch (Exception e){
            LogUtils.error("动火作业-查询监控设备状态信息异常",e);
            return VoUtil.exception();
        }
        return VoUtil.successData(data);
    }
}
