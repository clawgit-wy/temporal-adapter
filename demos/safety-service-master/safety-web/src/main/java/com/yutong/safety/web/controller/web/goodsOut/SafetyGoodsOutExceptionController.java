package com.yutong.safety.web.controller.web.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutExceptionDO;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutExceptionHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author caomg
 * @date 2024-9-24 14:30
 * @description SafetyGoodsOutExceptionController
 */
@Api(tags = "物资出门证-异常信息")
@RestController
@RequestMapping("/web/safety/goodsOut/catchException")
public class SafetyGoodsOutExceptionController {

    @Autowired
    SafetyGoodsOutExceptionHandler safetyGoodsOutExceptionHandler;

    @ApiOperation(value = "列表")
    @PostMapping("/list")
    public Object getList(@RequestBody SafetyGoodsOutExceptionDO en) {
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutExceptionHandler.getList(en));
    }

    @ApiOperation(value = "根据条件导出")
    @PostMapping("/exportData")
    public Object exportData(@RequestBody SafetyGoodsOutExceptionDO en, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("物资出门证异常信息表"+ DateUtils.format(new Date(),5) + ".xlsx").getBytes(), "ISO8859-1");

            // 设置响应头
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            safetyGoodsOutExceptionHandler.exportExcel(en, response.getOutputStream());
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出物资出门证异常信息异常", e);
        }
        return null;
    }

    @ApiOperation("新增")
    @PostMapping("/inser")
    public Object insert(@RequestBody SafetyGoodsOutExceptionDO en) {
        safetyGoodsOutExceptionHandler.insert(en);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @ApiOperation("更新")
    @PostMapping("/update")
    public Object update(@RequestBody SafetyGoodsOutExceptionDO en) {
        safetyGoodsOutExceptionHandler.update(en);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }
}
