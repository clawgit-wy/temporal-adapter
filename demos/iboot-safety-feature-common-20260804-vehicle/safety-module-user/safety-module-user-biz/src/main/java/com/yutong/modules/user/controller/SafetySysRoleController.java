package com.yutong.modules.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yutong.modules.user.dto.SafetySysRoleDTO;
import com.yutong.modules.user.dto.SetRolePermDTO;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.query.SafetySysRoleQuery;
import com.yutong.modules.user.api.ISafetySysRoleService;
import com.yutong.modules.user.vo.RolePermConfigVO;
import com.yutong.modules.user.vo.SafetySysRoleOptionListVO;
import com.yutong.modules.user.vo.SafetySysRoleVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.ColumnPermission;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="系统角色")
@RestController
@RequestMapping("/sys/role")
public class SafetySysRoleController extends JeecgController<SafetySysRole, ISafetySysRoleService> {
	@Autowired
	private ISafetySysRoleService safetySysRoleService;

	/**
	 * 分页列表查询
	 *
	 * @param safetySysRoleQuery 角色查询条件
	 * @param pageNo           页码
	 * @param pageSize         每页大小
	 * @param req              HttpServletRequest
	 * @return 分页结果
	 */
    @ColumnPermission(permCode="systemd:roles")
    @RequiresPermissions("systemd:roles")
	@ApiOperation(value="系统角色 - 分页列表查询", notes="系统角色 - 分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SafetySysRoleQuery safetySysRoleQuery,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        IPage<SafetySysRoleVO> voiPage = safetySysRoleService.queryPageList(safetySysRoleQuery, pageNo, pageSize);
        return Result.OK(voiPage);
	}

	/**
	 * 添加
	 *
	 * @param safetySysRoleDTO 角色信息
	 * @return 添加结果
	 */
    @RequiresPermissions("systemd:roles:add")
	@AutoLog(value = "系统角色 - 添加")
	@ApiOperation(value="系统角色 - 添加", notes="系统角色 - 添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody @Valid SafetySysRoleDTO safetySysRoleDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean success = safetySysRoleService.save(safetySysRoleDTO);
        return success ? Result.OK("添加成功！") : Result.error("添加失败");
	}

	/**
	 * 编辑
	 *
	 * @param safetySysRoleDTO 角色信息
	 * @return 编辑结果
	 */
    @RequiresPermissions("systemd:roles:edit")
	@AutoLog(value = "系统角色 - 编辑", operateType = 3)
	@ApiOperation(value="系统角色 - 编辑", notes="系统角色 - 编辑")
	@RequestMapping(value = "/update", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> update(@RequestBody @Valid SafetySysRoleDTO safetySysRoleDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean success = safetySysRoleService.update(safetySysRoleDTO);
        return success ? Result.OK("编辑成功!") : Result.error("数据不存在！");
	}

	/**
	 * 通过 id 删除
	 *
	 * @param id 角色 id
	 * @return 删除结果
	 */
    @RequiresPermissions("systemd:roles:delete")
	@AutoLog(value = "系统角色 - 通过 id 删除")
	@ApiOperation(value="系统角色 - 通过 id 删除", notes="系统角色 - 通过 id 删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Long id) {

        boolean success = safetySysRoleService.delete(id);
        return success ? Result.OK("删除成功!") : Result.error("数据不存在!");
	}


    /**
     * 导出 excel
     *
     * @param request  HttpServletRequest
     * @param safetySysRoleQuery 角色查询条件
     * @return 导出结果
     */
    @ColumnPermission(permCode="systemd:roles")
    @RequiresPermissions("systemd:roles:export")
    @AutoLog(value = "系统角色 - 导出")
    @ApiOperation(value="系统角色 - 导出", notes="系统角色 - 导出")
    @GetMapping(value = "/export")
    public ModelAndView export(HttpServletRequest request, SafetySysRoleQuery safetySysRoleQuery) {
        List<SafetySysRoleVO> list = safetySysRoleService.queryAllList(safetySysRoleQuery);

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "系统角色");
        mv.addObject(NormalExcelConstants.CLASS, SafetySysRoleVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("系统角色报表", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "系统角色");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 设置角色权限
     *
     * @param setRolePermDTO 角色授权DTO
     * @return 设置结果
     */
    @RequiresPermissions("systemd:roles:auth")
    @AutoLog(value = "系统角色 - 设置权限",operateType = 3)
    @ApiOperation(value="系统角色 - 设置权限", notes="系统角色 - 设置权限")
    @PostMapping(value = "/setRolePerms")
    public Result<?> setRolePerms(@RequestBody @Valid SetRolePermDTO setRolePermDTO) {
        boolean success = safetySysRoleService.setRolePerms(setRolePermDTO);
        return success ? Result.OK("权限设置成功！") : Result.error("权限设置失败");
    }

    /**
     * 查询角色权限配置（菜单树 + 已勾选的权限ID）
     */
    @ApiOperation(value="系统角色 - 权限树", notes="系统角色 - 权限树")
    @RequestMapping(value = "/queryRolePerms", method = RequestMethod.GET)
    public Result<RolePermConfigVO> queryRolePerms(@RequestParam(name = "roleId", required = true) Long roleId) {
        RolePermConfigVO rolePerms = safetySysRoleService.queryRolePerms(roleId);
        return Result.OK(rolePerms);
    }

    /**
     * 获取角色下拉框列表
     *
     * @return 角色列表
     */
    @ApiOperation(value="系统角色 - 获取角色下拉框列表", notes="系统角色 - 获取角色下拉框列表")
    @GetMapping(value = "/optionList")
    public Result<?> optionlist() {
        List<SafetySysRoleOptionListVO> list = safetySysRoleService.optionlist();
        return Result.OK(list);
    }

    /**
     * 批量补全角色编码
     */
    @AutoLog(value = "系统角色 - 批量补全角色编码",operateType = 3)
    @ApiOperation(value="系统角色 - 批量补全角色编码（初始化后可删除接口）", notes="查询角色编码为空的记录，生成并保存")
    @PostMapping(value = "/batchFillRoleCodes")
    public Result<?> batchFillRoleCodes() {
        int count = safetySysRoleService.batchFillRoleCodes();
        return Result.OK("成功补全 " + count + " 条角色编码");
    }

}
