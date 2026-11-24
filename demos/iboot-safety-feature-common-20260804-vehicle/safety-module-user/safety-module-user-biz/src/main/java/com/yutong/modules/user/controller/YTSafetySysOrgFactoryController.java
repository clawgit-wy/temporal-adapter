package com.yutong.modules.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yutong.modules.user.dto.YTSafetySysOrgFactoryDTO;
import com.yutong.modules.user.dto.YTSafetySysOrgFactoryImportDTO;
import com.yutong.modules.user.entity.YTSafetySysOrgFactory;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import com.yutong.modules.user.api.IYTSafetySysOrgFactoryService;
import com.yutong.modules.user.vo.YTSafetySysOrgFactoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 组织架构映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="组织架构映射表")
@RestController
@RequestMapping("/sys/org/factory")
public class YTSafetySysOrgFactoryController extends JeecgController<YTSafetySysOrgFactory, IYTSafetySysOrgFactoryService> {
	@Autowired
	private IYTSafetySysOrgFactoryService yTSafetySysOrgFactoryService;

	@Autowired
	private ISafetyFieldManageService safetyFieldManageService;

    @Resource
    private ISafetyGroupInfoService safetyGroupInfoService;

    //excel导入条数限制
    @Value("${ytsafety.excel.importlimit}")
    private Integer importlimit;

	/**
	 * 分页列表查询
	 */
    @ColumnPermission(permCode="systemd:orgstructure")
    @RequiresPermissions("systemd:orgstructure")
	@ApiOperation(value="组织架构映射表-分页列表查询", notes="组织架构映射表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(@RequestParam(name = "orgName",required = false) @ApiParam(value = "组织名称") String orgName,
								   @RequestParam(name = "factoryName",required = false) @ApiParam(value = "厂区名称") String factoryName,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		IPage<YTSafetySysOrgFactoryVO> pageList = yTSafetySysOrgFactoryService.queryPageList(pageNo, pageSize, orgName, factoryName);
		return Result.OK(pageList);
	}

	/**
	 * 添加
	 */
    @RequiresPermissions("systemd:orgstructure:add")
	@AutoLog(value = "组织架构映射表-添加")
	@ApiOperation(value="组织架构映射表-添加", notes="组织架构映射表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody @Valid YTSafetySysOrgFactoryDTO dto) {
		boolean success = yTSafetySysOrgFactoryService.add(dto);
		return success ? Result.OK("添加成功！") : Result.error("添加失败");
	}

	/**
	 * 编辑
	 */
    @RequiresPermissions("systemd:orgstructure:update")
	@AutoLog(value = "组织架构映射表-编辑",operateType = 3)
	@ApiOperation(value="组织架构映射表-编辑", notes="组织架构映射表-编辑")
	@PostMapping(value = "/update")
	public Result<?> update(@RequestBody @Valid YTSafetySysOrgFactoryDTO dto) {
		boolean success = yTSafetySysOrgFactoryService.update(dto);
		return success ? Result.OK("编辑成功！") : Result.error("编辑失败");
	}

	/**
	 * 通过id删除
	 */
    @RequiresPermissions("systemd:orgstructure:delete")
	@AutoLog(value = "组织架构映射表-通过id删除")
	@ApiOperation(value="组织架构映射表-通过id删除", notes="组织架构映射表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id") @Valid @NotNull(message = "主键不能为空") Long id) {
		boolean success = yTSafetySysOrgFactoryService.delete(id);
		return success ? Result.OK("删除成功!") : Result.error("删除失败!");
	}

	/**
	 * 通过id查询
	 */
	@ApiOperation(value="组织架构映射表-通过id查询", notes="组织架构映射表-通过id查询")
	@GetMapping(value = "/getDetails")
	public Result<?> getDetails(@RequestParam(name="id") @Valid @NotNull(message = "主键不能为空") Long id) {
		YTSafetySysOrgFactoryVO yTSafetySysOrgFactory = yTSafetySysOrgFactoryService.getDetails(id);
		return Result.OK(yTSafetySysOrgFactory);
	}

