package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.handler.spot.SafetySpotCheckRecordHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckResultHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author gwxiexj
 * @date 2024-05-15 13:41
 */
@Api(tags = "设备设施点检-设备点检记录")
@RestController
@RequestMapping("/web/safety/spot/record")
public class SafetySpotCheckRecordController {
    @Autowired
    private SafetySpotCheckRecordHandler safetySpotCheckRecordHandler;

    @Autowired
    private SafetySpotCheckResultHandler safetySpotCheckResultHandler;

    @Autowired
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping("/listPage")
    public Object listPage(SpotCheckRecordListParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetySpotCheckRecordHandler.pageList(param, token);
        }catch (Exception e){
            LogUtils.error("设备点检记录分页列表获取异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"设备点检记录分页列表获取异常");
        }
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param response 请求
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(SpotCheckRecordListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("设备设施点检-设备巡检日志"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySpotCheckRecordHandler.export(param, token);
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
            LogUtils.error("导出设备设施点检-设备巡检日志异常", e);
        }
        return null;
    }

    /**
     * 设备设施点检-巡查记录的整改记录明细
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "整改记录")
    @RequestMapping("/reformList")
    public Object reformList(SpotCheckResultParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetySpotCheckResultHandler.pageList(param, token);
        }catch (Exception e){
            LogUtils.error("整改记录列表获取异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"整改记录列表获取异常");
        }
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param response 请求
     */
    @ApiOperation(value = "整改记录数据导出接口")
    @RequestMapping("/exportReformList")
    public Object exportReformList(SpotCheckResultParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("设备设施点检-设备巡检整改记录"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySpotCheckResultHandler.export(param, token);
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
            LogUtils.error("导出设备设施点检-设备巡检整改记录异常", e);
        }
        return null;
    }

    /**
     * 设备设施点检-设备巡检结果
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "设备巡检结果")
    @RequestMapping("/taskReport")
    public Object taskReport(SpotCheckTaskListParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetySpotCheckTaskHandler.pageList(param, token);
        }catch (Exception e){
            LogUtils.error("设备巡检结果获取异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"设备巡检结果获取异常");
        }
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param response 请求
     */
    @ApiOperation(value = "整改记录数据导出接口")
    @RequestMapping("/exportTaskReport")
    public Object exportTaskReport(SpotCheckTaskListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("设备设施点检-设备巡检结果"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySpotCheckTaskHandler.export(param, token);
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
            LogUtils.error("导出设备设施点检-设备巡检结果异常", e);
        }
        return null;
    }

    @ApiOperation(value = "触发需求评审流程")
    @RequestMapping("/createEIPFlow")
    public Object createEIPFlow(@RequestBody ParamDTO paramDTO) {
        safetySpotCheckRecordHandler.createEIPFlow(paramDTO.getIdList(),paramDTO.getEip());
        return new VO(ResponseEnum.SUCCESS.getCode(), "创建流程成功");
    }
}
