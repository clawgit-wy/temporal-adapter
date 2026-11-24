package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.BlackMessageAddParam;
import com.yutong.safety.common.param.web.BlackMessageListParam;
import com.yutong.safety.common.param.web.BlackMessageUpdateParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyBlackMessageHandler;
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
 *  create gwcuiyx 2023.07.10
 *  黑名单信息管理--controller
 */
@Api(tags = "黑名单信息管理")
@RestController
@RequestMapping("/web/safety/black")
public class SafetyBlackMessageController {

    @Autowired
    private SafetyBlackMessageHandler safetyBlackMessageHandler;

    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(BlackMessageListParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyBlackMessageHandler.pageList(param);
    }

    @ApiOperation(value = "用工类型下拉框")
    @GetMapping("/querySelect")
    public VO querySelect() {
        return safetyBlackMessageHandler.querySelect();
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping("/delete")
    public Object delete(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        return safetyBlackMessageHandler.delete(id, tocken);
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping("/update")
    public Object update(BlackMessageUpdateParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyBlackMessageHandler.update(param, tocken);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/add")
    public Object add(BlackMessageAddParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyBlackMessageHandler.add(param, tocken);
    }

    /**
     * 数据展示操作
     * @param id
     * @return
     */
    @ApiOperation(value = "详情接口")
    @RequestMapping("/view")
    public Object view(Integer id) {
        return safetyBlackMessageHandler.view(id);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(BlackMessageListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("黑名单信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyBlackMessageHandler.export(param, tocken);
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
            LogUtils.error("导出黑名单信息列表异常", e);
        }
        return null;
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importBlackMessage(CommonImportParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyBlackMessageHandler.importBlackMessage(param, tocken);
    }

    @ApiOperation(value = "黑名单导入模板-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "黑名单导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/blackDownload.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手工执行
     * @param isAll
     * @param dateTime
     * @return
     */
    @RequestMapping("/tasksWebMessage")
    public Object tasksWebMessage(Boolean isAll, String dateTime) {
        isAll = false;
        return safetyBlackMessageHandler.tasksMessage(isAll, dateTime);
    }

    @ApiOperation("批量查询车辆是否在黑名单中")
    @RequestMapping("/isBlackBatchQuery")
    public Object isBlackBatchQuery(String carNoList) {
        return new VO(ResponseEnum.SUCCESS.getCode(), "", safetyBlackMessageHandler.isBlackBatchQuery(carNoList));
    }
}
