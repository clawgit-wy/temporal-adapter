package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDTO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataQueryDTO;
import com.yutong.modules.risk.entity.YtsafetyRiskData;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcess;
import com.yutong.modules.risk.listener.RiskDataImportListener;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStage;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageService;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoService;
import com.yutong.modules.risk.service.IYtsafetyRiskDataProcessDetailsService;
import com.yutong.modules.risk.service.IYtsafetyRiskDataProcessService;
import com.yutong.modules.risk.service.IYtsafetyRiskDataService;
import com.yutong.modules.risk.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.ColumnPermission;
import org.jeecg.common.excel.utils.ExcelUtil;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.ImportTemplateDownloadUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 风险数据表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Slf4j
@Api(tags="风险数据表")
@RestController
@RequestMapping("/risk/ytsafetyRiskData")
public class YtsafetyRiskDataController extends JeecgController<YtsafetyRiskData, IYtsafetyRiskDataService> {
    @Autowired
    private IYtsafetyRiskDataService ytsafetyRiskDataService;

    @Autowired
    private IYtsafetyRiskDataProcessService ytsafetyRiskDataProcessService;

    @Autowired
    private IYtsafetyRiskDataProcessDetailsService ytsafetyRiskDataProcessDetailsService;

    @Autowired
    private IYtsafetyHomeworkStageService ytsafetyHomeworkStageService;

    @Autowired
    private IYtsafetyLocationInfoService ytsafetyLocationInfoService;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;

    //excel导入条数限制
    @Value("${ytsafety.excel.importlimit}")
    private Integer importlimit;

    /**
     * 分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ColumnPermission(permCode = "risk:riskdata")
    @RequiresPermissions("risk:riskdata:query")
    @ApiOperation(value = "风险数据表-分页列表查询", notes = "风险数据表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<YtsafetyRiskDataVO>> queryPageList(YtsafetyRiskDataQueryDTO query,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        Page<YtsafetyRiskDataVO> page = new Page<>(pageNo, pageSize);
        IPage<YtsafetyRiskDataVO> pageList = ytsafetyRiskDataService.queryPageList(page, query);
        return Result.OK(pageList);
    }

    /**
     * 风险数据表-添加
     *
     * @param dto
     * @return
     */
    @RequiresPermissions("risk:riskdata:add")
    @AutoLog(value = "风险数据表-添加", operateType = 2)
    @ApiOperation(value = "风险数据表-添加", notes = "风险数据表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody YtsafetyRiskDataDTO dto) {
        ytsafetyRiskDataProcessService.add(dto);
        return Result.OK("添加成功！");
    }

    /**
     * 风险数据表-编辑
     *
     * @param dto
     * @return
     */
    @RequiresPermissions("risk:riskdata:edit")
    @AutoLog(value = "风险数据表-编辑")
    @ApiOperation(value = "风险数据表-编辑", notes = "风险数据表-编辑")
    @PostMapping(value = "/edit")
    public Result<?> edit(@RequestBody YtsafetyRiskDataDTO dto) {
        ytsafetyRiskDataProcessService.edit(dto);
        return Result.OK("编辑成功!");
    }

    /**
     * 风险数据表-通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "风险数据表-通过id查询", notes = "风险数据表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<YtsafetyRiskDataProcessVO> queryById(@RequestParam(name = "id", required = true) Long id) {
        YtsafetyRiskDataProcessVO vo = ytsafetyRiskDataProcessService.queryById(id);
        return Result.OK(vo);
    }

    /**
     * 风险数据表-通过数据id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "风险数据表-通过数据id查询", notes = "风险数据表-通过数据id查询")
    @GetMapping(value = "/queryByDataId")
    public Result<YtsafetyRiskDataVO> queryByDataId(@RequestParam(name = "id", required = true) Long id) {
        YtsafetyRiskDataVO vo = ytsafetyRiskDataService.queryByDataId(id);
        return Result.OK(vo);
    }

    /**
     * 风险数据表-批量推送
     *
     * @param dto
     * @return
     */
    @RequiresPermissions("risk:riskdata:update:push")
    @AutoLog(value = "风险数据表-批量推送", operateType = 2)
    @ApiOperation(value = "风险数据表-批量推送", notes = "风险数据表-批量推送")
    @PostMapping(value = "/batchPush")
    public Result<List<YtsafetyRiskDataProcessVO>> batchPush(@RequestBody YtsafetyRiskDataDTO dto) {
        List<YtsafetyRiskDataProcessVO> voList = ytsafetyRiskDataProcessService.batchPush(dto);
        return Result.OK(voList);
    }

