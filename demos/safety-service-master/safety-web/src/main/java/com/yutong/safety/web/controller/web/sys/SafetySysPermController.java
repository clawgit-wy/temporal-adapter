package com.yutong.safety.web.controller.web.sys;

import com.yutong.safety.common.param.web.sys.SysPermParam;
import com.yutong.safety.core.handler.SafetySysPermHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * create gwcuiyx 2023.07.26
 * 权限资源表--controller
 */
@Api(tags = "权限资源表")
@RestController
@RequestMapping("/web/safety/perm")
public class SafetySysPermController {
    @Autowired
    private SafetySysPermHandler safetySysPermHandler;

    /**
     * 查询权限资源列表
     * @return
     */
    @ApiOperation(value = "查询权限资源列表")
    @RequestMapping("/selectPermList")
    @ResponseBody
    public Object selectPermList() {
        return safetySysPermHandler.selectPermList();
    }

    /**
     * 权限资源添加
     * @param param
     * @return
     */
    @ApiOperation(value = "权限资源添加")
    @RequestMapping("/addSysPerm")
    @ResponseBody
    public Object addSysPerm(SysPermParam param) {
        safetySysPermHandler.insertSysPerm(param);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "权限资源更新")
    @RequestMapping("/updateSysPerm")
    @ResponseBody
    public Object updateSysPerm(SysPermParam param) {
        safetySysPermHandler.updateSysPerm(param);
        return null;
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "权限资源删除")
    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteSysPerm(Integer id) {
        safetySysPermHandler.deleteSysPerm(id);
        return null;
    }

}
