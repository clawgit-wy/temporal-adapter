package com.yutong.modules.risk.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDTO;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStage;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcess;
import com.yutong.modules.risk.listener.HomeworkStageImportListener;
import com.yutong.modules.risk.query.YtsafetyHomeworkStageQuery;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageProcessDetailsService;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageProcessService;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageService;
import com.yutong.modules.risk.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.excel.utils.ExcelUtil;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.ImportTemplateDownloadUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

 /**
  * @Description: 作业环节表
  * @Author: jeecg-boot
  * @Date:   2026-06-30
  * @Version: V1.0
  */
@Slf4j
@Api(tags="作业环节表")
@RestController
@RequestMapping("/risk/ytsafetyHomeworkStage")
public class YtsafetyHomeworkStageController extends JeecgController<YtsafetyHomeworkStage, IYtsafetyHomeworkStageService> {
	@Autowired
	private IYtsafetyHomeworkStageService ytsafetyHomeworkStageService;

	@Autowired
	private IYtsafetyHomeworkStageProcessService ytsafetyHomeworkStageProcessService;

	@Autowired
	private IYtsafetyHomeworkStageProcessDetailsService ytsafetyHomeworkStageProcessDetailsService;

	/**
	 * 分页列表查询
	 *
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "作业环节表-分页列表查询")
	@ApiOperation(value="作业环节表-分页列表查询", notes="作业环节表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(YtsafetyHomeworkStageQuery query,
	                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
	                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
	                               HttpServletRequest req) {
		Page<YtsafetyHomeworkStage> page = new Page<YtsafetyHomeworkStage>(pageNo, pageSize);
		IPage<YtsafetyHomeworkStage> pageList = ytsafetyHomeworkStageService.queryPageList(page, query);
		return Result.OK(pageList);
	}

	/**
	 * 添加
	 *
	 * @param dto
	 * @return
	 */
	@AutoLog(value = "作业环节表-添加", operateType = 2 )
	@ApiOperation(value="作业环节表-添加", notes="作业环节表-添加")
	@PostMapping(value = "/add")
    @RequiresPermissions("risk:homeworkstage:add")
	public Result<?> add(@RequestBody YtsafetyHomeworkStageProcessDTO dto) {
		ytsafetyHomeworkStageProcessService.add(dto);
		return Result.OK("添加成功！");
	}

