package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.ValidateEnvironmentAddParam;
import com.yutong.safety.common.param.web.spot.ValidateEnvironmentListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.spot.SafetyValidateEnvironmentHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * 业务验证-货车环保标准抽查结果
 * @author gwxiexj
 * @since 2024-06-11 16:45
 */
@Api(tags = "业务验证-货车环保标准抽查结果")
@RestController
@RequestMapping("/web/safety/validate/environment")
public class SafetyValidateEnvironmentController {

    @Autowired
    private SafetyValidateEnvironmentHandler safetyValidateEnvironmentHandler;

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/listPage")
    public Object listPage(ValidateEnvironmentListParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetyValidateEnvironmentHandler.pageList(param,token);
        }catch (Exception e){
            LogUtils.error("分页数据列表查询接口-异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"");
        }
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(ValidateEnvironmentListParam param, HttpServletResponse response,
                         @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("业务验证-货车环保标准抽查结果"+
                    DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyValidateEnvironmentHandler.export(param, token);
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
            LogUtils.error("导出业务验证-货车环保标准抽查结果异常", e);
        }
        return null;
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/add")
    public Object add(ValidateEnvironmentAddParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            param.setEventSource(1);
            param.setBusinessType("货车环保标准抽查");
            //保存
            return safetyValidateEnvironmentHandler.save(param);
        }catch (Exception e){
            LogUtils.error("",2);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"");
        }
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/update")
    public Object update(ValidateEnvironmentAddParam param, @RequestHeader("X-Access-Token") String token) {
        param.setEventSource(1);
        param.setBusinessType("货车环保标准抽查");
        return safetyValidateEnvironmentHandler.update(param);
    }
}
