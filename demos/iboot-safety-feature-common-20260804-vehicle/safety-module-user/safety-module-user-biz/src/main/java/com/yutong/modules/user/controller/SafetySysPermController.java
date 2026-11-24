package com.yutong.modules.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.user.convert.SysPermConvert;
import com.yutong.modules.user.dto.SafetySysPermDTO;
import com.yutong.modules.user.api.ISafetySysRoleService;
import com.yutong.modules.user.api.SafetySysPermService;
import com.yutong.modules.user.entity.SafetySysPerm;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.PublicClass;
import com.yutong.modules.user.vo.SafetySysPermVO;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.aspect.annotation.AutoLog;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="菜单")
@RestController
@RequestMapping("/sys/safetySysPerm")
public class SafetySysPermController {

	@Autowired
	private SafetySysPermService safetySysPermService;
    @Autowired
    private SysPermConvert sysPermConvert;
    @Autowired
    private ISafetySysRoleService safetySysRoleService;

    @RequiresPermissions("systemd:menu:query")
    @ApiOperation(value = "菜单列表", notes = "菜单列表")
    @PostMapping(value = "/list")
    public Result<List<MenuTree>> list(@RequestBody SafetySysPermDTO permDto) {
        Result<List<MenuTree>> result = new Result<>();
       try {
           List<MenuTree> menuTreeList = safetySysPermService.getMenuTreeList(permDto);
           result.setResult(menuTreeList);
           result.setSuccess(true);
           result.setCode(200);
       }catch (Exception e){
           log.error(e.getMessage(), e);
           result.error500(e.getMessage());
       }
        return result;
    }

    @ApiOperation(value = "流程显示菜单列表", notes = "流程显示菜单列表")
    @PostMapping(value = "/processMenuList")
    public Result<List<SafetySysPermVO>> list() {
        Result<List<SafetySysPermVO>> result = new Result<>();
        try {
            List<SafetySysPerm> menuTreeList = safetySysPermService.list(
                    new LambdaQueryWrapper<SafetySysPerm>()
                            .eq(SafetySysPerm::getProcessDisplay, "1")
                            .eq(SafetySysPerm::getDeleteFalg, "0")
                            .select(SafetySysPerm::getId, SafetySysPerm::getPermName));

            result.setResult(sysPermConvert.toVOList(menuTreeList));
            result.setSuccess(true);
            result.setCode(200);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

     /**
      * 添加菜单
      * @param
      * @return
      */
     @RequiresPermissions(value = {"systemd:menu:add", "systemd:menu:edit"}, logical = Logical.OR)
     @AutoLog(value = "添加/编辑菜单",operateType = 3)
     @ApiOperation(value = "添加/编辑菜单", notes = "添加/编辑菜单")
     @PostMapping(value = "/saveOrUpdatePerm")
     public Result<SafetySysPermDTO> saveOrUpdatePerm(@RequestBody SafetySysPermDTO permDto) {
         Result<SafetySysPermDTO> result = new Result<SafetySysPermDTO>();
         try {
             safetySysPermService.saveOrUpdatePerm(permDto);
             result.setSuccess(true);
             result.setCode(200);
         } catch (Exception e) {
             log.error(e.getMessage(), e);
             result.error500(e.getMessage());
         }
         return result;
     }



     /**
      * 删除菜单
      * @param ids
      * @return
      */
     @RequiresPermissions("systemd:menu:del")
     @AutoLog(value = "删除菜单")
     @ApiOperation(value = "删除菜单", notes = "删除菜单")
     @DeleteMapping(value = "/delete")
     public Result<SafetySysPermDTO> delete(@RequestParam(name = "ids", required = true) String ids) {
         Result<SafetySysPermDTO> result = new Result<>();
         try {
             String[] arr = ids.split(",");
             for (String id : arr){
                 if (oConvertUtils.isNotEmpty(id)) {
                     try {
                         safetySysPermService.deletePermission(id);
                     } catch (JeecgBootException e) {
                         if(e.getMessage()!=null && e.getMessage().contains("未找到菜单信息")){
                             log.warn(e.getMessage());
                         }
                     }
                 }
             }
             result.setSuccess(true);
             result.setCode(200);
         } catch (Exception e) {
             log.error(e.getMessage(), e);
             result.error500(e.getMessage());
         }
         return result;
     }


    /**
     * 查询菜单
     * @param
     * @return
     */
    @ApiOperation(value = "菜单详情", notes = "菜单详情")
    @GetMapping(value = "/getSystemMenuDetail")
    public Result<List<PublicClass>> getSystemMenuDetail(@RequestParam("id") String id){
        Result<List<PublicClass>> result = new Result<>();
        try{
            List<PublicClass> classes = safetySysPermService.getMenuClassInfo(id);
            result.setResult(classes);
            result.setSuccess(true);
            result.setCode(200);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 查询菜单路径是否已存在
     * @param
     * @return
     */
    @ApiOperation(value = "查询菜单路径是否已存在", notes = "查询菜单路径是否已存在")
    @GetMapping(value = "/getPermUrlIsExist")
    public Result<Boolean> getPermUrlIsExist(@RequestParam("permUrl") String permUrl,@RequestParam("id") String id){
        Result<Boolean> result = new Result<>();
        try{
            boolean isExist =  safetySysPermService.getPermUrlIsExist(permUrl,id);
            result.setResult(isExist);
            result.setSuccess(true);
            result.setCode(200);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

}
