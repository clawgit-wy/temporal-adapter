package com.yutong.modules.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yutong.modules.user.dto.YTSafetySysJobRoleMappingDTO;
import com.yutong.modules.user.dto.YTSafetySysJobRoleMappingImportDTO;
import com.yutong.modules.user.entity.YTSafetySysJobRoleMapping;
import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.api.ISafetySysRoleService;
import com.yutong.modules.user.api.IYTSafetySysJobRoleMappingService;
import com.yutong.modules.user.vo.YTSafetySysJobRoleMappingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.ColumnPermission;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 岗位角色映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="岗位角色映射表")
@RestController
@RequestMapping("/sys/job/role/mapping")
public class YTSafetySysJobRoleMappingController extends JeecgController<YTSafetySysJobRoleMapping, IYTSafetySysJobRoleMappingService> {
	@Autowired
	private IYTSafetySysJobRoleMappingService yTSafetySysJobRoleMappingService;

	@Autowired
	private ISafetySysRoleService safetySysRoleService;

    @Autowired
    private ISafetyEmployeeMessageService safetyEmployeeMessageService;

    //excel导入条数限制
    @Value("${ytsafety.excel.importlimit}")
    private Integer importlimit;

	/**
	 * 分页列表查询
	 */
    @ColumnPermission(permCode="systemd:jobrole")
    @RequiresPermissions("systemd:jobrole")
	@ApiOperation(value="岗位角色映射表-分页列表查询", notes="岗位角色映射表-分页列表查询")
	@GetMapping(value = "/pageList")
	public Result<?> queryPageList(@RequestParam(name = "jobName",required = false) @ApiParam(value = "岗位名称") String jobName,
								   @RequestParam(name = "roleName",required = false) @ApiParam(value = "角色名称") String roleName,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		IPage<YTSafetySysJobRoleMappingVO> pageList = yTSafetySysJobRoleMappingService.queryPageList(pageNo, pageSize, jobName, roleName);
		return Result.OK(pageList);
	}

	/**
	 * 添加
	 */
    @RequiresPermissions("systemd:jobrole:add")
	@AutoLog(value = "岗位角色映射表-添加")
	@ApiOperation(value="岗位角色映射表-添加", notes="岗位角色映射表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody @Valid YTSafetySysJobRoleMappingDTO ytSafetySysJobRoleMappingDTO) {
		boolean success = yTSafetySysJobRoleMappingService.add(ytSafetySysJobRoleMappingDTO);
        return success ? Result.OK("添加成功！") : Result.error("添加失败");
	}

	/**
	 * 编辑
	 */
    @RequiresPermissions("systemd:jobrole:update")
	@AutoLog(value = "岗位角色映射表-编辑",operateType = 3)
	@ApiOperation(value="岗位角色映射表-编辑", notes="岗位角色映射表-编辑")
	@RequestMapping(value = "/update", method = {RequestMethod.POST})
	public Result<?> update(@RequestBody @Valid YTSafetySysJobRoleMappingDTO ytSafetySysJobRoleMappingDTO) {
        boolean success = yTSafetySysJobRoleMappingService.update(ytSafetySysJobRoleMappingDTO);
		return success ? Result.OK("编辑成功！") : Result.error("编辑失败");
	}

	/**
	 * 通过id删除
	 */
    @RequiresPermissions("systemd:jobrole:delete")
	@AutoLog(value = "岗位角色映射表-通过id删除")
	@ApiOperation(value="岗位角色映射表-通过id删除", notes="岗位角色映射表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) @ApiParam(value = "主键") @NotNull(message = "主建不能为空") @Valid Long id) {
        boolean success = yTSafetySysJobRoleMappingService.delete(id);
		return success ? Result.OK("删除成功！") : Result.error("删除失败");
	}

	/**
	 * 通过id查询
	 */
	@ApiOperation(value="岗位角色映射表-通过id查询", notes="岗位角色映射表-通过id查询")
	@GetMapping(value = "/getDetails")
	public Result<?> getDetails(@RequestParam(name="id",required=true) @ApiParam(value = "主键") @NotNull(message = "主建不能为空") @Valid Long id) {
		YTSafetySysJobRoleMappingVO sysJobRoleMappingVO = yTSafetySysJobRoleMappingService.getDetails(id);
		return Result.OK(sysJobRoleMappingVO);
	}

//	/**
//	 * 导出excel
//	 */
//    @AutoLog(value = "岗位角色映射表-导出")
//    @ApiOperation(value="岗位角色映射表-导出", notes="导出")
//	@RequestMapping(value = "/export")
//	public ModelAndView exportXls(HttpServletRequest request,
//	                              @RequestParam(name = "jobName") @ApiParam(value = "岗位名称") String jobName,
//	                              @RequestParam(name = "roleName") @ApiParam(value = "岗位名称") String roleName) {
//		List<YTSafetySysJobRoleMappingVO> list = yTSafetySysJobRoleMappingService.queryAllList(jobName,roleName);
//		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//		mv.addObject(NormalExcelConstants.FILE_NAME, "岗位角色映射");
//		mv.addObject(NormalExcelConstants.CLASS, YTSafetySysJobRoleMappingVO.class);
//		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//		ExportParams exportParams = new ExportParams("岗位角色映射报表", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "岗位角色映射");
//		mv.addObject(NormalExcelConstants.PARAMS, exportParams);
//		mv.addObject(NormalExcelConstants.DATA_LIST, list);
//		return mv;
//	}

