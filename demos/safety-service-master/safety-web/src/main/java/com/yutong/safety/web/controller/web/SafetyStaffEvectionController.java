package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.param.web.SafetyStaffEvectionParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.core.handler.SafetyStaffEvectionHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author lkp
 * @date 20240412
 */
@Api(tags = "公休信息")
@Controller
@RequestMapping("/web/safety/staffEvection")
public class SafetyStaffEvectionController {
    @Autowired
    private SafetyStaffEvectionHandler safetyStaffEvectionHandler;

    /**
     * 查询列表
     * @return
     */
    @ApiOperation(value = "列表")
    @PostMapping("/list")
    @ResponseBody
    public Object queryList(SafetyStaffEvectionParam param){
        return safetyStaffEvectionHandler.queryList(param);
    }

    /**
     * 新增
     * @return
     */
    @ApiOperation(value = "新增")
    @PostMapping("/add")
    @ResponseBody
    public Object add(SafetyStaffEvectionParam param){
        return safetyStaffEvectionHandler.add(param);
    }

    /**
     * 修改
     * @return
     */
    @ApiOperation(value = "修改")
    @PostMapping("/update")
    @ResponseBody
    public Object update(SafetyStaffEvectionParam param){
        return safetyStaffEvectionHandler.update(param);
    }

    /**
     * 数据添加接口
     * @return
     */
    @ApiOperation(value = "导入")
    @PostMapping("/importData")
    @ResponseBody
    public Object importData(@RequestParam("file") MultipartFile file) {
        return safetyStaffEvectionHandler.importData(file);
    }

    @ApiOperation(value = "公休信息导入模板-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "公休信息导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/staffEvection.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
