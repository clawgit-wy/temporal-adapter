package com.yutong.modules.user.controller;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yutong.modules.user.dto.YtsafetySysResponsibleDTO;
import com.yutong.modules.user.dto.YtsafetySysResponsibleImportDTO;
import com.yutong.modules.user.dto.YtsafetySysResponsibleQueryDTO;
import com.yutong.modules.user.entity.YtsafetySysResponsible;
import com.yutong.modules.user.api.IYtsafetySysResponsibleService;
import com.yutong.modules.user.vo.YtsafetySysResponsibleEditVO;
import com.yutong.modules.user.vo.YtsafetySysResponsibleVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.ColumnPermission;
import org.jeecg.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Slf4j
@Api(tags="安全责任信息库")
@RestController
@RequestMapping("/responsible")
public class YtsafetySysResponsibleController extends JeecgController<YtsafetySysResponsible, IYtsafetySysResponsibleService> {

     @Autowired
     private IYtsafetySysResponsibleService ytsafetySysResponsibleService;

     //excel导入条数限制
     @Value("${ytsafety.excel.importlimit}")
     private Integer importlimit;

     /**
      * 分页列表查询
      *
      * @param sysResponsibleQueryDTO
      * @param pageNo
      * @param pageSize
      * @param req
      * @return
      */
     @ColumnPermission(permCode = "systemd:secResponsible")
     @RequiresPermissions("systemd:secResponsible:query")
     @ApiOperation(value = "安全责任信息库-分页列表查询", notes = "安全责任信息库-分页列表查询")
     @GetMapping(value = "/list")
     public Result<IPage<YtsafetySysResponsibleVO>> queryPageList(YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO,
                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                    HttpServletRequest req) {
         Page<YtsafetySysResponsibleVO> page = new Page<YtsafetySysResponsibleVO>(pageNo, pageSize);
         IPage<YtsafetySysResponsibleVO> pageList = ytsafetySysResponsibleService.queryPage(page, sysResponsibleQueryDTO);
         return Result.OK(pageList);
     }

     /**
      * 添加
      *
      * @param sysResponsibleDTO
      * @return
      */

     @RequiresPermissions("systemd:secResponsible:add")
     @AutoLog(value = "安全责任信息库-添加")
     @ApiOperation(value = "安全责任信息库-添加", notes = "安全责任信息库-添加")
     @PostMapping(value = "/add")
     public Result<?> add(@RequestBody YtsafetySysResponsibleDTO sysResponsibleDTO) {
         ytsafetySysResponsibleService.saveSysResponsible(sysResponsibleDTO);
         return Result.OK("添加成功！");
     }

     /**
      * 编辑
      *
      * @param sysResponsibleDTO
      * @return
      */
     @RequiresPermissions("systemd:secResponsible:edit")
     @AutoLog(value = "安全责任信息库-编辑")
     @ApiOperation(value = "安全责任信息库-编辑", notes = "安全责任信息库-编辑")
     @PostMapping(value = "/edit")
     public Result<?> edit(@RequestBody YtsafetySysResponsibleDTO sysResponsibleDTO) {
         ytsafetySysResponsibleService.editSysResponsible(sysResponsibleDTO);
         return Result.OK("编辑成功!");
     }

     /**
      * 通过id删除
      *
      * @param id
      * @return
      */
     @RequiresPermissions("systemd:secResponsible:del")
     @AutoLog(value = "安全责任信息库-通过id删除")
     @ApiOperation(value = "安全责任信息库-通过id删除", notes = "安全责任信息库-通过id删除")
     @DeleteMapping(value = "/delete")
     public Result<?> delete(@RequestParam(name = "id", required = true) Long id) {
         ytsafetySysResponsibleService.deleteSysResponsible(id);
         return Result.OK("删除成功!");
     }

     /**
      * 老数据同步到责任人映射表
      *
      * @return
      */
     @RequiresPermissions("systemd:secResponsible:edit")
     @AutoLog(value = "安全责任信息库-老数据同步映射")
     @ApiOperation(value = "安全责任信息库-老数据同步映射", notes = "安全责任信息库-老数据同步映射")
     @PostMapping(value = "/syncMapping")
     public Result<?> syncMapping() {
         int count = ytsafetySysResponsibleService.syncHistoryResponsibleMapping();
         return Result.OK("同步成功，共处理" + count + "条安全责任人数据!");
     }

     /**
      * 通过id查询
      *
      * @param id
      * @return
      */
     @RequiresPermissions("systemd:secResponsible:query")
     @ApiOperation(value = "安全责任信息库-通过id查询", notes = "安全责任信息库-通过id查询")
     @GetMapping(value = "/queryById")
     public Result<?> queryById(@RequestParam(name = "id", required = true) Long id) {
         YtsafetySysResponsibleEditVO ytsafetySysResponsible = ytsafetySysResponsibleService.querySysResponsibleById(id);
         return Result.OK(ytsafetySysResponsible);
     }

     /**
      * 通过id查询
      *
      * @param empNo
      * @return
      */
     @RequiresPermissions("systemd:secResponsible:query")
     @ApiOperation(value = "安全责任信息库-通过empNo查询", notes = "安全责任信息库-通过empNo查询")
     @GetMapping(value = "/queryByEmpNo")
     public Result<?> queryByEmpNo(@RequestParam(name = "empNo", required = true) String empNo) {
         YtsafetySysResponsibleEditVO ytsafetySysResponsible = ytsafetySysResponsibleService.querySysUserByEmpNo(empNo);
         return Result.OK(ytsafetySysResponsible);
     }