    /**
     * 风险数据表-导出excel
     *
     * @param request
     * @param query
     */
    @ColumnPermission(permCode = "risk:riskdata")
    @RequiresPermissions("risk:riskdata:export")
    @AutoLog(value = "风险数据表-导出", operateType = 5)
    @ApiOperation(value = "风险数据表-导出excel", notes = "风险数据表-导出excel")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, YtsafetyRiskDataQueryDTO query) {
        List<YtsafetyRiskDataExportVO> list = ytsafetyRiskDataService.queryList(query).stream().map(
                vo -> {
                    YtsafetyRiskDataExportVO exportVO = new YtsafetyRiskDataExportVO();
                    BeanUtils.copyProperties(vo, exportVO);
                    exportVO.setDetailUrl(exportUrl + "safety/risk/riskDataApprove?mode=detail&isUpdate=true&id=" + vo.getId());
                    return exportVO;
                }
        ).collect(Collectors.toList());


        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "风险数据");
        mv.addObject(NormalExcelConstants.CLASS, YtsafetyRiskDataExportVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("风险数据", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "风险数据");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 风险数据表-导出流程详情excel
     *
     * @param request
     * @param processId 流程表主键
     */
    @AutoLog(value = "风险数据表-导出流程详情", operateType = 5)
    @ApiOperation(value = "风险数据表-导出流程详情excel", notes = "风险数据表-导出流程详情excel")
    @GetMapping(value = "/exportDetailsXls")
    public ModelAndView exportDetailsXls(HttpServletRequest request, @RequestParam(name = "processId", required = true) Long processId) {
        List<YtsafetyRiskDataDetailsExportVO> list = ytsafetyRiskDataProcessDetailsService.queryExportList(processId);

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "风险数据流程详情");
        mv.addObject(NormalExcelConstants.CLASS, YtsafetyRiskDataDetailsExportVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("风险数据流程详情", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "风险数据流程详情");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 下载导入模板（依据 YtsafetyRiskDataDetailsImportVO 的 @Excel 注解生成）
     */
    @ApiOperation(value = "风险数据表-下载导入模板", notes = "下载导入模板")
    @GetMapping(value = "/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        String instructions = "1、带*的为必填字段\n" +
                "2、厂区名称、区域名称、位置名称、所属组织名称需与系统内名称一致";
        List<String> examples = Arrays.asList(
                "示例：FX-001",
                "示例：机械伤害",
                "示例：涂装作业",
                "示例：乘用车工厂",
                "示例：涂装车间A线",
                "示例：涂装车间",
                "示例：涂装机器人工位",
                "示例：涂装机器人",
                "示例：设备因素",
                "示例：机械安全",
                "示例：设备防护",
                "示例：涂装机器人作业时存在机械伤害风险",
                "示例：加装安全防护罩",
                "示例：可能",
                "示例：严重",
                "示例：频繁"
        );
        ImportTemplateDownloadUtil.buildImportTemplate(YtsafetyRiskDataDetailsImportVO.class,
                "风险数据导入模板", "风险数据信息", instructions, examples, response);
    }

    /**
     * 批量导入
     *
     * @param dto  Excel导入参数
     * @param file Excel文件
     * @return
     */
    @RequiresPermissions("risk:riskdata:batch:import")
    @ApiOperation(value = "风险数据表-批量导入", notes = "风险数据表-批量导入")
    @PostMapping(value = "/importExcel")
    public ModelAndView importExcel(YtsafetyRiskDataDTO dto, @RequestPart("file") MultipartFile file,
                                    HttpServletRequest request, HttpServletResponse response) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
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

        List<YtsafetyRiskDataDetailsImportVO> successList = new ArrayList<>();
        List<YtsafetyRiskDataDetailsImportVO> failDataList = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            RiskDataImportListener listener = new RiskDataImportListener();
            ExcelUtil.importExcel(is, YtsafetyRiskDataDetailsImportVO.class, listener, 3, 0);

            successList = listener.getSuccessList();
            failDataList = listener.getFailDataList();
            if ((successList.size() + failDataList.size()) > importlimit) {
                throw new RuntimeException("最多导入" + importlimit + "条数据！");
            }
            log.info("风险数据导入完成：成功{}条，失败{}条", successList.size(), failDataList.size());

            if (successList.isEmpty()) {
                mv.addObject(NormalExcelConstants.FILE_NAME, "风险数据导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, YtsafetyRiskDataDetailsImportVO.class);
                ExportParams exportParams = new ExportParams("风险数据导入失败信息",
                        "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "风险数据");
                mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
                return mv;
            }

            List<YtsafetyRiskDataDetailsVO> result = ytsafetyRiskDataProcessService.importData(dto, successList);

            // 检查service层校验失败的记录（如changeType==4时riskCode不匹配）
            List<YtsafetyRiskDataDetailsImportVO> serviceFailList = successList.stream()
                    .filter(v -> v.getErrorList() != null && !v.getErrorList().isEmpty())
                    .collect(Collectors.toList());

            if (!serviceFailList.isEmpty()) {
                successList.removeAll(serviceFailList);
                failDataList.addAll(serviceFailList);
                mv.addObject(NormalExcelConstants.FILE_NAME, "风险数据导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, YtsafetyRiskDataDetailsImportVO.class);
                ExportParams exportParams = new ExportParams(
                        "风险数据导入失败信息",
                        "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "风险数据");
                mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
                return mv;
            }

            ImportExcelUtil.printWriteResult(response, Result.OK(result));
            return null;
        } catch (Exception e) {
            log.error("风险数据导入失败", e);
            ImportExcelUtil.printWriteResult(response, Result.error("导入失败：" + e.getMessage()));
            return null;
        }
    }

    /**
     * "风险数据表-流程操作
     *
     * @param processParamDTO
     * @return
     */
    @AutoLog(value = "风险数据表-流程操作", operateType = 3)
    @ApiOperation(value = "风险数据表-流程操作", notes = "风险数据表-流程操作")
    @PostMapping(value = "/processApprove")
    public Result<BpmReponseParamsVO> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
        return Result.OK(ytsafetyRiskDataProcessService.executeProcess(processParamDTO));
    }

    /**
     * 风险数据表-查询风险类型字典树
     *
     * @return
     */
    @ApiOperation(value = "风险数据表-查询风险类型字典树", notes = "风险数据表-查询风险类型字典树")
    @GetMapping(value = "/riskTypeDict")
    public Result<List<YtSafetyRiskTypeDictVO>> queryRiskTypeDict() {
        return Result.OK(ytsafetyRiskDataProcessService.queryRiskTypeDict());
    }

    /**
     * 风险数据表-作业环节下拉列表
     *
     * @param name 作业环节名称（模糊查询）
     * @return
     */
    @ApiOperation(value = "风险数据表-作业环节下拉列表", notes = "风险数据表-作业环节下拉列表，依据名称模糊查询")
    @GetMapping(value = "/homeworkStageDropdown")
    public Result<List<YtsafetyHomeworkStageDropdownVO>> queryHomeworkStageDropdown(@RequestParam(name = "name", required = false) String name) {
        List<YtsafetyHomeworkStage> list = ytsafetyHomeworkStageService.queryDropdownList(name);

        // 收集所有位置ID，批量查询位置信息库
        Set<Long> allLocationIds = new LinkedHashSet<>();
        for (YtsafetyHomeworkStage stage : list) {
            if (StringUtils.isNotBlank(stage.getLocationIds())) {
                for (String idStr : stage.getLocationIds().split(",")) {
                    allLocationIds.add(Long.valueOf(idStr.trim()));
                }
            }
        }
        Map<Long, YtsafetyLocationInfo> locationMap = Collections.emptyMap();
        if (!allLocationIds.isEmpty()) {
            locationMap = ytsafetyLocationInfoService.listByIds(allLocationIds).stream()
                    .collect(Collectors.toMap(YtsafetyLocationInfo::getId, e -> e, (a, b) -> a));
        }

        Map<Long, YtsafetyLocationInfo> finalLocationMap = locationMap;
        List<YtsafetyHomeworkStageDropdownVO> result = list.stream().map(e -> {
            YtsafetyHomeworkStageDropdownVO vo = new YtsafetyHomeworkStageDropdownVO();
            vo.setId(e.getId());
            vo.setCode(e.getLinkCode());
            vo.setName(e.getLinkName());
            vo.setFactoryId(e.getFactoryId());
            vo.setFactoryCode(e.getFactoryCode());
            vo.setFactoryName(e.getFactoryName());
            vo.setAreaId(e.getAreaId());
            vo.setAreaName(e.getAreaName());
            vo.setOrgId(e.getOrgId());
            vo.setOrgCode(e.getOrgCode());
            vo.setOrgName(e.getOrgName());
            // 组装位置下拉列表
            List<LocationDropdownVO> locations = new ArrayList<>();
            if (StringUtils.isNotBlank(e.getLocationIds())) {
                for (String idStr : e.getLocationIds().split(",")) {
                    Long locId = Long.valueOf(idStr.trim());
                    YtsafetyLocationInfo locInfo = finalLocationMap.get(locId);
                    if (locInfo != null) {
                        LocationDropdownVO locVO = new LocationDropdownVO();
                        locVO.setId(locInfo.getId());
                        locVO.setCode(locInfo.getLocationCode());
                        locVO.setName(locInfo.getLocationName());
                        locations.add(locVO);
                    }
                }
            }
            vo.setLocations(locations);
            return vo;
        }).collect(Collectors.toList());
        return Result.OK(result);
    }

}
