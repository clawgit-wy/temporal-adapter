package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.dto.build.SafetyBuildIssuedLogDTO;
import com.yutong.safety.core.handler.build.SafetyBuildIssuedLogHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Api(tags = "楼宇门禁-门禁授权日志")
@RestController
@RequestMapping("/web/build/safety/isuuedLog")
public class SafetyBuildIssuedLogController {
    @Autowired
    SafetyBuildIssuedLogHandler safetyBuildIssuedLogHandler;

    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public Object list(SafetyBuildIssuedLogDTO param) {
        return safetyBuildIssuedLogHandler.pageList(param);
    }

    @ApiOperation(value = "根据条件导出")
    @RequestMapping("/exportData")
    public Object exportData(SafetyBuildIssuedLogDTO en, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("门禁授权日志表" + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");

            // 设置响应头
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            safetyBuildIssuedLogHandler.exportExcel(en, response.getOutputStream());
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出门禁授权日志表异常", e);
        }
        return null;
    }

    @ApiOperation(value = "手动下发")
    @RequestMapping(value = "/downBySelf", method = {RequestMethod.POST})
    public Object downBySelf(@RequestHeader("X-Access-Token") String tocken) {
        safetyBuildIssuedLogHandler.downBySelf(tocken);
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), "手动下发成功");
    }
}
