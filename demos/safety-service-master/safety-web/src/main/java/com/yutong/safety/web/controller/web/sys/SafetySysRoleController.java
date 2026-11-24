package com.yutong.safety.web.controller.web.sys;

import com.yutong.safety.common.param.web.sys.SysRoleListParam;
import com.yutong.safety.common.param.web.sys.SysRoleParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetySysRoleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * create gwcuiyx 2023.07.27
 * 角色列表--controller
 */
@Api(tags = "角色列表功能")
@RestController
@RequestMapping("/web/safety/role")
public class SafetySysRoleController {
    @Autowired
    private SafetySysRoleHandler safetySysRoleHandler;

    /**
     * 数据分页列表
     * @param param
     * @return
     */
    @ApiOperation(value = "查询角色列表")
    @RequestMapping("/selectRoleList")
    @ResponseBody
    public Object selectRoleList(SysRoleListParam param) {
        return safetySysRoleHandler.selectRoleList(param);
    }

    /**
     * 数据新增操作
     * @param param
     * @return
     */
    @ApiOperation(value = "角色新增操作")
    @RequestMapping("/insertSysRole")
    @ResponseBody
    public Object insertSysRole(SysRoleParam param, @RequestHeader("X-Access-Token") String tocken) {
        safetySysRoleHandler.insertSysRole(param, tocken);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "角色更新操作")
    @RequestMapping("/updateSysRole")
    @ResponseBody
    public Object updateSysRole(SysRoleParam param, @RequestHeader("X-Access-Token") String tocken) {
        safetySysRoleHandler.updateSysRole(param, tocken);
        return null;
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "角色删除操作")
    @RequestMapping("/deleteSysRole")
    @ResponseBody
    public Object deleteSysRole(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        safetySysRoleHandler.deleteSysRoleById(id, tocken);
        return null;
    }

    /**
     * 数据获取操作
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取角色资源")
    @RequestMapping("/getRoleHasPerms")
    @ResponseBody
    public Object getRoleHasPerms(Integer id) {
        return safetySysRoleHandler.getRoleHasPerms(id);
    }

    /**
     * 设置权限操作
     * @param roleId
     * @param permIds
     * @return
     */
    @ApiOperation(value = "设置权限操作")
    @RequestMapping("/setRolePerms")
    @ResponseBody
    public Object setRolePerms(Integer roleId, String permIds) {
        return safetySysRoleHandler.setRolePerms(roleId, permIds);
    }

    /**
     * 角色下拉皆苦
     * @return
     */
    @ApiOperation(value = "角色下拉数据")
    @RequestMapping("/selectSysRoles")
    public Object selectSysRoles() {
        return safetySysRoleHandler.selectSysRoles();
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public void export(SysRoleListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("角色管理列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySysRoleHandler.export(param, tocken);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            LogUtils.error("导出角色管理列表异常", e);
        }
    }

}
