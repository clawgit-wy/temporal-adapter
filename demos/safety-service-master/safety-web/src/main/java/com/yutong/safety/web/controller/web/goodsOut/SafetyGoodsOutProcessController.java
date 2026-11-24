package com.yutong.safety.web.controller.web.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.param.*;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutEIPParamDTO;
import com.yutong.safety.core.handler.SafetyBpmProcessHandler;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "物资出门证WEB-流程管理")
@RestController
@RequestMapping("/web/safety/goodsOut/process")
public class SafetyGoodsOutProcessController {

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
    public Object saveAndCreateProcess(@RequestBody GoodsOutProcessAddParam processDTO,@RequestHeader("X-Access-Token") String token) {
            if(StringUtils.isBlank(processDTO.getCreator()) && StringUtils.isBlank(token)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“creator”不能为空");
            }
            if(StringUtils.isBlank(processDTO.getCreator())){
                String userName = TokenUtils.getUserName(token);
                if(StringUtils.isBlank(userName)){
                    throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
                }
                processDTO.setCreator(userName);
            }
        try {
            return safetyGoodsOutProcessHandler.saveAndCreateProcessByUserName(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("创建出门证流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("审批流程")
    @RequestMapping(value = "/auditProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object audit(@RequestBody GoodsOutProcessUpdateParam processDTO,@RequestHeader("X-Access-Token") String token){
        if(StringUtils.isBlank(processDTO.getCurPersonLogin()) && StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“curPersonLogin”不能为空");
        }
        if(StringUtils.isBlank(processDTO.getCurPersonLogin())){
            String userName = TokenUtils.getUserName(token);
            if(StringUtils.isBlank(userName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            processDTO.setCurPersonLogin(userName);
        }
        try{
            return safetyGoodsOutProcessHandler.updateAndAuditByUserName(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }


    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("审批流程")
    @RequestMapping(value = "/auditForProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object auditForProcess(GoodsOutProcessUpdateDTO processDTO, @RequestHeader("X-Access-Token") String token){
        if(StringUtils.isBlank(processDTO.getCurPersonLogin()) && StringUtils.isBlank(token)){
            throw new BizException("9999","提交人“curPersonLogin”不能为空");
        }
        if(StringUtils.isBlank(processDTO.getCurPersonLogin())){
            String userName = TokenUtils.getUserName(token);
            if(StringUtils.isBlank(userName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            processDTO.setCurPersonLogin(userName);
        }
        try{
            return safetyGoodsOutProcessHandler.updateAndAuditForProcess(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    /**
     * 数据新增或者更新操作
     * @param processDTO 入参
     * @return 返回值
     */
    @ApiOperation(value = "转派流程")
    @RequestMapping(value = "/updateTransfer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateTransfer(@RequestBody GoodsOutProcessUpdateParam processDTO,@RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(processDTO.getCurPersonLogin()) && StringUtils.isBlank(token)){
            throw new BizException("9999","提交人“curPersonLogin”不能为空");
        }
        if(StringUtils.isBlank(processDTO.getCurPersonLogin())){
            String userName = TokenUtils.getUserName(token);
            if(StringUtils.isBlank(userName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            processDTO.setCurPersonLogin(userName);
        }
        try {
            return safetyGoodsOutProcessHandler.updateTransferByUserName(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation("流程传阅")
    @RequestMapping(value = "/circulateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object circulate(String flowId, String fromUser, String toUser,@RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(fromUser) || StringUtils.isBlank(toUser)|| StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"入参不能为空，请联系管理员");
        }
        if(StringUtils.isBlank(fromUser)){
            String userName = TokenUtils.getUserName(token);
            if(StringUtils.isBlank(userName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            fromUser = userName;
        }
        return safetyBpmProcessHandler.circulate(flowId,fromUser,toUser);
    }

    @ApiOperation("流程驳回")
    @RequestMapping(value = "/rejectProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object rejectProcess(@RequestBody GoodsOutProcessUpdateParam processDTO,
                                @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(token)){
            throw new BizException("9999","入参不能为空，请联系管理员");
        }

        return safetyGoodsOutProcessHandler.rejectByUserName(processDTO);
    }

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(GoodsOutProcessListParam param,@RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(param.getCurPersonLogin())){
            param.setCurPersonLogin(userName);
        }
        return safetyGoodsOutProcessHandler.getPageList(param,token);
    }

    @ApiOperation("出门证明细导出")
    @RequestMapping(value = "/exportData", method = {RequestMethod.GET,RequestMethod.POST})
    public Object exportData(GoodsOutProcessListParam param, HttpServletResponse response,
                             @RequestHeader("X-Access-Token") String token){
        try {
            String fileName = new String(("出门证流程明细"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyGoodsOutProcessHandler.exportData(param, token);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出出门证流程明细异常", e);
        }
        return null;
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
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation(value = "根据ID获取出门证详情")
    @RequestMapping(value = "/getDetailById", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getDetailById(String processId){
        return safetyGoodsOutProcessHandler.getDetailById(processId);
    }


    @ApiOperation(value = "解析excel附件")
    @RequestMapping(value = "importDetailExcel", method = {RequestMethod.GET,RequestMethod.POST})
    public Object importEnterFiling(HttpServletResponse response,
                                @RequestParam("file") MultipartFile file,
                                    @RequestHeader("X-Access-Token") String token) {
        //根据token获取当前用户的对接人信息
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已过期，请重新登录！");
        }
        response.setCharacterEncoding("utf-8");
        List<Map<String,String>> eList = safetyGoodsOutProcessHandler.importDetailExcel(file);
        if(CollectionUtils.isEmpty(eList)){
            return new BizException(ResponseEnum.PARAM_ERROR.getCode(), "导入数据有异常");
        }
        return eList;
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
     * 数据新增或者更新操作
     * @param nodeUpdateParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "更新节点处理人")
    @RequestMapping(value = "/getProcessCurDealHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getProcessCurDealHandler(GoodsOutNodeUpdateParam nodeUpdateParam){
        try {
            if(nodeUpdateParam == null|| nodeUpdateParam.getId() == null || StringUtils.isBlank(nodeUpdateParam.getFlowId())){
                throw new BizException("9999","入参id和flowId都必填");
            }
            return safetyGoodsOutProcessHandler.getProcessCurDealHandler(nodeUpdateParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation(value = "物资详情模板-模板下载")
    @RequestMapping(value = "/download", method = {RequestMethod.GET,RequestMethod.POST})
    public void download(HttpServletResponse response) {
        String fileName = "物资详情模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/goodsOutDetails.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            LogUtils.error("物资详情模板-模板下载出错{}",e.getMessage());
        }
    }

    @ApiOperation(value = "保存接收详情")
    @RequestMapping(value = "/saveReceiveDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveReceiveDetail(SafetyGoodsOutDetailReceiveDO en){
        safetyGoodsOutProcessHandler.saveReceiveDetail(en);
        return new VO(ResponseEnum.SUCCESS.getCode(), "接收成功", "");
    }

    @ApiOperation(value = "查询接收详情(只需传processId和receiveEmpNo)")
    @RequestMapping(value = "/getReceiveDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getReceiveDetail(GoodsOutDetailReceiveListParam en){
        return new VO(ResponseEnum.SUCCESS.getCode(), "", safetyGoodsOutProcessHandler.getReceiveDetail(en));
    }

    @ApiOperation(value = "EIP待办批量转办")
    @RequestMapping(value = "/changeEIPTodoWithList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object changeEIPTodoWithList(@RequestBody SafetyGoodsOutEIPParamDTO en){
        safetyGoodsOutProcessHandler.changeEIPTodoSelf(en.getEnList());
        return new VO(ResponseEnum.SUCCESS.getCode(), "转办成功", "");
    }

    @ApiOperation(value = "物资详情模板-车辆出厂模板下载")
    @RequestMapping(value = "/downloadCarExcel", method = {RequestMethod.GET,RequestMethod.POST})
    public void downloadCarExcel(HttpServletResponse response) {
        String fileName = "车辆出厂模板下载.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/goodsOutCar.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            LogUtils.error("物资详情模板-车辆出厂模板下载{}",e.getMessage());
        }
    }

    @ApiOperation(value = "获取物资出门证流程参考附件")
    @RequestMapping(value = "/getGoodsTemplateFile", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getGoodsTemplateFile(@RequestHeader("X-Access-Token") String token){
        try{
            return safetyGoodsOutProcessHandler.getGoodsTemplateFile();
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"未找到物资出门证参考附件");
        }
    }

    @ApiOperation(value = "下载资出门证流程参考附件")
    @RequestMapping(value = "/downGoodsTemplateFile", method = {RequestMethod.GET,RequestMethod.POST})
    public void downGoodsTemplateFile(HttpServletResponse response,
                                      String fileId,
                                      @RequestHeader("X-Access-Token") String token) {
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