     /**
      * 添加
      *
      * @param dto
      * @return
      */
     @AutoLog(value = "作业环节表-修改", operateType = 2 )
     @ApiOperation(value="作业环节表-修改", notes="作业环节表-修改")
     @PostMapping(value = "/edit")
     @RequiresPermissions("risk:homeworkstage:edit")
     public Result<?> edit(@RequestBody YtsafetyHomeworkStageProcessDTO dto) {
         if(ObjectUtil.isNull(dto.getId())){
             throw new JeecgBootException("流程id为空无法修改，请核实后提交");
         }
         ytsafetyHomeworkStageProcessService.edit(dto);
         return Result.OK("添加成功！");
     }

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "作业环节表-通过id查询")
	@ApiOperation(value="作业环节表-通过id查询", notes="作业环节表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<YtsafetyHomeworkStageProcessVO> queryById(@RequestParam(name="id",required=true) Long id) {
		YtsafetyHomeworkStageProcessVO vo = ytsafetyHomeworkStageProcessService.queryById(id);
		return Result.OK(vo);
	}

     /**
      * 通过流程id查询详情
      * @param id
      * @return
      */
     @AutoLog(value = "作业环节表-通过流程id查询详情")
     @ApiOperation(value="作业环节表-通过流程id查询详情", notes="作业环节表-通过流程id查询详情")
     @GetMapping(value = "/queryByProcessId")
     public Result<YtsafetyHomeworkStageProcessVO> queryByProcessId(@RequestParam(name="id",required=true) Long id) {
         YtsafetyHomeworkStageProcessVO vo = ytsafetyHomeworkStageProcessService.queryByProcessId(id);
         return Result.OK(vo);
     }

	/**
	 * 作业环节-批量推送
	 *
	 * @param dto
	 * @return
	 */
	@AutoLog(value = "作业环节-批量推送", operateType = 2)
	@ApiOperation(value="作业环节-批量推送", notes="作业环节-批量推送")
	@PostMapping(value = "/batchPush")
    @RequiresPermissions("risk:homeworkstage:push")
	public Result<List<YtsafetyHomeworkStageProcessVO>> batchPush(@RequestBody YtsafetyHomeworkStageProcessDTO dto) {
		List<YtsafetyHomeworkStageProcessVO> voList = ytsafetyHomeworkStageProcessService.batchPush(dto);
		return Result.OK(voList);
	}

	/**
	 * 作业环节-导出excel
	 *
	 * @param request
	 * @param query
	 */
	@AutoLog(value = "作业环节-导出", operateType = 5)
	@ApiOperation(value="作业环节-导出excel", notes="作业环节-导出excel")
	@GetMapping(value = "/exportXls")
    @RequiresPermissions("risk:homeworkstage:export")
	public ModelAndView exportXls(HttpServletRequest request, YtsafetyHomeworkStageQuery query) {
		List<YtsafetyHomeworkStageExportVO> list = ytsafetyHomeworkStageService.queryList(query);
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		mv.addObject(NormalExcelConstants.FILE_NAME, "作业环节");
		mv.addObject(NormalExcelConstants.CLASS, YtsafetyHomeworkStageExportVO.class);
		LoginUser sysUser =  BusUtits.getLoginUser();
		ExportParams exportParams = new ExportParams("作业环节", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "作业环节");
		mv.addObject(NormalExcelConstants.PARAMS, exportParams);
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}

	/**
	 * 作业环节-导出流程详情excel
	 *
	 * @param request
	 * @param processId 流程表主键
	 */
	@AutoLog(value = "作业环节-导出流程详情", operateType = 5)
	@ApiOperation(value="作业环节-导出流程详情excel", notes="作业环节-导出流程详情excel")
	@GetMapping(value = "/exportDetailsXls")
	public ModelAndView exportDetailsXls(HttpServletRequest request, @RequestParam(name="processId", required=true) Long processId) {
		List<YtsafetyHomeworkStageDetailsExportVO> list = ytsafetyHomeworkStageProcessDetailsService.queryExportList(processId);

		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		mv.addObject(NormalExcelConstants.FILE_NAME, "作业环节流程详情");
		mv.addObject(NormalExcelConstants.CLASS, YtsafetyHomeworkStageDetailsExportVO.class);

		LoginUser sysUser =  BusUtits.getLoginUser();
		ExportParams exportParams = new ExportParams("作业环节流程详情", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "作业环节流程详情");
		mv.addObject(NormalExcelConstants.PARAMS, exportParams);
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}

	/**
	 * 下载导入模板
     * 新增三个参数，1 新增 2修改 3推送编辑
 	 */
	@ApiOperation(value="作业环节-下载导入模板", notes="下载导入模板")
	@GetMapping(value = "/importTemplate")
	public void importTemplate(@RequestParam(name="importType", required=false) Integer importType,
                               HttpServletResponse response) throws IOException {
        String[] headers = {"作业环节编码","作业环节名称*","所属厂区名称*","所属区域名称*","所属组织名称*",
                "位置名称*"};
        String instructions = "1、带*的为必填字段\n" +
                "2、所属厂区名称、所属区域名称、所属组织名称需与系统内名称一致\n" +
                "3、位置名称多个用英文逗号隔开";
        List<String> examples = Arrays.asList(
                "示例：ZYHJ01-DPYCJ-ZYHJ1517",
                "示例：涂装作业",
                "示例：一工厂（十八里河）",
                "示例：涂装区域",
                "示例：客车板块_宇通客车股份有限公司_制造体系_涂装车间",
                "示例：涂装车间A线，涂装车间B线"
        );
        ImportTemplateDownloadUtil.buildImportTemplate("作业环节更新模板", "作业环节信息", headers, instructions, examples, response);
	}

	/**
	 * 批量导入
	 *
	 * @param dto Excel导入参数
	 * @param file Excel文件
	 * @return
	 */
	@ApiOperation(value="作业环节-批量导入", notes="作业环节-批量导入")
	@PostMapping(value = "/importExcel")
    @RequiresPermissions("risk:homeworkstage:import")
	public ModelAndView importExcel(YtsafetyHomeworkStageProcessDTO dto, @RequestPart("file") MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		LoginUser sysUser =  BusUtits.getLoginUser();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		if (file.isEmpty()) {
			ImportExcelUtil.printWriteResult(response, Result.error("上传文件不能为空"));
			return null;
		}
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || !(originalFilename.endsWith(".xls") || originalFilename.endsWith(".xlsx"))) {
			ImportExcelUtil.printWriteResult(response, Result.error("请上传Excel文件（.xls 或 .xlsx）"));
			return null;
		}
		// 如果申请类型为空，赋默认值1（新增）
		if (dto == null || dto.getChangeType() == null) {
			if (dto == null) dto = new YtsafetyHomeworkStageProcessDTO();
			dto.setChangeType(1);
		}
		List<YtsafetyHomeworkStageImportVO> successList = new ArrayList<>();
		List<YtsafetyHomeworkStageImportVO> failDataList = new ArrayList<>();
		try (InputStream is = file.getInputStream()) {
			HomeworkStageImportListener listener = new HomeworkStageImportListener();
			ExcelUtil.importExcel(is, YtsafetyHomeworkStageImportVO.class, listener, 3, 0);
			successList = listener.getSuccessList();
			failDataList = listener.getFailDataList();
			log.info("作业环节导入完成：成功{}条，失败{}条", successList.size(), failDataList.size());
			if (successList.isEmpty()) {
				mv.addObject(NormalExcelConstants.FILE_NAME, "作业环节导入失败信息");
				mv.addObject(NormalExcelConstants.CLASS, YtsafetyHomeworkStageImportVO.class);
				ExportParams exportParams = new ExportParams("作业环节导入失败信息(成功0条,失败" + failDataList.size() + "条)",
						"导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "作业环节");
				mv.addObject(NormalExcelConstants.PARAMS, exportParams);
				mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
				return mv;
			}
			List<YtsafetyHomeworkStageVO> result = ytsafetyHomeworkStageProcessService.importData(dto, successList);
			// 检查service层校验失败的记录
			List<YtsafetyHomeworkStageImportVO> serviceFailList = successList.stream()
					.filter(v -> v.getErrorList() != null && !v.getErrorList().isEmpty())
					.collect(Collectors.toList());

			if (!serviceFailList.isEmpty()) {
				successList.removeAll(serviceFailList);
				failDataList.addAll(serviceFailList);
				mv.addObject(NormalExcelConstants.FILE_NAME, "作业环节导入失败信息");
				mv.addObject(NormalExcelConstants.CLASS, YtsafetyHomeworkStageImportVO.class);
				ExportParams exportParams = new ExportParams(
						"作业环节导入失败信息(成功" + successList.size() + "条,失败" + failDataList.size() + "条)",
						"导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "作业环节");
				mv.addObject(NormalExcelConstants.PARAMS, exportParams);
				mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
				return mv;
			}
			ImportExcelUtil.printWriteResult(response, Result.OK(result));
			return null;
		} catch (Exception e) {
			log.error("作业环节导入失败", e);
			ImportExcelUtil.printWriteResult(response, Result.error("导入失败：" + e.getMessage()));
			return null;
		}
	}
     @AutoLog(value = "作业环节-流程操作",operateType=3)
     @ApiOperation(value="作业环节-流程操作", notes="作业环节-流程操作")
     @PostMapping(value = "/processApprove")
     public Result<BpmReponseParamsVO> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
         return Result.OK(ytsafetyHomeworkStageProcessService.executeProcess(processParamDTO));
     }
}
