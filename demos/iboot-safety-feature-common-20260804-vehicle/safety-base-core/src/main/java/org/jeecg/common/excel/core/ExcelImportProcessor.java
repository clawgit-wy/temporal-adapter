package org.jeecg.common.excel.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.excel.utils.ExcelUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class ExcelImportProcessor {

    public static <T extends BaseImportData> ModelAndView processImport(MultipartFile file,
            HttpServletRequest request,HttpServletResponse response,
            Class<T> clazz,AbstractImportListener<T> listener,int headRowNumber,int sheetNo,
            String businessName,Consumer<List<T>> successHandler) throws Exception {


        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //验证上传文件
        validateFile(file);

        try (InputStream is = file.getInputStream()) {
            ExcelUtil.importExcel(is, clazz, listener, headRowNumber, sheetNo);

            List<T> successList = listener.getSuccessList();
            List<T> failDataList = listener.getFailDataList();

            log.info("{}导入完成：成功{}条，失败{}条", businessName, successList.size(), failDataList.size());

            if (successHandler != null && !successList.isEmpty()) {
                log.info("开始处理{}条成功数据", successList.size());
                successHandler.accept(successList);
                log.info("成功数据处理完成");
            }

            if (!failDataList.isEmpty()) {
                log.warn("{}导入存在{}条失败数据，生成错误文件", businessName, failDataList.size());
                ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
                mv.addObject(NormalExcelConstants.FILE_NAME, businessName + "导入失败信息");
                mv.addObject(NormalExcelConstants.CLASS, clazz);
                ExportParams exportParams = new ExportParams(
                        businessName + "导入失败信息(成功" + successList.size() + "条,失败" + failDataList.size() + "条)",
                        "导入人:" + (sysUser != null ? sysUser.getRealname() : ""),
                        businessName
                );
                mv.addObject(NormalExcelConstants.PARAMS, exportParams);
                mv.addObject(NormalExcelConstants.DATA_LIST, failDataList);
                return mv;
            }

        } catch (Exception e) {
            log.error("{}导入失败", businessName, e);
            ImportExcelUtil.printWriteResult(response, Result.error("导入失败：" + e.getMessage()));
            return null;
        }

        ImportExcelUtil.printWriteResult(response, Result.OK("导入成功"));
        return null;
    }

    /**
     * 验证上传文件
     */
    private static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                !(originalFilename.endsWith(".xls") || originalFilename.endsWith(".xlsx"))) {
            throw new IllegalArgumentException("请上传Excel文件（.xls 或 .xlsx）");
        }
    }
}
