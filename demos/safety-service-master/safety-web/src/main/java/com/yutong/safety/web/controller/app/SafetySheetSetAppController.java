package com.yutong.safety.web.controller.app;

import com.yutong.safety.core.handler.SafetySheetSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端获取表单设置字段内容
 */
@RestController
@RequestMapping("/app/safety/sheet")
public class SafetySheetSetAppController {
    @Autowired
    private SafetySheetSetHandler safetySheetSetHandler;

    /**
     * 数据列表查询操作功能
     * @return
     */
    @RequestMapping("/list")
    public Object sheetSetList() {
        return safetySheetSetHandler.appList();
    }
}
