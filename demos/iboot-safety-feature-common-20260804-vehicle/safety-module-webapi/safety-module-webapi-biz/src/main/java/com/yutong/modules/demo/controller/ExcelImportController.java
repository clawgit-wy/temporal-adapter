package com.yutong.modules.demo.controller;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.yutong.modules.demo.entity.DemoImportData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.excel.utils.ExcelUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Api(tags="导入demo")
@RestController
@RequestMapping("/demo")
public class ExcelImportController {


    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @ApiOperation(value = "导入Excel并统一校验", notes = "上传Excel，经过格式校验和业务校验，全部成功返回成功信息，有失败直接下载错误文件")
    @PostMapping("/import")
    public ModelAndView importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response){

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xls") || originalFilename.endsWith(".xlsx"))) {
            throw new IllegalArgumentException("请上传Excel文件（.xls 或 .xlsx）");
        }
        List<DemoImportData> successList = new ArrayList<>();
        List<DemoImportData> failDataList = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            // 1. 使用自定义监听器读取并做统一格式校验
            DemoImportListener listener = new DemoImportListener();
//            headRowNumber 去掉表头,从第几行开始读取
//            sheetNo       读第几个sheet
            ExcelUtil.importExcel(is, DemoImportData.class, listener,1,0);

            successList = listener.getSuccessList();
            failDataList = listener.getFailDataList();

            log.info("导入完成：成功{}条，失败{}条", successList.size(), failDataList.size());

            if (!failDataList.isEmpty()) {
                mv.addObject(NormalExcelConstants.FILE_NAME, "组织架构导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, DemoImportData.class);
                ExportParams exportParams = new ExportParams("组织架构导入失败信息(成功"+successList.size()+"条,失败"+failDataList.size()+"条)", "导入人:" + (sysUser != null ? sysUser.getRealname() : ""), "组织架构");
                mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
                return mv;
            }
        } catch (Exception e) {
            ImportExcelUtil.printWriteResult(response,Result.error("导入失败："+e.getMessage()));
            return null;
        }
        ImportExcelUtil.printWriteResult(response,Result.OK("导入成功"));
        return null;
    }

    public static class DemoImportListener  extends AnalysisEventListener<DemoImportData> {

        private final List<DemoImportData> successList = new ArrayList<>();
        private final List<DemoImportData> failDataList = new ArrayList<>();

        @Override
        public void invoke(DemoImportData data, AnalysisContext context) {
            Integer rowIndex = context.readRowHolder().getRowIndex() + 1;
            data.setRowIndex(rowIndex);

            Set<ConstraintViolation<DemoImportData>> violations = VALIDATOR.validate(data);

            if (violations.isEmpty()) {
//                做其他业务校验
//                校验通过加入 successList 失败加入failDataList
                successList.add(data);
            } else {
                String errorMsg = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; "));
                log.warn("第{}行数据校验失败：{}", rowIndex, errorMsg);
                data.setErrorMsg(errorMsg);
                failDataList.add(data);
            }
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) throws Exception {
            String errMsg;
            if (exception instanceof ExcelDataConvertException) {
                ExcelDataConvertException e = (ExcelDataConvertException) exception;
                errMsg = String.format("第%d行第%d列数据格式转换异常",
                        e.getRowIndex() + 1, e.getColumnIndex() + 1);
            } else {
                errMsg = "解析异常：" + exception.getMessage();
            }
            log.warn(errMsg);
            throw new RuntimeException(errMsg, exception);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("所有数据解析完成，共{}行", successList.size() + failDataList.size());
        }

        public List<DemoImportData> getSuccessList() { return successList; }
        public List<DemoImportData> getFailDataList() { return failDataList; }
    }

}