//	/**
//	 * 导出excel
//	 */
//    @AutoLog(value = "组织架构映射表-导出")
//    @ApiOperation(value="组织架构映射表-导出", notes="导出")
//	@GetMapping(value = "/export")
//	public ModelAndView exportXls(HttpServletRequest request,
//                                  @RequestParam(name = "orgName",required = false) @ApiParam(value = "组织名称") String orgName,
//                                  @RequestParam(name = "factoryName",required = false) @ApiParam(value = "厂区名称") String factoryName) {
//		List<YTSafetySysOrgFactoryVO> list = yTSafetySysOrgFactoryService.queryAllList(orgName,factoryName);
//		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//		mv.addObject(NormalExcelConstants.FILE_NAME, "组织架构映射");
//		mv.addObject(NormalExcelConstants.CLASS, YTSafetySysOrgFactoryVO.class);
//		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//		ExportParams exportParams = new ExportParams("组织架构映射报表", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "组织架构映射");
//		mv.addObject(NormalExcelConstants.PARAMS, exportParams);
//		mv.addObject(NormalExcelConstants.DATA_LIST, list);
//		return mv;
//	}

    /**
     * 下载导入模板
     */
    @RequiresPermissions("systemd:orgstructure:import")
    @AutoLog(value = "组织架构映射表-下载导入模板",operateType = 6)
    @ApiOperation(value="组织架构映射表-下载导入模板", notes="下载导入模板")
    @GetMapping(value = "/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        String fileName = URLEncoder.encode("组织架构映射导入模板", "UTF-8");
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

        String[] headers = {"组织架构*", "厂区*"};
        Sheet sheet = workbook.createSheet("导入模板");

        // 设置列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 30 * 256);
        }

        // 第1行：标题
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(25);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("组织架构映射导入模板");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        // 第2行：说明文字（合并所有列）
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));
        Row noteRow = sheet.createRow(1);
        noteRow.setHeightInPoints(60);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("注：带*的为必填字段 \n组织架构列填写组织架构路径名称，示例:客车板块,宇通客车股份有限公司,餐饮后勤体系,司法保卫室,治安管理科,十八里河门卫组\n厂区列填写厂区名称，示例:一工厂（十八里河）");
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
        dataRow.createCell(0).setCellValue("示例:客车板块,宇通客车股份有限公司,餐饮后勤体系,司法保卫室,治安管理科,十八里河门卫组");
        dataRow.createCell(1).setCellValue("示例:一工厂（十八里河）");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

	/**
	 * 通过excel导入数据
	 * Excel格式：第一行标题"组织架构映射表"，第三行表头（组织架构|组织架构编码|厂区名称|厂区编码）
	 */
	@AutoLog(value = "组织架构映射表-通过excel导入数据")
	@ApiOperation(value="组织架构映射表-通过excel导入数据", notes="根据厂区编码查找厂区信息并保存")
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
            String[] expectedHeaders = {"组织架构*", "厂区*"};
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(2);
                if (headerRow == null) {
                    ImportExcelUtil.printWriteResult(response, Result.error("导入模板格式不正确：请按模板导入正确数据"));
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
            List<YTSafetySysOrgFactoryImportDTO> orgFactoryImportList = ExcelImportUtil.importExcel(file.getInputStream(), YTSafetySysOrgFactoryImportDTO.class, params);
            // 过滤空行：所有业务字段均为 null/空的行不导入
            orgFactoryImportList = orgFactoryImportList.stream()
                    .filter(dto -> dto != null
                            && (StringUtils.hasText(dto.getOrgName()) || StringUtils.hasText(dto.getOrgCode()) || StringUtils.hasText(dto.getFactoryName()) || StringUtils.hasText(dto.getFactoryCode())))
                    .collect(Collectors.toList());
            if (orgFactoryImportList.isEmpty()) {
                ImportExcelUtil.printWriteResult(response,Result.error("导入内容为空"));
                return null;
            }
            if (orgFactoryImportList.size() > importlimit){
                ImportExcelUtil.printWriteResult(response,Result.error("最多导入"+importlimit+"条数据！"));
                return null;
            }
            List<YTSafetySysOrgFactoryImportDTO> msgVo = yTSafetySysOrgFactoryService.importSysData(orgFactoryImportList);
            if (msgVo!=null&& !msgVo.isEmpty()) {
                mv.addObject(NormalExcelConstants.FILE_NAME, "组织架构导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, YTSafetySysOrgFactoryImportDTO.class);
                ExportParams exportParams = new ExportParams("组织架构导入失败信息", "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "组织架构");
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