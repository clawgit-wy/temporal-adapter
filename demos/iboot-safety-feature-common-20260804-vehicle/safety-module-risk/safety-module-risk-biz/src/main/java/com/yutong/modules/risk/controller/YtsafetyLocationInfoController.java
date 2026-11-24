package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.constants.enums.ApplyTypeEnum;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoDTO;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoQueryDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.listener.LocationInfoImportListener;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoProcessDetailsService;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoProcessService;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoService;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.UserAuthApiService;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import com.yutong.modules.user.vo.SafetyGroupInfoTreeVO;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 位置信息表
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Slf4j
@Api(tags="位置信息表")
@RestController
@RequestMapping("/risk/location")
public class YtsafetyLocationInfoController extends JeecgController<YtsafetyLocationInfo, IYtsafetyLocationInfoService> {

    @Autowired
    private IYtsafetyLocationInfoProcessService ytsafetyLocationInfoProcessService;

    @Autowired
    private IYtsafetyLocationInfoProcessDetailsService ytsafetyLocationInfoProcessDetailsService;

    @Autowired
    private UserAuthApiService userAuthApiService;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;

    //excel导入条数限制
    @Value("${ytsafety.excel.importlimit}")
    private Integer importlimit;

    /**
     * 位置信息表-分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ColumnPermission(permCode = "risk:workStation")
    @RequiresPermissions("risk:workStation:query")
    @ApiOperation(value="位置信息表-分页列表查询", notes="位置信息表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<YtsafetyLocationInfoVO>> queryPageList(YtsafetyLocationInfoQueryDTO query,
                                                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                               HttpServletRequest req) {
        Page<YtsafetyLocationInfoVO> page = new Page<>(pageNo, pageSize);
        IPage<YtsafetyLocationInfoVO> pageList = service.queryPageList(page, query);
        return Result.OK(pageList);
    }

    /**
     * 位置信息表-添加
     *
     * @param dto
     * @return
     */
    @RequiresPermissions("risk:workStation:add")
    @AutoLog(value = "位置信息表-添加", operateType = 2 )
    @ApiOperation(value="位置信息表-添加", notes="位置信息表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody YtsafetyLocationInfoDTO dto) {
        ytsafetyLocationInfoProcessService.add(dto);
        return Result.OK("添加成功！");
    }

    /**
     * 位置信息表-编辑
     *
     * @param dto
     * @return
     */
    @RequiresPermissions("risk:workStation:edit")
    @AutoLog(value = "位置信息表-编辑")
    @ApiOperation(value="位置信息表-编辑", notes="位置信息表-编辑")
    @PostMapping(value = "/edit")
    public Result<?> edit(@RequestBody YtsafetyLocationInfoDTO dto) {
        ytsafetyLocationInfoProcessService.edit(dto);
        return Result.OK("编辑成功!");
    }

    /**
     * 位置信息表-通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value="位置信息表-通过id查询", notes="位置信息表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<YtsafetyLocationInfoProcessVO> queryById(@RequestParam(name="id",required=true) Long id,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        YtsafetyLocationInfoProcessVO vo = ytsafetyLocationInfoProcessService.queryById(id);
        return Result.OK(vo);
    }

    /**
     * 位置信息表-批量推送
     *
     * @param dto
     * @return
     */
    @RequiresPermissions("risk:workStation:update:push")
    @AutoLog(value = "位置信息表-批量推送", operateType = 2)
    @ApiOperation(value="位置信息表-批量推送", notes="位置信息表-批量推送")
    @PostMapping(value = "/batchPush")
    public Result<List<YtsafetyLocationInfoProcessVO>> batchPush(@RequestBody YtsafetyLocationInfoDTO dto) {
        List<YtsafetyLocationInfoProcessVO> voList = ytsafetyLocationInfoProcessService.batchPush(dto);
        return Result.OK(voList);
    }