	/**
	 * 下载导入模板
	 */
	@AutoLog(value = "岗位角色映射表-下载导入模板",operateType = 6)
	@ApiOperation(value="岗位角色映射表-下载导入模板", notes="下载导入模板")
	@GetMapping(value = "/importTemplate")
	public void importTemplate(HttpServletResponse response) throws IOException {
		String fileName = URLEncoder.encode("岗位角色映射导入模板", "UTF-8");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook();

		// 说明文字样式（红色字体、自动换行）
		CellStyle noteStyle = workbook.createCellStyle();
		Font noteFont = workbook.createFont();
		noteFont.setColor(IndexedColors.RED.getIndex());
		noteStyle.setFont(noteFont);
		noteStyle.setWrapText(true);

		// 表头样式（加粗）
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setWrapText(true);

		String[] headers = {"岗位编码*", "岗位名称", "角色名称*"};

		Sheet sheet = workbook.createSheet("导入模板");

		// 设置列宽
		for (int i = 0; i < headers.length; i++) {
			sheet.setColumnWidth(i, 30 * 256);
		}

		// 第1行：标题
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(25);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("岗位角色映射导入模板");
		titleCell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

		// 第2行：说明文字（合并第1列和第2列）
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));
		Row noteRow = sheet.createRow(1);
		noteRow.setHeightInPoints(60);
		Cell noteCell = noteRow.createCell(0);
		noteCell.setCellValue("注：带*的为必填字段 \n岗位编码列填写岗位的编码，示例：20004837\n岗位名称列填写岗位的名称，示例：安防规划与管理组长\n角色名称列填写角色的名称，示例：治安管理员");
		noteCell.setCellStyle(noteStyle);

		// 第3行：表头
		Row headerRow = sheet.createRow(2);
		headerRow.setHeightInPoints(22);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		// 第4行：示例数据
		Row dataRow = sheet.createRow(3);
		dataRow.createCell(0).setCellValue("示例：20004837");
		dataRow.createCell(1).setCellValue("示例：安防规划与管理组长");
		dataRow.createCell(2).setCellValue("示例：治安管理员");

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	/**
	 * 通过excel导入数据
	 * Excel格式：第一行标题"岗位角色映射表"，第二行表头（岗位编码|岗位名称|角色编码|角色名称）
	 */
    @RequiresPermissions("systemd:jobrole:import")
	@AutoLog(value = "岗位角色映射表-通过excel导入数据")
	@ApiOperation(value="岗位角色映射表-通过excel导入数据", notes="根据岗位编码和角色编码匹配数据后导入")
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public ModelAndView importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ImportParams params = new ImportParams();
        params.setTitleRows(2);
        params.setHeadRows(1);
        params.setNeedSave(false);
        try {
            String fileName = file.getOriginalFilename();
            if (fileName != null && (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))){
                ImportExcelUtil.printWriteResult(response, Result.error("只能导入Execl文件"));
                return null;
            }
            // 先校验表头：读取第3行（索引为2）的表头内容
            String[] expectedHeaders = {"岗位编码*", "岗位名称", "角色名称*"};
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(2);
                if (headerRow == null) {
                    ImportExcelUtil.printWriteResult(response, Result.error("导入模板格式不正确：第3行表头为空"));
                    return null;
                }
                for (int i = 0; i < expectedHeaders.length; i++) {
                    Cell cell = headerRow.getCell(i);
                    String actual = cell != null ? cell.toString().trim() : "";
                    if (!expectedHeaders[i].equals(actual)) {
                        ImportExcelUtil.printWriteResult(response, Result.error("导入模板表头不正确：第" + (i + 1) + "列表头应为\"" + expectedHeaders[i] + "\"，实际为\"" + actual + "\"，请重新下载模板导入"));
                        return null;
                    }
                }
            }
            // 表头校验通过后，再读取完整数据
            List<YTSafetySysJobRoleMappingImportDTO> jobRoleMappingImportList = ExcelImportUtil.importExcel(file.getInputStream(), YTSafetySysJobRoleMappingImportDTO.class, params);
            // 过滤空行：所有业务字段均为 null/空的行不导入
            jobRoleMappingImportList = jobRoleMappingImportList.stream()
                    .filter(dto -> dto != null
                            && (StringUtils.hasText(dto.getJobCode()) || StringUtils.hasText(dto.getJobName()) || StringUtils.hasText(dto.getRoleName())))
                    .collect(Collectors.toList());
            if (jobRoleMappingImportList.isEmpty()) {
                ImportExcelUtil.printWriteResult(response,Result.error("导入内容为空"));
                return null;
            }
            if (jobRoleMappingImportList.size() > importlimit){
                ImportExcelUtil.printWriteResult(response,Result.error("最多导入"+importlimit+"条数据！"));
                return null;
            }
            List<YTSafetySysJobRoleMappingImportDTO> msgVo = yTSafetySysJobRoleMappingService.importSysData(jobRoleMappingImportList);
            if (msgVo!=null&& !msgVo.isEmpty()) {
                mv.addObject(NormalExcelConstants.FILE_NAME, "岗位角色映射导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, YTSafetySysJobRoleMappingImportDTO.class);
                ExportParams exportParams = new ExportParams("岗位角色映射导入失败信息", "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "岗位角色");
                mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                mv.addObject(NormalExcelConstants.DATA_LIST, msgVo);
                return mv;
            }
        } catch (Exception e) {
            ImportExcelUtil.printWriteResult(response,Result.error("导入失败：请按模板导入正确数据"));
            return null;
        }
        ImportExcelUtil.printWriteResult(response,Result.OK("导入成功"));
        return null;
	}

}