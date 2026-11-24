package com.yutong.modules.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.yutong.modules.user.dto.SafetySysUserDTO;
import com.yutong.modules.user.dto.SafetySysUserImpoetDTO;
import com.yutong.modules.user.query.SafetySysUserQuery;
import com.yutong.modules.user.entity.SafetySysUser;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.SafetySysUserVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.aspect.annotation.ColumnPermission;
import org.jeecg.common.system.base.controller.JeecgController;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.ImportTemplateDownloadUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 系统用户表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="系统用户表")
@RestController
@RequestMapping("/safety/user")
public class SafetySysUserController extends JeecgController<SafetySysUser, ISafetySysUserService> {
	@Autowired
	private ISafetySysUserService safetySysUserService;

    //excel导入条数限制
    @Value("${ytsafety.excel.importlimit}")
    private Integer importlimit;

	/**
	 * 分页列表查询
	 *
	 * @param safetySysUserQueryDto
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
    @RequiresPermissions("systemd:users")
	@ApiOperation(value="系统用户表-分页列表查询", notes="系统用户表-分页列表查询")
	@GetMapping(value = "/list")
    @ColumnPermission(permCode = "systemd:users")
	public Result<?> queryPageList(SafetySysUserQuery safetySysUserQueryDto,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
		Page<SafetySysUserVO> page = new Page<SafetySysUserVO>(pageNo, pageSize);
		IPage<SafetySysUserVO> pageList = safetySysUserService.queryPageList(page, safetySysUserQueryDto);
		return Result.OK(pageList);
	}

	/**
	 * 添加
	 *
	 * @param safetySysUserDto
	 * @return
	 */
    @RequiresPermissions("systemd:users:add")
	@AutoLog(value = "系统用户表-添加",operateType = 2)
	@ApiOperation(value="系统用户表-添加", notes="系统用户表-添加")
	@PostMapping(value = "/insertSysUser")
	public Result<?> insertSysUser(@Valid @RequestBody SafetySysUserDTO safetySysUserDto) {
		safetySysUserService.addUser(safetySysUserDto);
		return Result.OK("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param
	 * @return
	 */
    @RequiresPermissions("systemd:users:edit")
	@AutoLog(value = "系统用户表-编辑",operateType = 3)
	@ApiOperation(value="系统用户表-编辑", notes="系统用户表-编辑")
	@RequestMapping(value = "/updateSysUserById", method = {RequestMethod.POST})
	public Result<?> updateSysUserById(@Valid @RequestBody SafetySysUserDTO dto) {
		safetySysUserService.updateSysUserById(dto);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
    @RequiresPermissions("systemd:users:delete")
    @AutoLog(value = "系统用户表-通过id删除")
	@ApiOperation(value="系统用户表-通过id删除", notes="系统用户表-通过id删除")
	@DeleteMapping(value = "/deleteSysUserById")
	public Result<?> deleteSysUserById(@RequestParam(name="id",required=true) String id) {
		safetySysUserService.deleteById(id);
		return Result.OK("删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="系统用户表-通过id查询", notes="系统用户表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=false) Long id) {
		SafetySysUserVO safetySysUser = safetySysUserService.queryById(id);
		return Result.OK(safetySysUser);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param safetySysUserQueryDto
   */
  @RequiresPermissions("systemd:users:export")
  @AutoLog(value = "系统用户表-导出excel")
  @ApiOperation(value="系统用户表-导出excel", notes="系统用户表-通过id查询")
  @GetMapping(value = "/exportXls")
  @ColumnPermission(permCode = "systemd:users")
  public ModelAndView exportXls(HttpServletRequest request, SafetySysUserQuery safetySysUserQueryDto) {
      List<SafetySysUserVO> list = safetySysUserService.queryList(safetySysUserQueryDto);

      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "系统用户");
      mv.addObject(NormalExcelConstants.CLASS, SafetySysUserVO.class);

      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
      ExportParams exportParams = new ExportParams("系统用户", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "系统用户");
      mv.addObject(NormalExcelConstants.PARAMS, exportParams);
      mv.addObject(NormalExcelConstants.DATA_LIST, list);

      // 3. 输出下载
      return mv;
  }

    /**
     * 下载导入模板
     */
    @ApiOperation(value="系统用户表-下载导入模板", notes="下载导入模板")
    @GetMapping(value = "/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        String[] headers = {"员工名", "员工号*", "授权厂区", "角色*", "部门"};
        String instructions = "1、多个角色用英文逗号隔开\n" +
                "2、多级部门用“_”隔开，多个授权部门用英文“,”隔开\n" +
                "3、多个授权厂区用英文逗号隔开\n" +
                "4、表*的为必填字段";
        java.util.List<String> examples = java.util.Arrays.asList(
                "示例：应新宇",
                "示例：91062998",
                "示例：一工厂（十八里河）,二工厂（新能源）",
                "示例：超级管理员",
                "示例：客车板块_宇通客车股份有限公司_人力资源体系_司法保卫室,客车板块_宇通客车股份有限公司_人力资源体系_司法保卫室_消防安全科,工程机械板块_宇通重工股份有限公司_综合管理室_司法保卫模块"
        );
        ImportTemplateDownloadUtil.buildImportTemplate("系统用户导入模版", "系统用户信息", headers, instructions, examples, response);
    }

  /**
   * 通过excel导入数据
   *
   * @param
   * @param
   * @return
   */
  @RequiresPermissions("systemd:users:import")
  @AutoLog(value = "系统用户表-通过excel导入用户数据")
  @ApiOperation(value="系统用户表--通过excel导入用户数据")
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public ModelAndView importExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {

      try {
          String fileName = file.getOriginalFilename();
          if (fileName != null && (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
              throw new RuntimeException("只能导入Excel文件");
          }
          if (file.isEmpty())throw new RuntimeException("不能上传空文件");
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(false);
          List<SafetySysUserImpoetDTO> safetySysUserDtos = ExcelImportUtil.importExcel(file.getInputStream(), SafetySysUserImpoetDTO.class, params);
          List<SafetySysUserImpoetDTO> safetySysUserDtoList = safetySysUserDtos.stream().filter(BeanUtil::isNotEmpty).collect(Collectors.toList());
          if (safetySysUserDtoList.isEmpty()) {
              throw new RuntimeException("导入内容为空");
          }
          if (safetySysUserDtoList.size() > importlimit) {
              throw new RuntimeException("最多导入"+importlimit+"条数据！");

          }
          List<SafetySysUserImpoetDTO> maps = safetySysUserService.importExcel(safetySysUserDtoList);
          if (CollUtil.isNotEmpty(maps)) {
              ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
              mv.addObject(NormalExcelConstants.FILE_NAME, "系统用户导入失败信息");
              mv.addObject(NormalExcelConstants.CLASS, SafetySysUserImpoetDTO.class);

              LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
              ExportParams exportParams = new ExportParams("系统用户导入失败信息", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "系统用户");
              mv.addObject(NormalExcelConstants.PARAMS, exportParams);
              mv.addObject(NormalExcelConstants.DATA_LIST, maps);
              return mv;
          }
      } catch (Exception e) {
          log.error(e.getMessage());
          ImportExcelUtil.printWriteResult(response,Result.error("导入失败"+e.getMessage()));
          return null;
      }
      ImportExcelUtil.printWriteResult(response,Result.OK("导入成功"));
      return null;
  }

    @ApiOperation(value="系统用户表--数据权限菜单")
    @GetMapping("/getTree")
    public Result<List<MenuTree>> getTree(Long id){
      return Result.OK( safetySysUserService.getTree(id));
    }


    @AutoLog(value = "系统用户表-同步用户和厂区关系",operateType = 2)
    @ApiOperation(value="系统用户表--同步用户和厂区关系")
    @GetMapping("/sync")
    public Result<?> sync() {
        safetySysUserService.sync();
        return Result.ok();
    }
}
