package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.spot.SafetySpotCheckPlanHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author gwxiexj
 * @since 2024-5-7 13:51
 */
@Api(tags = "设备设施点检-任务设置")
@RestController
@RequestMapping("/web/safety/spot/plan")
public class SafetySpotCheckPlanController {

    @Autowired
    private SafetySpotCheckPlanHandler safetySpotCheckPlanHandler;

    @Autowired
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    /**
     * 列表查询方法
     * @param param 入参
     * @param token 用户token
     * @return 返回值
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(SpotCheckPlanListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckPlanHandler.pageList(param,token);
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param response 请求
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(SpotCheckPlanListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("设备设施点检-任务设置列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySpotCheckPlanHandler.export(param, token);
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
            LogUtils.error("导出设备设施点检-设备巡检标准配置列表异常", e);
        }
        return null;
    }


    /**
     * 批量启用禁用信息
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "启用禁用状态接口")
    @RequestMapping("/modifyStatus")
    public Object modifyStatus(SpotCheckPlanBatchParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetySpotCheckPlanHandler.modifyStatus(param.getIds(), param.getStatus(), token);
        }catch (Exception e){
            LogUtils.error("任务设置批量启用禁用功能异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"任务设置批量启用禁用功能异常");
        }
    }

    /**
     * 数据添加操作
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/add")
    public Object add(SpotCheckPlanSaveParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetySpotCheckPlanHandler.add(param, token);
        }catch (Exception e){
            LogUtils.error("任务设置新增/修改操作异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"任务设置新增/修改操作异常");
        }
    }

    /**
     * 批量启用禁用信息
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "批量休眠接口")
    @RequestMapping("/modifyDormancy")
    public Object modifyDormancy(SpotCheckPlanDormancyParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetySpotCheckPlanHandler.modifyDormancy(param, token);
        }catch (Exception e){
            LogUtils.error("任务设置批量启用禁用功能异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"任务设置批量启用禁用功能异常");
        }
    }

    /**
     * 设备设施点检-获取设备设施点检详情
     * @param workNo 入参 员工号
     * @return 返回值
     */
    @ApiOperation(value = "01员工号转成6开头的")
    @RequestMapping("/employeeConvertWorkNo")
    public Object  employeeConvertWorkNo(String workNo){
        //01开头的员工号转为60开头的新员工号
        String newWorkNo = workNo;
        try{
            if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
                newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(workNo);
            }
            return newWorkNo;
        }catch (Exception e){
            LogUtils.error("获取设备点检记录异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"员工号转换失败！");
        }
    }

    /**
     * 更新任务-更新任务下的设备清单
     * @param planIdStr 入参
     * @return 返回值
     */
    @ApiOperation(value = "更新任务")
    @RequestMapping("/modifyTasks")
    public Object modifyTasks(String planIdStr, @RequestHeader("X-Access-Token") String token){
        try {
            return safetySpotCheckPlanHandler.modifyTasks(planIdStr, token);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e){
            LogUtils.error("治安巡查-任务设置批量启用禁用功能异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"治安巡查-任务设置批量启用禁用功能异常");
        }
    }
}
