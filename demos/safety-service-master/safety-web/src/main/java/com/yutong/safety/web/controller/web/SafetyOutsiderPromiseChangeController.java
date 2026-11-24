package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseChangeDTO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author lukunpeng
 * @time 2024-6-14
 * 外协外包入厂权限调整申请
 */
@Api(tags = "外协外包入厂权限调整申请")
@RestController
@RequestMapping("/web/safety/outsiderPromise")
public class SafetyOutsiderPromiseChangeController {

    @Autowired
    SafetyOutsiderPromiseChangeHandler safetyOutsiderPromiseChangeHandler;

    /**
     * 创建流程
     * @return
     */
    @ApiOperation("创建流程")
    @RequestMapping("/createFlow")
    public Object createFlow(SafetyOutsiderPromiseChangeDTO dto, @RequestHeader("X-Access-Token") String token){
        return safetyOutsiderPromiseChangeHandler.createFlow(dto, token);
    }
    /**
     * 查看流程
     * @return
     */
    @ApiOperation("查看流程")
    @RequestMapping("/view")
    public Object view(String flowId){
        return safetyOutsiderPromiseChangeHandler.view(flowId);
    }

    /**
     * 审批流程
     * @return
     */
    @ApiOperation("审批流程")
    @RequestMapping("/audit")
    public Object audit(SafetyOutsiderPromiseChangeDTO dto, @RequestHeader("X-Access-Token") String token){
        return safetyOutsiderPromiseChangeHandler.audit(dto, token);
    }

    /**
     * 获取历史权限
     * @return
     */
    @ApiOperation("获取历史权限")
    @RequestMapping("/promise")
    public Object promise(String empNos, String type, @RequestHeader("X-Access-Token") String token){
        return safetyOutsiderPromiseChangeHandler.promise(empNos, type, token);
    }

    /**
     * 获取历史权限
     * @return
     */
    @ApiOperation("根据用户名获取历史权限")
    @RequestMapping("/promiseByName")
    public Object promiseByName(String empStr, String type, @RequestHeader("X-Access-Token") String token){
        return safetyOutsiderPromiseChangeHandler.promiseByName(empStr,type,token);
    }

    /**
     * 分页查询数据
     * @param dto
     * @return
     */
    @ApiOperation("查询列表")
    @RequestMapping("/findList")
    public Object findList(SafetyOutsiderPromiseDTO dto, @RequestHeader("X-Access-Token") String token){
        return safetyOutsiderPromiseChangeHandler.findPageList(dto, token);
    }

    /**
     * 分页查询数据
     * @param dto
     * @return
     */
    @ApiOperation("查询列表-无权限过滤")
    @RequestMapping("/findListNoPower")
    public Object findListNoPower(SafetyOutsiderPromiseDTO dto, @RequestHeader("X-Access-Token") String token){
        return safetyOutsiderPromiseChangeHandler.findPageListNoPower(dto);
    }

    /**
     * 数据导出
     */
    @ApiOperation(value = "根据条件导出")
    @RequestMapping("/exportData")
    public Object exportData(SafetyOutsiderPromiseDTO dto,  HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("外协/外包员工权限信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyOutsiderPromiseChangeHandler.exportData(dto, token);
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
            LogUtils.error("外协/外包员工权限信息列表异常", e);
        }
        return null;
    }
    /**
     * @param param
     * @return
     */
    @ApiOperation(value = "批量导入")
    @RequestMapping("/import")
    public Object importData(CommonImportParam param, @RequestHeader("X-Access-Token") String token){
        return safetyOutsiderPromiseChangeHandler.importData(param, token);
    }

    @ApiOperation(value = "外协外包权限变更导入模板-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "外协外包权限变更导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/empPromiseChange.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回传EHR
     * @param flowId`
     * @return
     */
    @ApiOperation("更新流程状态")
    @RequestMapping("/updateFlowStatus")
    public Object updateFlowStatus(String flowId){
        safetyOutsiderPromiseChangeHandler.updateFlowStatus(flowId);
        return null;
    }

    @ApiOperation("定时下发EHR及门禁改写权限")
    @RequestMapping("/issueEHRTiming")
    public Object issueEHRTiming(){
        safetyOutsiderPromiseChangeHandler.issueEHRTiming();
        return null;
    }

    @ApiOperation("修改发起人")
    @RequestMapping("/updateCreator")
    public Object updateCreator(SafetyOutsiderPromiseChangeDTO dto, @RequestHeader("X-Access-Token") String token){
        safetyOutsiderPromiseChangeHandler.updateCreator(dto,token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "更新成功");
    }

    @ApiOperation("校验当前登录人有无权限修改人员信息")
    @RequestMapping("/validProcess")
    public Object validProcess(@RequestParam("curEmpNo")String curEmpNo,@RequestParam("chooseEmpNo")String chooseEmpNo){
        return new VO(ResponseEnum.SUCCESS.getCode(), "", safetyOutsiderPromiseChangeHandler.validProcess(curEmpNo,chooseEmpNo));
    }
}
