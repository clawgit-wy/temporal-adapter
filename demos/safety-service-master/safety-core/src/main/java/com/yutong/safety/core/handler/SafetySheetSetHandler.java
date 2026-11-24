package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.SheetSetAddParam;
import com.yutong.safety.common.param.web.SheetSetParam;
import com.yutong.safety.common.vo.SafetyHeartVO;
import com.yutong.safety.core.service.SafetySheetSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  create gwcuiyx 2023.07.12
 *  表单设置--handler
 */
@Component
public class SafetySheetSetHandler {

    @Autowired
    private SafetySheetSetService safetySheetSetService;

    /**
     * 数据列表查找操作功能
     * @return
     */
    public Object sheetSetList() {
        return safetySheetSetService.sheetSetList();
    }

    /**
     * 移动端获取有效数据
     * @return
     */
    public Object appList() {
        return safetySheetSetService.appList();
    }

    /**
     * 数据列表更新操作
     * @param param
     * @return
     */
    public Object updateSheetSet(SheetSetParam param, String tocken) {
        safetySheetSetService.updateSheetSet(param, tocken);
        return null;
    }

    /**
     * 数据整体新增或者更新
     * @return
     */
    public Object saveOrUpdate(String sheetSet, String tocken) {
        safetySheetSetService.saveOrUpdate(sheetSet, tocken);
        return null;
    }

    /**
     * 数据添加功能
     * @param param
     * @return
     */
    public Object save(SheetSetAddParam param, String tocken) {
        safetySheetSetService.save(param, tocken);
        return null;
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        safetySheetSetService.delete(id, tocken);
        return null;
    }

    /**
     * 心跳检查接口
     * @return
     */
    public SafetyHeartVO getHeartBeatInfo() {
        SafetyHeartVO safetyHeartVO = new SafetyHeartVO();
        long num = safetySheetSetService.getHeartBeatInfo();
        safetyHeartVO.setStatus(num >= 0 ? "UP" : "DOWN");
        return safetyHeartVO;
    }

}
