package com.yutong.safety.web.controller.web.sys;

import cn.hutool.log.Log;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.UserMessageAddParam;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.param.web.UserMessageUpdateParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetySysUserHandler;
import com.yutong.safety.core.handler.login.LoginHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
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

import static org.reflections.Reflections.log;

/**
 * create gwcuiyx 2023.07.28
 * 账号信息表-controller
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/web/safety/user")
public class SafetySysUserController {
    @Autowired
    private SafetySysUserHandler safetySysUserHandler;

    @Autowired
    private LoginHandler loginHandler;

    @Autowired
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    /**
     * 数据列表
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    @ResponseBody
    public Object pagetList(UserMessageListParam param) {
        return safetySysUserHandler.pageList(param);
    }

    /**
     * 数据列表
     * @param param
     * @return
     */
    @ApiOperation(value = "查询用户接口-不分页")
    @RequestMapping(value = "/getUserList",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object getUserList(UserMessageListParam param) {
        return safetySysUserHandler.getList(param);
    }


    /**
     * 数据增加
     * @param param
     * @return
     */
    @ApiOperation(value = "数据新增操作")
    @RequestMapping("/insertSysUser")
    @ResponseBody
    public Object insertSysUser(UserMessageAddParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetySysUserHandler.insertSysUser(param, tocken);
    }

    /**
     * 数据更新
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新操作")
    @RequestMapping("/updateSysUserById")
    @ResponseBody
    public Object updateSysUserById(UserMessageUpdateParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetySysUserHandler.updateSysUserById(param, tocken);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "数据删除操作")
    @RequestMapping("/deleteSysUserById")
    @ResponseBody
    public Object deleteSysUserById(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        return safetySysUserHandler.deleteSysUserById(id, tocken);
    }

    /**
     * 设置角色
     * @param userId
     * @param roleIds
     * @return
     */
    @ApiOperation(value = "设置角色")
    @RequestMapping("setUserRoles")
    @ResponseBody
    public Object setUserRoles(Integer userId, String roleIds) {
        return safetySysUserHandler.setUserRoles(userId, roleIds);
    }

    /**
     * 查询已设置的角色集合
     * @param id
     * @return
     */
    @ApiOperation(value = "查询已设置的角色集合")
    @RequestMapping("getUserHasRoles")
    @ResponseBody
    public Object getUserHasRoles(Integer id) {
        return safetySysUserHandler.getUserHasRoles(id);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importSysUser(CommonImportParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetySysUserHandler.importSysUser(param, tocken);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public void export(UserMessageListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("用户信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySysUserHandler.export(param, tocken);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            LogUtils.error("导出用户信息列表异常", e);
        }
    }

    @ApiOperation(value = "用户管理导入模板-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "用户管理导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/userDownload.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            LogUtils.error("用户管理导入异常",e);
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getRoleListByEmpNo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getRoleListByEmpNo(String empNo) {
        if (StringUtils.isEmpty(empNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空!");
        }
        String newWorkNo = empNo;
        if(empNo.startsWith("01")){
            newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(empNo);
        }
        return loginHandler.getRoleList(newWorkNo);
    }
}
