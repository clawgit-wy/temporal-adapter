package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.spot.SpotCheckBatchDeleteParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleListParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleRequireParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleSaveParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.spot.SafetySpotCheckRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author gwcuiyx
 * @date 2024-4-30 8:54
 */
@Api(tags = "设备设施点检-设备巡检标准配置")
@RestController
@RequestMapping("/web/safety/spot/rule")
public class SafetySpotCheckRuleController {

    @Autowired
    private SafetySpotCheckRuleHandler safetySpotCheckRuleHandler;

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/listPage")
    public Object listPage(SpotCheckRuleListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckRuleHandler.pageList(param);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(SpotCheckRuleListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("设备设施点检-设备巡检标准配置列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySpotCheckRuleHandler.export(param, token);
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
     * 批量删除操作
     * @param param
     * @return
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping("/batchDelete")
    public Object batchDelete(SpotCheckBatchDeleteParam param, @RequestHeader("X-Access-Token") String token) {
        safetySpotCheckRuleHandler.batchDelete(param, token);
        return null;
    }

    /**
     * 模板下载接口
     * @param response
     */
    @ApiOperation(value = "设备巡检标准配置-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "设备设施点检-设备巡检标准配置导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/spotCheckRuleDownload.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping("/delete")
    public Object delete(Integer id, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckRuleHandler.delete(id, token);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/add")
    public Object add(SpotCheckRuleSaveParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckRuleHandler.add(param, token);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/update")
    public Object update(SpotCheckRuleSaveParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckRuleHandler.add(param, token);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importBlackMessage(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckRuleHandler.importSpotCheckRule(param, token);
    }

    @ApiOperation(value = "单独更新选项接口")
    @RequestMapping("/updateRequire")
    public Object updateRequire(SpotCheckRuleRequireParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckRuleHandler.updateRequire(param, token);
    }

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "不分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(SpotCheckRuleListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckRuleHandler.list(param);
    }

}
