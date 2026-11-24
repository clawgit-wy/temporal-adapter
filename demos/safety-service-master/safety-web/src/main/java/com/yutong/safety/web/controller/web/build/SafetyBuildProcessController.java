package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.build.param.BuildProcessAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessListParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessUpdateParam;
import com.yutong.safety.core.handler.SafetyBpmProcessHandler;
import com.yutong.safety.core.handler.build.SafetyBuildProcessHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

@Api(tags = "楼宇门禁-员工权限调整")
@RestController
@RequestMapping("/web/build/safety/buildProcess")
public class SafetyBuildProcessController {

    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;

    @Autowired
    private SafetyBpmProcessHandler safetyBpmProcessHandler;

    @Resource
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveAndCreateProcess(@RequestBody BuildProcessAddParam processDTO,
                                       @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(processDTO.getCreateEmployeeLogin()) && StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“creator”不能为空");
        }
        if(StringUtils.isBlank(processDTO.getCreateEmployeeLogin())){
            String userName = TokenUtils.getUserName(token);
            if(StringUtils.isBlank(userName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
            }
            processDTO.setCreateEmployeeLogin(userName);
        }
        try {
            String newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(processDTO.getCreateEmployeeNo());
            processDTO.setCreateEmployeeNo(newWorkNo);
            return safetyBuildProcessHandler.saveAndCreateProcessByUserName(processDTO,token);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("创建流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "创建流程异常");
        }
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("显示审批详情-涉及审批")
    @RequestMapping(value = "/showProcessDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object showProcessDetail(String processId,@RequestHeader("X-Access-Token") String token){
        if(StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"获取当前登录token失败");
        }
        return safetyBuildProcessHandler.showProcessDetail(processId,token);
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("查询流程详情-不涉及审批权限")
    @RequestMapping(value = "/viewProcessDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object viewProcessDetail(String processId,@RequestHeader("X-Access-Token") String token){
        return safetyBuildProcessHandler.showProcessDetail(processId,null);
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("查询流程主体-不涉及明细及汇总字段")
    @RequestMapping(value = "/viewProcessMain", method = {RequestMethod.GET,RequestMethod.POST})
    public Object viewProcessMain(String processId){
        if(StringUtils.isBlank(processId)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"入参“processId”不能为空");
        }
        return safetyBuildProcessHandler.getProcessByPrimaryKey(processId);
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("审批流程")
    @RequestMapping(value = "/auditProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object audit(@RequestBody BuildProcessUpdateParam processDTO, @RequestHeader("X-Access-Token") String token){
        try{
            return safetyBuildProcessHandler.updateAndAuditByUserName(processDTO, token);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "审批流程异常");
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

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(BuildProcessListParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        return safetyBuildProcessHandler.getPageList(param,token);
    }

    @ApiOperation("导出")
    @RequestMapping(value="/exportList",method = {RequestMethod.POST,RequestMethod.GET})
    public Object exportList(BuildProcessListParam param, HttpServletResponse response,
                             @RequestHeader("X-Access-Token") String token){
        try {
            String fileName = new String(("楼宇门禁申请流程列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyBuildProcessHandler.export(param, token);
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
            LogUtils.error("导出楼宇门禁申请流程列表异常", e);
        }
        return null;
    }

    /**
     * 数据新增或者更新操作
     * @param processId 入参
     * @return 返回值
     */
    @ApiOperation(value = "获取当前流程处理人")
    @RequestMapping(value = "/getProcessCurDealHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getProcessCurDealHandler(String processId){
        if(StringUtils.isBlank(processId)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"入参id必填");
        }
        return safetyBuildProcessHandler.getProcessCurDealHandler(processId);
    }

    @ApiOperation(value = "校验当前登录人的楼宇门禁角色类型")
    @RequestMapping(value = "/checkBuildRoleType", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkBuildRoleType(@RequestHeader("X-Access-Token") String token){
        if(StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"token失效");
        }
        String retVal = safetyBuildProcessHandler.checkUserType(token);
        return retVal.split(":")[0];
    }

    @ApiOperation(value = "楼宇门禁人员权限调整-人员模板下载")
    @GetMapping("/downloadEmpTemplate")
    public void downloadEmpTemplate(HttpServletResponse response) {
        String fileName = "车辆检查规则白名单导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/buildProcessEmpTemplate.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"模板下载失败"+e);
        }
    }

    @ApiOperation(value = "生成下发日志-测试")
    @RequestMapping(value = "/sendPowerByProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object sendPowerByProcess(String processId){
         safetyBuildProcessHandler.sendPowerByProcessNew(processId);
         return "0";
    }

    @ApiOperation(value = "发布后更新流程状态-测试")
    @RequestMapping(value = "/updateFlowStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateFlowStatus(String bpmFlowId,String status){
        safetyBuildProcessHandler.updateFlowStatus(bpmFlowId,status);
        return "0";
    }

    @ApiOperation(value = "获取责任部门对应EIP角色")
    @RequestMapping(value = "/getEipRoleByProcessId", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEipRoleByProcessId(String processId){
        try{
            if(StringUtils.isBlank(processId)){
                throw new BizException(ResponseEnum.FAIL.getCode(),"请指定流程ID");
            }
            return safetyBuildProcessHandler.getEipRoleByProcessId(processId);
        }catch (Exception e){
            LogUtils.error("获取EIP角色异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"获取EIP角色异常，"+e.getMessage());
        }
    }


    /**
     * 部分审批流程
     * @return 返回值
     */
    @ApiOperation("部分审批流程")
    @RequestMapping(value = "/auditPartProcess", method = {RequestMethod.POST})
    public Object auditPartProcess(@RequestBody BuildProcessUpdateParam param, @RequestHeader("X-Access-Token") String token){
        try{
            return safetyBuildProcessHandler.auditPartProcess(param, token);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("部分审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "部分审批流程异常");
        }
    }
}
