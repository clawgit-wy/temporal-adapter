package com.yutong.safety.web.controller.app.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.param.*;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutEIPDTO;
import com.yutong.safety.core.handler.SafetyBpmProcessHandler;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = "物资出门证APP-流程管理")
@RestController
@RequestMapping("/app/safety/goodsOut/process")
public class SafetyGoodsOutProcessAppController {

    @Autowired
    private SafetyGoodsOutProcessHandler safetyGoodsOutProcessHandler;

    @Autowired
    private SafetyBpmProcessHandler safetyBpmProcessHandler;

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveAndCreateProcess(@RequestBody GoodsOutProcessAddParam processDTO) {
        try{
            return safetyGoodsOutProcessHandler.saveAndCreateProcessByUserName(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("保存数据创建流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"保存数据创建流程异常");
        }
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("审批流程")
    @RequestMapping(value = "/auditProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object audit(@RequestBody GoodsOutProcessUpdateParam processDTO){
        try{
            return safetyGoodsOutProcessHandler.updateAndAuditByUserName(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"审批流程异常");
        }
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("审批流程")
    @RequestMapping(value = "/auditForProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object auditForProcess(GoodsOutProcessUpdateDTO processDTO){
        try{
            return safetyGoodsOutProcessHandler.updateAndAuditForProcess(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "审批流程异常");
        }
    }

    /**
     * 数据新增或者更新操作
     * @param processDTO 入参
     * @return 返回值
     */
    @ApiOperation(value = "转派流程")
    @RequestMapping(value = "/updateTransfer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateTransfer(@RequestBody GoodsOutProcessUpdateParam processDTO) {
        try {
            return safetyGoodsOutProcessHandler.updateTransferByUserName(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("流程转派异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "流程转派异常");
        }
    }

    @ApiOperation("流程传阅")
    @RequestMapping(value = "/circulateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object circulate(String flowId, String fromUser, String toUser) {
        return safetyBpmProcessHandler.circulate(flowId,fromUser,toUser);
    }

    /**
     * 数据新增或者更新操作
     * @param nodeUpdateParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "更新节点处理人")
    @RequestMapping(value = "/modifyCurDealHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyCurDealHandler(GoodsOutNodeUpdateParam nodeUpdateParam){
        try {
            if(nodeUpdateParam == null|| nodeUpdateParam.getId() == null || StringUtils.isBlank(nodeUpdateParam.getFlowId())){
                throw new BizException("9999","入参id和flowId都必填");
            }
            return safetyGoodsOutProcessHandler.modifyCurDealHandler(nodeUpdateParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("更新节点处理人异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"更新节点处理人异常");
        }
    }


    /**
     * 获取出门证放行清单
     * @param listParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "获取出门证放行清单")
    @RequestMapping(value = "/getOwnerRelease", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getOwnerRelease(GoodsOutAppListParam listParam){
        if(listParam == null|| StringUtils.isBlank(listParam.getCurPersonLogin())
                || StringUtils.isBlank(listParam.getSearchType())){
            throw new BizException("9999","未指定当前登录人/检索类型");
        }
        try {
            return safetyGoodsOutProcessHandler.selectReleaseList(listParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("获取出门证放行清单异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "获取出门证放行清单异常");
        }
    }

    @ApiOperation(value = "根据ID获取出门证详情")
    @RequestMapping(value = "/getDetailById", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getDetailById(String processId){
        return safetyGoodsOutProcessHandler.getDetailById(processId);
    }

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(GoodsOutProcessListParam param){
        if(StringUtils.isBlank(param.getCurPersonLogin())){
            throw new BizException("9999","当前登录人信息不能为空！");
        }
        return safetyGoodsOutProcessHandler.getPageList(param,"");
    }


    @ApiOperation("分页查询")
    @RequestMapping(value = "/getOwnerPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getOwnerPageList(GoodsOutProcessListParam param){
        if(StringUtils.isBlank(param.getCreator()) && StringUtils.isBlank(param.getSubmitPhone())){
            throw new BizException("9999","提交人和提交人电话至少有一个值不为空！");
        }
        return safetyGoodsOutProcessHandler.getPageList(param,"");
    }

    @ApiOperation(value = "解析excel附件")
    @RequestMapping(value = "importDetailExcel", method = {RequestMethod.GET,RequestMethod.POST})
    public Object importEnterFiling(HttpServletResponse response,
                                    @RequestParam("file") MultipartFile file) {
        response.setCharacterEncoding("utf-8");
        List<Map<String,String>> eList = safetyGoodsOutProcessHandler.importDetailExcel(file);
        if(CollectionUtils.isEmpty(eList)){
            return new BizException(ResponseEnum.PARAM_ERROR.getCode(), "导入数据有异常");
        }
        return eList;
    }

    /**
     *
     * @param listParam BPM流程ID拼接串
     * @return 返回指向结构
     */
    @ApiOperation(value = "获取出门人历史记录")
    @RequestMapping(value = "getProcessGooutList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getProcessGooutList(GoodsProcessGooutListParam listParam){
        if(StringUtils.isBlank(listParam.getSubmitNo())){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"未获取到当前登录人信息！");
        }
        return safetyGoodsOutProcessHandler.getProcessGooutList(listParam);
    }

    /**
     *
     * @param flowIdStr BPM流程ID拼接串
     * @return 返回指向结构
     */
    @ApiOperation(value = "批量更新流程当前节点信息")
    @RequestMapping(value = "batchModifyGoodsProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchModifyGoodsProcess(String flowIdStr){
        if(StringUtils.isBlank(flowIdStr)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"未指定流程实例ID，无法更新！");
        }
        return safetyGoodsOutProcessHandler.batchModifyGoodsProcess(flowIdStr);
    }

    /**
     *
     * @param flowIdStr BPM流程ID拼接串
     * @return 返回指向结构
     */
    @ApiOperation(value = "任务-批量更新流程当前节点信息")
    @RequestMapping(value = "taskBatchModifyProcessHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object taskBatchModifyProcessHandler(String flowIdStr){
        return safetyGoodsOutProcessHandler.taskBatchModifyProcessHandler(flowIdStr);
    }

    /**
     * 数据新增或者更新操作
     * @param nodeUpdateParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "更新节点处理人")
    @RequestMapping(value = "/getProcessCurDealHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getProcessCurDealHandler(GoodsOutNodeUpdateParam nodeUpdateParam){
        if(nodeUpdateParam == null|| nodeUpdateParam.getId() == null || StringUtils.isBlank(nodeUpdateParam.getFlowId())){
            throw new BizException("9999","入参id和flowId都必填");
        }
        try {
            return safetyGoodsOutProcessHandler.getProcessCurDealHandler(nodeUpdateParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("更新节点处理人异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "更新节点处理人异常");
        }
    }

    @ApiOperation(value = "保存接收详情")
    @RequestMapping(value = "/saveReceiveDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveReceiveDetail(SafetyGoodsOutDetailReceiveDO en){
        safetyGoodsOutProcessHandler.saveReceiveDetail(en);
        return new VO(ResponseEnum.SUCCESS.getCode(), "接收成功", "");
    }

    @ApiOperation(value = "保存接收详情EIP待办转办")
    @RequestMapping(value = "/changeEIPTodoBySelf", method = {RequestMethod.GET,RequestMethod.POST})
    public Object changeEIPTodoBySelf(SafetyGoodsOutEIPDTO en){
        List<SafetyGoodsOutEIPDTO> list = new ArrayList<>();
        list.add(en);
        safetyGoodsOutProcessHandler.changeEIPTodoSelf(list);
        return new VO(ResponseEnum.SUCCESS.getCode(), "转办成功", "");
    }

    @ApiOperation("流程废弃")
    @RequestMapping(value = "/abandonProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object abandonProcess(String processId,String flowId,String userName){
        return new VO(ResponseEnum.SUCCESS.getCode(), safetyGoodsOutProcessHandler.abandonProcess(processId,flowId,userName), "");
    }

    @ApiOperation(value = "获取物资出门证流程参考附件")
    @RequestMapping(value = "/getGoodsTemplateFile", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getGoodsTemplateFile(){
        try{
            return safetyGoodsOutProcessHandler.getGoodsTemplateFile();
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"未找到物资出门证参考附件");
        }
    }

    @ApiOperation(value = "下载资出门证流程参考附件")
    @RequestMapping(value = "/downGoodsTemplateFile", method = {RequestMethod.GET,RequestMethod.POST})
    public void downGoodsTemplateFile(HttpServletResponse response,
                                      String fileId) {
        if(StringUtils.isBlank(fileId) ){
            throw new BizException(ResponseEnum.FAIL.getCode(),"未指定参考附件下载链接，无法下载");
        }
        try {
            Map<String,String> fileMap = safetyGoodsOutProcessHandler.getGoodsTemplateFileById(fileId);
            String fileName = fileMap.get("fileName");
            String fileUrl = fileMap.get("fileUrl");
            // 创建URL
            URL url = new URL(fileUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream inputStream = conn.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            LogUtils.error("物资详情模板-车辆出厂模板下载{}",e.getMessage());
        }
    }
}
