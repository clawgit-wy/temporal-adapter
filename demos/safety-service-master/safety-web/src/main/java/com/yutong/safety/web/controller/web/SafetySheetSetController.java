package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.param.web.SheetSetAddParam;
import com.yutong.safety.common.param.web.SheetSetParam;
import com.yutong.safety.core.handler.SafetySheetSetHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  create gwcuiyx 2023.07.12
 *  表单设置--controller
 */
@Api(tags = "表单设置")
@RestController
@RequestMapping("/web/safety/sheet")
public class SafetySheetSetController {
    @Autowired
    private SafetySheetSetHandler safetySheetSetHandler;

    /**
     * 数据列表查询操作功能
     * @return
     */
    @ApiOperation(value = "数据列表查询接口")
    @RequestMapping("/list")
    public Object sheetSetList() {
        return safetySheetSetHandler.sheetSetList();
    }

    /**
     * 数据更新功能操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新接口")
    @RequestMapping("/update")
    public Object updateSheetSet(SheetSetParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetySheetSetHandler.updateSheetSet(param, tocken);
    }

    @ApiOperation(value = "数据新增或者修改接口")
    @RequestMapping("/saveOrUpdate")
    @ResponseBody
    public Object saveOrUpdate(String sheetSet, @RequestHeader("X-Access-Token") String tocken) {
        return safetySheetSetHandler.saveOrUpdate(sheetSet, tocken);
    }

    @ApiOperation(value = "数据添加功能")
    @RequestMapping("/save")
    @ResponseBody
    public Object save(SheetSetAddParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetySheetSetHandler.save(param, tocken);
    }

    @ApiOperation(value = "数据删除操作")
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        return safetySheetSetHandler.delete(id, tocken);
    }

}
