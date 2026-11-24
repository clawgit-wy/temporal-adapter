package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.UserMessageAddParam;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.param.web.UserMessageUpdateParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyUserMessageHandler;
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
 *  create gwcuiyx 2023.07.13
 *  用户管理--controller
 */
//@Api(tags = "用户管理")
@RestController
//@RequestMapping("/web/safety/useraaaa")
public class SafetyUserMessageController {
    @Autowired
    private SafetyUserMessageHandler safetyUserMessageHandler;

    /**
     * 用户管理数据列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表接口")
    @RequestMapping("/list")
    public Object list(UserMessageListParam param) {
        return safetyUserMessageHandler.pageList(param);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(UserMessageListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("用户信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyUserMessageHandler.export(param, tocken);
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
            LogUtils.error("导出用户信息列表异常", e);
        }
        return null;
    }

    /**
     * 添加功能
     * @param param
     * @return
     */
    @ApiOperation(value = "数据添加接口")
    @RequestMapping("/add")
    public Object add(UserMessageAddParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyUserMessageHandler.add(param, tocken);
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新接口")
    @RequestMapping("/update")
    public Object update(UserMessageUpdateParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyUserMessageHandler.update(param, tocken);
    }

    /**
     * 数据展示view
     * @param id
     * @return
     */
    @ApiOperation(value = "数据详情接口")
    @RequestMapping("/info")
    public Object info(Integer id) {
        return safetyUserMessageHandler.info(id);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "数据删除接口")
    @RequestMapping("/delete")
    public Object delete(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        return safetyUserMessageHandler.delete(id, tocken);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importUserMessage(CommonImportParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyUserMessageHandler.importUserMessage(param, tocken);
    }
}
