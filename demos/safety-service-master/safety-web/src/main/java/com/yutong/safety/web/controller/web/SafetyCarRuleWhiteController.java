package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.param.web.CarRuleWhiteListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.core.handler.SafetyCarRuleWhiteHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author lkp
 * @date 20240421
 */
@Api(tags = "车辆检查规则白名单")
@RestController
@RequestMapping("/web/safety/car/white")
public class SafetyCarRuleWhiteController {

    @Autowired
    SafetyCarRuleWhiteHandler carRuleWhiteListHandler;
    /**
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(CarRuleWhiteListParam param){
        return carRuleWhiteListHandler.pageList(param);
    }

    /**
     * @param param
     * @return
     */
    @ApiOperation(value = "新增或者更新")
    @RequestMapping("/update")
    public Object update(CarRuleWhiteListParam param){
        return carRuleWhiteListHandler.update(param);
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除")
    @RequestMapping("/delete")
    public Object delete(int id){
        return carRuleWhiteListHandler.deleteById(id);
    }

    /**
     * @param param
     * @return
     */
    @ApiOperation(value = "批量导入")
    @RequestMapping("/import")
    public Object importData(CommonImportParam param){
        return carRuleWhiteListHandler.importData(param);
    }

    @ApiOperation(value = "车辆检查规则白名单导入模板-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "车辆检查规则白名单导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/carRuleWhite.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
