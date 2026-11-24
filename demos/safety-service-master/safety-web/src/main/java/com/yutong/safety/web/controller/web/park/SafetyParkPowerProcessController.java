package com.yutong.safety.web.controller.web.park;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDO;
import com.yutong.safety.core.dto.build.SafetyBuildManageProcessDTO;
import com.yutong.safety.core.dto.park.SafetyParkPowerProcessDTO;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

@Api(tags = "人车权限调整流程")
@RestController
@RequestMapping("/web/park/safety/parkPowerProcess")
public class SafetyParkPowerProcessController {
    @Autowired
    SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.POST})
    public Object saveAndCreateProcess(@RequestBody SafetyParkPowerProcessDTO entity, @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(entity.getCreateEmployeeLogin()) && StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“CreateEmployeeLogin”不能为空");
        }
        try {
            safetyParkPowerProcessHandler.saveAndCreateProcess(entity,token);
            return null;
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("创建人车大门权限调整流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation(value = "审批流程")
    @RequestMapping(value = "/approvalProcess", method = {RequestMethod.POST})
    public Object approvalProcess(@RequestBody SafetyParkPowerProcessDTO entity, @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        try {
            return safetyParkPowerProcessHandler.approvalProcess(entity,token);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批人车大门权限调整流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }

    @ApiOperation(value = "根据id查询明细")
    @RequestMapping(value = "/getDetailById", method = {RequestMethod.POST})
    public Object getDetailById(String id){
        return safetyParkPowerProcessHandler.getDetailById(id);
    }

    @ApiOperation(value = "根据userCode查询车辆信息明细")
    @RequestMapping(value = "/getListByUserCode", method = {RequestMethod.POST})
    public Object getListByUserCode(@RequestBody List<String> userCodeList){
        return safetyParkPowerProcessHandler.getListByUserCode(userCodeList);
    }

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.POST})
    public Object getPageList(SafetyParkPowerProcessDTO param,@RequestHeader("X-Access-Token") String token){
        return safetyParkPowerProcessHandler.getPageList(param,token);
    }

    @ApiOperation("下载模板")
    @RequestMapping(value = "/download", method = {RequestMethod.GET})
    public void download(HttpServletResponse response) {
        String fileName = "人员导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/userImport.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"大门权限申请下载人员导入模板出错={}\"}",e.getMessage());
        }
    }
}
