package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.listener.ShiftScheduleImportListener;
import com.yutong.modules.risk.service.IYTSafetyShiftScheduleService;
import com.yutong.modules.risk.vo.ShiftScheduleExportVO;
import com.yutong.modules.risk.vo.ShiftScheduleImportVO;
import com.yutong.modules.risk.vo.ShiftSchedulePageVO;
import com.yutong.modules.risk.web.ShiftScheduleAddParam;
import com.yutong.modules.risk.web.ShiftScheduleListParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.excel.utils.ExcelUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.ImportTemplateDownloadUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 排班管理
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Api(tags = "排班管理")
@RestController
@RequestMapping("/ytsafety/shift/schedule")
public class YTSafetyShiftScheduleController {

    @Autowired
    private IYTSafetyShiftScheduleService shiftScheduleService;

    /**
     * 分页查询
     * @param param 查询参数
     * @return 分页结果
     */
    @RequiresPermissions("schedule:shift:list")
    @AutoLog(value = "排班管理-分页数据列表查询", operateType = 1)
    @ApiOperation(value = "排班管理-分页数据列表查询接口")
    @PostMapping("/listPage")
    public Result<IPage<ShiftSchedulePageVO>> listPage(@RequestBody ShiftScheduleListParam param) {
        Page<ShiftSchedulePageVO> page = new Page<>(param.getPageNo(), param.getPageSize());
        IPage<ShiftSchedulePageVO> pageList = shiftScheduleService.pageList(page, param);
        return Result.OK(pageList);
    }

    /**
     * 查询列表（不分页）
     * @param param 查询参数
     * @return 列表
     */
    @RequiresPermissions("schedule:shift:list")
    @AutoLog(value = "排班管理-列表查询", operateType = 1)
    @ApiOperation(value = "排班管理-列表查询接口")
    @PostMapping("/list")
    public Result<List<ShiftSchedulePageVO>> list(@RequestBody ShiftScheduleListParam param) {
        List<ShiftSchedulePageVO> list = shiftScheduleService.selectList(param);
        return Result.OK(list);
    }

    /**
     * 根据 ID 查询
     * @param id 主键
     * @return 实体
     */
    @AutoLog(value = "排班管理-根据 ID 查询", operateType = 1)
    @ApiOperation(value = "排班管理-根据 ID 查询接口")
    @GetMapping("/queryById")
    public Result<ShiftSchedulePageVO> queryById(@RequestParam("id") Long id) {
        ShiftSchedulePageVO vo = shiftScheduleService.queryById(id);
        return Result.OK(vo);
    }

    /**
     * 新增
     * @param param 新增参数
     * @return 新增记录的主键 ID
     */
    @RequiresPermissions("schedule:shift:add")
    @AutoLog(value = "排班管理-新增", operateType = 2)
    @ApiOperation(value = "排班管理-新增接口")
    @PostMapping("/add")
    public Result<Long> add(@Valid @RequestBody ShiftScheduleAddParam param) {
        // 设置创建人
        LoginUser loginUser = BusUtits.getLoginUser();
        if (loginUser != null) {
            param.setCreateBy(loginUser.getRealname());
            param.setCreateUser(loginUser.getEmpNo());
        }
        Long id = shiftScheduleService.save(param);
        return Result.OK(id);
    }

    /**
     * 修改
     * @param param 修改参数
     * @return 修改结果
     */
    @RequiresPermissions("schedule:shift:edit")
    @AutoLog(value = "排班管理-修改", operateType = 3)
    @ApiOperation(value = "排班管理-修改接口")
    @PostMapping("/update")
    public Result<Integer> update(@Valid @RequestBody ShiftScheduleAddParam param) {
        // 设置更新人
        LoginUser loginUser = BusUtits.getLoginUser();
        if (loginUser != null) {
            param.setUpdateBy(loginUser.getRealname());
            param.setUpdateUser(loginUser.getEmpNo());
        }
        int result = shiftScheduleService.update(param);
        return Result.OK(result);
    }

    /**
     * 删除（逻辑删除）
     * @param id 主键
     * @return 删除结果
     */
    @RequiresPermissions("schedule:shift:delete")
    @AutoLog(value = "排班管理-删除", operateType = 2)
    @ApiOperation(value = "排班管理-删除接口")
    @PostMapping("/delete")
    public Result<Integer> delete(@RequestParam("id") Long id) {
        int result = shiftScheduleService.deleteById(id);
        return Result.OK(result);
    }

    /**
     * 导出
     * @param param 查询参数
     * @return 导出结果
     */
    @RequiresPermissions("schedule:shift:export")
    @AutoLog(value = "排班管理-数据导出", operateType = 1)
    @ApiOperation(value = "排班管理-数据导出接口")
    @GetMapping("/export")
    public ModelAndView export(ShiftScheduleListParam param) {
        List<ShiftSchedulePageVO> list = shiftScheduleService.selectList(param);
        List<ShiftScheduleExportVO> exportList = new ArrayList<>();
        for (ShiftSchedulePageVO item : list) {
            ShiftScheduleExportVO vo = new ShiftScheduleExportVO();
            BeanUtils.copyProperties(item, vo);
            // 人员类型转换 1：在岗人员 2：安保带班 3：消防值班
            switch (item.getPersonType()) {
                case "1":
                    vo.setPersonType("在岗人员");
                    break;
                case "2":
                    vo.setPersonType("安保带班");
                    break;
                case "3":
                    vo.setPersonType("消防值班");
                    break;
                default:
                    break;
            }
            exportList.add(vo);
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String sheetName = "排班管理";
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, sheetName);
        mv.addObject(NormalExcelConstants.CLASS, ShiftScheduleExportVO.class);
        ExportParams exportParams = new ExportParams(sheetName, "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), sheetName);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 下载导入模板（依据 ShiftScheduleImportVO 的 @Excel 注解生成）
     */
    @RequiresPermissions("schedule:shift:export")
    @AutoLog(value = "排班管理-下载导入模板", operateType = 1)
    @ApiOperation(value = "排班管理-下载导入模板接口")
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        String instructions = "1、带*的为必填字段\n" +
                "2、排班日期格式：yyyy-MM-dd\n" +
                "3、人员类型填写：安保带班 / 消防值班";
        List<String> examples = Arrays.asList(
                "2026-08-01",
                "安保带班",
                "YG001",
                "乘用车工厂",
                "A"
        );
        ImportTemplateDownloadUtil.buildImportTemplate(ShiftScheduleImportVO.class,
                "排班管理导入模板", "排班管理信息", instructions, examples, response);
    }

    /**
     * 批量导入
     * @param file Excel 文件
     * @return 导入结果
     */
    @RequiresPermissions("schedule:shift:import")
    @AutoLog(value = "排班管理-批量导入", operateType = 2)
    @ApiOperation(value = "排班管理-批量导入接口")
    @PostMapping("/importExcel")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
            return Result.error("请上传Excel文件（.xls 或 .xlsx）");
        }
        try (InputStream is = file.getInputStream()) {
            ShiftScheduleImportListener listener = new ShiftScheduleImportListener();
            ExcelUtil.importExcel(is, ShiftScheduleImportVO.class, listener, 3, 0);
            List<ShiftScheduleImportVO> successList = listener.getSuccessList();
            if (successList.isEmpty()) {
                return Result.error("没有成功解析的数据");
            }
            int count = shiftScheduleService.importData(successList);
            return Result.OK("成功导入" + count + "条数据");
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }
}
