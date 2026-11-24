package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingReportSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterStatisticSh;
import com.yutong.safety.core.handler.enter.SafetyCrkCarHistoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * 车辆备案
 * */
@Api(tags = "安保入厂-备案物流车入厂分析")
@RestController
@RequestMapping("/web/enter/filingReport/")
public class SafetyEnterFilingReportController {

    @Autowired
    private SafetyCrkCarHistoryHandler safetyCrkCarHistoryHandler;

    @ApiOperation(value = "列表查询")
    @RequestMapping(value = "/reportPageList" , method = {RequestMethod.GET,RequestMethod.POST})
    public Object reportPageList(SafetyEnterFilingReportSh reportSh,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyCrkCarHistoryHandler.reportPageList(reportSh,token);
    }

    @ApiOperation(value = "导出")
    @RequestMapping(value = "/reportExport", method = {RequestMethod.GET,RequestMethod.POST})
    public Object reportExport(HttpServletResponse response,SafetyEnterFilingReportSh reportSh,
                         @RequestHeader("X-Access-Token") String token){
        try {
            String fileName = new String(("备案物流车进厂分析报表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyCrkCarHistoryHandler.reportExport(reportSh, token);
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
            LogUtils.error("导出备案物流车进厂分析报表异常", e);
            return new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "备案车辆信息统计")
    @RequestMapping(value = "/enterStatistic", method = {RequestMethod.GET,RequestMethod.POST})
    public Object enterStatistic(SafetyEnterStatisticSh statisticSh,
                                 @RequestHeader("X-Access-Token") String token){
        try{
            return safetyCrkCarHistoryHandler.enterStatistic(statisticSh,token);
        }catch (BizException ex){
            LogUtils.error("备案车辆信息统计异常", ex.getMsg());
            throw new BizException(ex.getCode(), ex.getMsg());
        }catch (Exception e){
            LogUtils.error("备案车辆信息统计异常", e);
            return new BizException(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }
}