     /**
      * 导出excel
      *
      * @param request
      * @param sysResponsibleQueryDTO
      */
     @ColumnPermission(permCode = "systemd:secResponsible")
     @RequiresPermissions("systemd:secResponsible:export")
     @ApiOperation(value = "安全责任信息库-导出", notes = "安全责任信息库-导出")
     @GetMapping(value = "/exportXls")
     public ModelAndView exportXls(HttpServletRequest request, YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO) {
         return ytsafetySysResponsibleService.exportSysResponsible(request, sysResponsibleQueryDTO);
     }

     /**
      * 下载导入模板
      */
     @ApiOperation(value="安全责任信息库-下载导入模板", notes="下载导入模板")
     @GetMapping(value = "/importTemplate")
     public void importTemplate(HttpServletResponse response) throws IOException {
         String[] headers = {"员工号*", "员工名", "角色名称*", "有效期", "授权部门", "授权厂区"};
         String instructions = "1、多个角色名称用英文逗号隔开\n" +
                 "2、多级部门用“_”隔开，多个授权部门用英文“,”隔开\n" +
                 "3、多个授权厂区用英文逗号隔开\n" +
                 "4、表*的为必填字段";
         java.util.List<String> examples = java.util.Arrays.asList(
                 "示例：01005566",
                 "示例：武小先",
                 "示例：门卫,保卫主管",
                 "示例：2026-5-30",
                 "示例：客车板块_宇通客车股份有限公司_人力资源体系_司法保卫室,客车板块_宇通客车股份有限公司_人力资源体系_司法保卫室_消防安全科,工程机械板块_宇通重工股份有限公司_综合管理室_司法保卫模块",
                 "示例：一工厂（十八里河）,二工厂（新能源）"
         );
         ImportTemplateDownloadUtil.buildImportTemplate("安全责任人导入模板", "安全责任人信息", headers, instructions, examples, response);
     }

     /**
      * 通过excel导入数据
      *
      * @param response
      * @return
      */
     @RequiresPermissions("systemd:secResponsible:import")
     @ApiOperation(value = "安全责任信息库-导入", notes = "安全责任信息库-导入")
     @PostMapping(value = "/importExcel")
     public ModelAndView importExcel(@RequestParam(value="file") MultipartFile file, HttpServletResponse response) {
         ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
         LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
         ImportParams params = new ImportParams();
         params.setTitleRows(2);
         params.setHeadRows(1);
         params.setNeedSave(false);
         try {

             String fileName = file.getOriginalFilename();
             if (fileName != null && (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
                 throw new RuntimeException("只能导入Excel文件");
             }
             if (file.isEmpty()) throw new RuntimeException("不能上传空文件");

             List<YtsafetySysResponsibleImportDTO> safetySysUserDtoList = filterMsgVO(ExcelImportUtil.importExcel(file.getInputStream(), YtsafetySysResponsibleImportDTO.class, params));
             if (safetySysUserDtoList.isEmpty()) {
                 throw new RuntimeException("导入内容为空");
             }
             if(safetySysUserDtoList.size() > importlimit){
                 throw new RuntimeException("最多导入"+importlimit+"条数据！");
             }

             List<YtsafetySysResponsibleImportDTO> msgVo = ytsafetySysResponsibleService.importSysResponsible(safetySysUserDtoList);
             if (msgVo!=null&& !msgVo.isEmpty()) {
                 mv.addObject(NormalExcelConstants.FILE_NAME, "安全责任人导入失败信息");
                 mv.addObject(NormalExcelConstants.CLASS, YtsafetySysResponsibleImportDTO.class);
                 ExportParams exportParams = new ExportParams("安全责任人导入失败信息", "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "安全责任人");
                 mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                 mv.addObject(NormalExcelConstants.DATA_LIST, msgVo);
                 return mv;
             }
         } catch (Exception e) {
             ImportExcelUtil.printWriteResult(response,Result.error("导入失败：" + e.getMessage()));
             return null;
         }
         ImportExcelUtil.printWriteResult(response,Result.OK("导入成功"));
         return null;
     }

     /**
      * 去重
      * @param safetySysUserDtoList
      * @return
      */
     public List<YtsafetySysResponsibleImportDTO> filterMsgVO(List<YtsafetySysResponsibleImportDTO> safetySysUserDtoList){
         HashMap<String,YtsafetySysResponsibleImportDTO> results = new HashMap<String,YtsafetySysResponsibleImportDTO>();
         List<YtsafetySysResponsibleImportDTO> emptyList = new ArrayList<>();
         for (YtsafetySysResponsibleImportDTO dto:safetySysUserDtoList){
             if(dto==null||dto.IsEmpty()){ emptyList.add(dto);continue;}
             if (StringUtils.isEmpty(dto.getEmpNo())) continue;
             if(results.containsKey(dto.getEmpNo().trim())){
                 dto.setIsRepeat(1);
                 results.get(dto.getEmpNo().trim()).setIsRepeat(1);
             }else{
                 results.put(dto.getEmpNo().trim(),dto);
             }
         }
         safetySysUserDtoList.removeAll(emptyList);
         return safetySysUserDtoList;
     }



 }
