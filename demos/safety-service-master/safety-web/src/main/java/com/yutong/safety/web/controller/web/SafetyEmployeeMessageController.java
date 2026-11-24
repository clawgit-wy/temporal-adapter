package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.common.param.web.EmployeeMessageUpdateParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
 * @author gwcuiyx
 * @date 2024-4-2 17:16
 */
@Api(tags = "员工信息台账")
@RestController
@RequestMapping("/web/safety/employee")
public class SafetyEmployeeMessageController {
    @Autowired
    private SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;

    /**
     * 分页查询功能
     * @param param
     * @param tocken
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(EmployeeMessageListParam param, @RequestHeader("X-Access-Token") String tocken) {
        param.setPageFlag(true);
        return safetyEmployeeMessageHandler.pageList(param);
    }

    /**
     * 分页查询功能
     * @param param
     * @param tocken
     * @return
     */
    @ApiOperation(value = "不分页数据列表查询接口")
    @RequestMapping("/listForTab")
    public Object listForTab(EmployeeMessageListParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyEmployeeMessageHandler.pageList(param);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public void export(EmployeeMessageListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("员工信息台账列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyEmployeeMessageHandler.export(param, token);
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
            LogUtils.error("导出员工信息台账列表异常", e);
        }
    }

    /**
     *
     * @param param
     * @return
     */
    @ApiOperation(value = "批量修改备注")
    @RequestMapping("/batchUpdateRemark")
    public Object batchUpdateRemark(EmployeeMessageUpdateParam param) {
        safetyEmployeeMessageHandler.batchUpdateRemark(param);
        return null;
    }

    /**
     *
     * @param workNo
     * @return
     */
    @ApiOperation(value = "根据员工号获取员工信息")
    @RequestMapping(value = "/getEmployeeByWorkNo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEmployeeByWorkNo(String workNo,String phone){
        if(StringUtils.isBlank(workNo) && StringUtils.isBlank(phone)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号或手机号不能为空");
        }
        return safetyEmployeeMessageHandler.getEmployeeByWorkNo(workNo,phone);
    }

    @ApiOperation("查询人员信息-不分页")
    @RequestMapping(value = "/getUserListByCondition", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getUserListByCondition(EmployeeMessageListParam param) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyEmployeeMessageHandler.getUserListByCondition(param));
    }

    @ApiOperation("查询人员信息-只查询子组织下")
    @RequestMapping(value = "/getUserListByConditionContain", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getUserListByConditionContain(EmployeeMessageListParam param,@RequestHeader("X-Access-Token") String token) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyEmployeeMessageHandler.getUserListByConditionContain(param,token));
    }

    @ApiOperation("筛选出当前登录人管辖下的人员")
    @RequestMapping("/classPeople")
    public Object classPeople(String userCodeList, @RequestHeader("X-Access-Token") String token) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyEmployeeMessageHandler.classPeople(userCodeList,token));
    }

    @ApiOperation("根据员工号匹配人员信息")
    @RequestMapping("/classPeopleForAll")
    public Object classPeopleForAll(String userCodeList, @RequestHeader("X-Access-Token") String token) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyEmployeeMessageHandler.classPeopleForAll(userCodeList,token));
    }

    @ApiOperation(value = "下拉框")
    @RequestMapping(value = "/dropDownBox", method = {RequestMethod.GET})
    public VO dropDownBox() {
        return safetyEmployeeMessageHandler.dropDownBox();
    }

    @ApiOperation(value = "下拉框数据更新")
    @RequestMapping(value = "/saveDropDownBox", method = {RequestMethod.POST})
    public VO saveDropDownBox(@RequestHeader("X-Access-Token") String token) {
        return safetyEmployeeMessageHandler.saveDropDownBox(token);
    }
}
