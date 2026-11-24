package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterProtectSh;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.dto.build.SafetyBuildPowerDTO;
import com.yutong.safety.core.handler.build.SafetyBuildPowerHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Api(tags = "楼宇门禁-保密负责人及责任部门管理")
@RestController
@RequestMapping("/web/safety/buildPower")
public class SafetyBuildPowerController {

    @Autowired
    SafetyBuildPowerHandler safetyBuildPowerHandler;

    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/list", method = {RequestMethod.POST})
    public Object list(SafetyBuildPowerDO param) {
        return safetyBuildPowerHandler.pageList(param);
    }

    @ApiOperation(value = "数据列表查询接口-带绝密信息")
    @RequestMapping(value="/listWithSecret", method = {RequestMethod.POST})
    public Object listWithSecret(String deviceCode) {
        return safetyBuildPowerHandler.listWithSecret(deviceCode);
    }

    @ApiOperation(value = "数据列表查询接口(批量)-带绝密信息")
    @RequestMapping(value="/listWithSecretBatch", method = {RequestMethod.POST})
    public Object listWithSecretBatch(String deviceCodes) {
        return safetyBuildPowerHandler.listWithSecretBatch(deviceCodes);
    }

    @ApiOperation(value = "反查责任人信息")
    @RequestMapping(value="/getPeopleList", method = {RequestMethod.POST})
    public Object getPeopleList(SafetyBuildPowerDTO param) {
        return safetyBuildPowerHandler.getPeopleList(param);
    }

    @ApiOperation(value = "根据条件导出")
    @RequestMapping("/exportData")
    public Object exportData(SafetyBuildPowerDO en, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("保密负责人及责任部门管理信息表"+ DateUtils.format(new Date(),5) + ".xlsx").getBytes(), "ISO8859-1");

            // 设置响应头
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            safetyBuildPowerHandler.exportExcel(en, response.getOutputStream());
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出保密负责人及责任部门管理信息表异常", e);
        }
        return null;
    }

    @ApiOperation(value = "更新单个")
    @RequestMapping(value="/updateData", method = {RequestMethod.POST})
    public Object updateData(SafetyBuildPowerDO en) {
        safetyBuildPowerHandler.updateData(en);
        return new VO(ResponseEnum.SUCCESS.getCode(), "修改成功", "");
    }

    /**
     * 数据删除操作
     *
     * @param empNo        员工号
     * @param dutyDeptCode 责任部门
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object delete(String empNo, String dutyDeptCode) {
        return safetyBuildPowerHandler.delete(empNo, dutyDeptCode);
    }
}
