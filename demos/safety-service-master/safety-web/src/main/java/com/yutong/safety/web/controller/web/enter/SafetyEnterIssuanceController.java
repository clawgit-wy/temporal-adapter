package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterIssuanceSh;
import com.yutong.safety.core.handler.enter.SafetyEnterIssuanceHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * 车辆备案
 * */
@Api(tags = "安保入厂-权限下发日志")
@RestController
@RequestMapping("/web/enter/issuance/")
public class SafetyEnterIssuanceController {

    @Autowired
    private SafetyEnterIssuanceHandler safetyEnterIssuanceHandler;

    @ApiOperation(value = "列表查询")
    @RequestMapping(value = "/list" , method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(SafetyEnterIssuanceSh enterFactoryIssuance,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyEnterIssuanceHandler.pageList(enterFactoryIssuance,token);
    }


    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save" , method = {RequestMethod.GET,RequestMethod.POST})
    public Object save(SafetyEnterIssuanceDO enterFactoryIssuance,
                       @RequestHeader("X-Access-Token") String token) {
       return safetyEnterIssuanceHandler.save(enterFactoryIssuance,token);
    }

    @ApiOperation(value = "导出")
    @RequestMapping(value = "/export", method = {RequestMethod.GET,RequestMethod.POST})
    public Object export(HttpServletResponse response, SafetyEnterIssuanceSh enterFactoryIssuance,
                         @RequestHeader("X-Access-Token") String token){
        try {
            String fileName = new String(("物流车辆入参备案-备案下发日志"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyEnterIssuanceHandler.export(enterFactoryIssuance, token);
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
            LogUtils.error("导出物流车辆入参备案-备案下发日志异常", e);
        }
        return null;
    }
    @ApiOperation(value = "重新下发权限")
    @RequestMapping(value = "/updateIssuance" , method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateIssuance(@RequestHeader("X-Access-Token") String token){
        return safetyEnterIssuanceHandler.updateIssuance(token);
    }

}