    /**
     * 位置信息表-批量推送
     *
     * @return
     */
    @ApiOperation(value="位置信息表-同步风险数据", notes="位置信息表-同步风险数据")
    @PostMapping(value = "/syncRiskLevel")
    public Result<String> syncRiskLevel() {
        List<Long> ids = service.lambdaQuery()
                .select(YtsafetyLocationInfo::getId)
                .eq(YtsafetyLocationInfo::getDelFlag, 0)
                .list()
                .stream()
                .map(YtsafetyLocationInfo::getId)
                .collect(Collectors.toList());
        service.syncLocationRiskLevel(ids);
        return Result.OK("同步成功");
    }

    /**
     * 位置信息表-导出excel
     *
     * @param request
     * @param query
     */
    @RequiresPermissions("risk:workStation:export")
    @AutoLog(value = "位置信息表-导出", operateType = 5)
    @ApiOperation(value="位置信息表-导出excel", notes="位置信息表-导出excel")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, YtsafetyLocationInfoQueryDTO query) {
        List<YtsafetyLocationInfoExportVO> list = service.queryList(query).stream().map(vo -> {
            YtsafetyLocationInfoExportVO exportVO = new YtsafetyLocationInfoExportVO();
            BeanUtils.copyProperties(vo, exportVO);
            exportVO.setDetailUrl(exportUrl + "safety/risk/workStationApprove?mode=detail&id=" + vo.getId());
            return exportVO;
        }).collect(Collectors.toList());



        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "位置信息");
        mv.addObject(NormalExcelConstants.CLASS, YtsafetyLocationInfoExportVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("位置信息", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "位置信息");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 位置信息表-导出流程详情excel
     *
     * @param request
     * @param processId 流程表主键
     */
    @AutoLog(value = "位置信息表-导出流程详情", operateType = 5)
    @ApiOperation(value="位置信息表-导出流程详情excel", notes="位置信息表-导出流程详情excel")
    @GetMapping(value = "/exportDetailsXls")
    public ModelAndView exportDetailsXls(HttpServletRequest request, @RequestParam(name="processId", required=false) Long processId) {
        List<YtsafetyLocationInfoDetailsExportVO> list=null;
        if(processId==null) {
            list = new ArrayList<>();
        }else{
            list = ytsafetyLocationInfoProcessDetailsService.queryExportList(processId);
        }
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "位置信息流程详情");
        mv.addObject(NormalExcelConstants.CLASS, YtsafetyLocationInfoDetailsExportVO.class);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("位置信息流程详情", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "位置信息流程详情");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 位置信息表-下载导入模板
     */
    @ApiOperation(value="位置信息表-下载导入模板", notes="下载导入模板")
    @GetMapping(value = "/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        String[] headers = {"所属区域*", "所属组织*", "所属厂区*", "位置*", "位置编码"};
        String instructions = "1、带*的为必填字段\n" +
                "2、厂区名称、区域名称、所属组织名称需与系统内名称一致\n 3、所属组织格式为 “部门1,部门2,部门3” 部门为部门倒排";
        List<String> examples = Arrays.asList(
                "示例：涂装车间A线",
                "示例：涂装车间",
                "示例：乘用车工厂",
                "示例：涂装机器人",
                "示例：WZ001-001"
        );
        ImportTemplateDownloadUtil.buildImportTemplate("位置信息导入模板", "位置信息", headers, instructions, examples, response);
    }

    /**
     * 位置信息表-批量导入
     *
     * @param dto Excel导入参数
     * @param file Excel文件
     * @return
     */
    @RequiresPermissions("risk:workStation:batch:import")
    @ApiOperation(value="位置信息表-批量导入", notes="位置信息表-批量导入")
    @PostMapping(value = "/importExcel")
    public ModelAndView importExcel(YtsafetyLocationInfoDTO dto, @RequestPart("file") MultipartFile file,
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

        List<YtsafetyLocationInfoImportVO> successList = new ArrayList<>();
        List<YtsafetyLocationInfoImportVO> failDataList = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            LocationInfoImportListener listener = new LocationInfoImportListener();
            ExcelUtil.importExcel(is, YtsafetyLocationInfoImportVO.class, listener, 3, 0);

            successList = listener.getSuccessList();
            failDataList = listener.getFailDataList();

            if((successList.size()+failDataList.size()) > importlimit){
                throw new RuntimeException("最多导入"+importlimit+"条数据！");
            }

            log.info("位置信息导入完成：成功{}条，失败{}条", successList.size(), failDataList.size());

            if (successList.isEmpty()) {
                mv.addObject(NormalExcelConstants.FILE_NAME, "位置信息导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, YtsafetyLocationInfoImportVO.class);
                ExportParams exportParams = new ExportParams("位置信息导入失败信息)",
                        "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "位置信息");
                mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
                return mv;
            }

            List<YtsafetyLocationInfoProcessDetailsVO> result = ytsafetyLocationInfoProcessService.importData(dto, successList);

            if (dto.getChangeType() != null && (dto.getChangeType().equals(ApplyTypeEnum.BATCH_PUSH.getCode())||dto.getChangeType().equals(ApplyTypeEnum.BATCH_ADD.getCode()))) {
                ytsafetyLocationInfoProcessDetailsService.compareWithExisting(dto.getId(), result);
            }

            // 检查service层校验失败的记录
            List<YtsafetyLocationInfoImportVO> serviceFailList = successList.stream()
                    .filter(v -> v.getErrorList() != null && !v.getErrorList().isEmpty())
                    .collect(Collectors.toList());

            if (!serviceFailList.isEmpty()) {
                successList.removeAll(serviceFailList);
                failDataList.addAll(serviceFailList);
                mv.addObject(NormalExcelConstants.FILE_NAME, "位置信息导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, YtsafetyLocationInfoImportVO.class);
                ExportParams exportParams = new ExportParams(
                        "位置信息导入失败信息",
                        "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "位置信息");
                mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
                return mv;
            }

            ImportExcelUtil.printWriteResult(response, Result.OK(result));
            return null;
        } catch (Exception e) {
            log.error("位置信息导入失败", e);
            ImportExcelUtil.printWriteResult(response, Result.error("导入失败：" + e.getMessage()));
            return null;
        }
    }

    /**
     * 位置信息表-根据主键查询详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("risk:workStation:details")
    @ApiOperation(value="位置信息表-根据主键查询详情", notes="位置信息表-根据主键查询详情")
    @GetMapping(value = "/detail")
    public Result<YtsafetyLocationInfoVO> detail(@RequestParam(name="id", required=true) Long id) {
        YtsafetyLocationInfoVO vo = service.queryById(id);
        return Result.OK(vo);
    }

    /**
     * 位置信息表-批量启停状态设置
     *
     * @param ids 位置信息ID列表
     * @param status 状态 0启用 1停用
     * @return
     */
    @RequiresPermissions("risk:workStation:start:stop")
    @AutoLog(value = "位置信息表-批量启停状态设置")
    @ApiOperation(value="位置信息表-批量启停状态设置", notes="位置信息表-批量启停状态设置")
    @PostMapping(value = "/updateStatus")
    public Result<?> updateStatus(@RequestParam(name="ids", required=true) List<Long> ids,
                                   @RequestParam(name="status", required=true) Integer status) {
        service.updateStatus(ids, status);
        return Result.OK(status == 0 ? "批量启用成功！" : "批量停用成功！");
    }

    /**
     * 位置信息表-流程操作
     *
     * @param processParamDTO
     * @return
     */
    @AutoLog(value = "位置信息表-流程操作",operateType=3)
    @ApiOperation(value="位置信息表-流程操作", notes="位置信息表-流程操作")
    @PostMapping(value = "/processApprove")
    public Result<BpmReponseParamsVO> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
        return Result.OK(ytsafetyLocationInfoProcessService.executeProcess(processParamDTO));
    }



}
