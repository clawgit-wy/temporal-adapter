package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.spot.SafetyPatrolPlanHandler;
import com.yutong.safety.core.handler.spot.SafetyPatrolRecordHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author gwxiexj
 * @since 2024-05-22 16:48
 */
@Api(tags = "治安巡查-任务设置")
@RestController
@RequestMapping("/web/safety/patrol/plan")
public class SafetyPatrolPlanController {

    @Autowired
    private SafetyPatrolPlanHandler safetyPatrolPlanHandler;

    @Autowired
    private SafetyPatrolRecordHandler safetyPatrolRecordHandler;

    /**
     * 列表查询方法
     * @param param 入参
     * @param token 用户token
     * @return 返回值
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/list", method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(PatrolPlanListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyPatrolPlanHandler.pageList(param,token);
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param response 请求
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.GET,RequestMethod.POST})
    public Object export(PatrolPlanListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("治安巡查-任务设置列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyPatrolPlanHandler.export(param, token);
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
            LogUtils.error("导出治安巡查-任务设置列表异常", e);
        }
        return null;
    }


    /**
     * 批量启用禁用信息
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "启用禁用状态接口")
    @RequestMapping(value = "/modifyStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyStatus(SpotCheckPlanBatchParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyPatrolPlanHandler.modifyStatus(param.getIds(), param.getStatus(), token);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e){
            LogUtils.error("治安巡查-任务设置批量启用禁用功能异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"治安巡查-任务设置批量启用禁用功能异常");
        }
    }

    /**
     * 批量更新休眠
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "更新休眠接口")
    @RequestMapping(value = "/modifyDormancy", method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyDormancy(SpotCheckPlanDormancyParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            PatrolPlanAddParam param1 = new PatrolPlanAddParam();
            param1.setIdStr(param.getIds());
            param1.setDormancyBegin(param.getDormancyBegin());
            param1.setDormancyEnd(param.getDormancyEnd());

            return safetyPatrolPlanHandler.modifyDormancy(param1, token);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e){
            LogUtils.error("治安巡查-任务设置新增/修改操作异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"治安巡查-任务设置新增/修改操作异常");
        }
    }

    /**
     * 新增
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "新增接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET,RequestMethod.POST})
    public Object add(PatrolPlanAddParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyPatrolPlanHandler.add(param, token);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e){
            LogUtils.error("治安巡查-任务设置批量启用禁用功能异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"治安巡查-任务设置批量启用禁用功能异常");
        }
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("审批流程")
    @RequestMapping(value = "/audit", method = {RequestMethod.GET,RequestMethod.POST})
    public VO audit(PatrolRecordAddParam processDTO, @RequestHeader("X-Access-Token") String token){
        try{
            BpmNodeReturnVO returnVO = safetyPatrolRecordHandler.updateAndAuditByUserName(processDTO);
            return new VO(ResponseEnum.SUCCESS.getCode(), returnVO.getMessage(),returnVO.getStatus());
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }

    }

    /**
     * 数据新增或者更新操作
     * @param processDTO 入参
     * @return 返回值
     */
    @ApiOperation(value = "转派流程")
    @RequestMapping(value = "/updateTransfer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateTransfer(PatrolRecordAddParam processDTO, @RequestHeader("X-Access-Token") String token) {
        try {
            String userName = TokenUtils.getUserName(token);
            processDTO.setCurPersonNo(userName);

            BpmNodeReturnVO returnVO = safetyPatrolRecordHandler.updateTransferByUserName(processDTO);
            return new VO(ResponseEnum.SUCCESS.getCode(), returnVO.getMessage(),returnVO.getStatus());
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("转派流程异常{}",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "" + e.getMessage());
        }
    }

    /**
     * 数据新增或者更新操作
     * @param nodeUpdateParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "更新节点处理人")
    @RequestMapping(value = "/modifyCurDealHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyCurDealHandler(PatrolNodeUpdateParam nodeUpdateParam){
        try {
            if(nodeUpdateParam == null|| nodeUpdateParam.getId() == null || StringUtils.isBlank(nodeUpdateParam.getFlowId())){
                throw new BizException("9999","入参id和flowId都必填");
            }
            return safetyPatrolRecordHandler.modifyCurDealHandler(nodeUpdateParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "" + e.getMessage());
        }
    }

    @ApiOperation(value = "定时生成岗位验证任务")
    @RequestMapping(value = "/taskPositionEdit", method = {RequestMethod.GET,RequestMethod.POST})
    public Object taskPositionEdit() {
        try {
            safetyPatrolPlanHandler.taskPositionEdit();
        }catch (Exception e){
            LogUtils.error("定时生成岗位验证任务异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"定时生成岗位验证任务异常");
        }
        return null;
    }

    @ApiOperation(value = "定时生成岗位验证通知")
    @RequestMapping(value = "/taskPositionMes", method = {RequestMethod.GET,RequestMethod.POST})
    public Object taskPositionMes() {
        try {
            safetyPatrolPlanHandler.taskPositionMes();
        }catch (Exception e){
            LogUtils.error("定时生成岗位验证通知异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"定时生成岗位验证通知异常");
        }
        return null;
    }
}
