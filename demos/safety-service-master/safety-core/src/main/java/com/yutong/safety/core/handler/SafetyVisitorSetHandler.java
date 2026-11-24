package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyVisitorSetDO;
import com.yutong.safety.core.service.SafetyVisitorSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-4-2 17:04
 */
@Component
public class SafetyVisitorSetHandler {
    @Autowired
    private SafetyVisitorSetService safetyVisitorSetService;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(VisitorSetListParam param) {
        List<SafetyVisitorSetDO> list = safetyVisitorSetService.pageList(param);
        long total = safetyVisitorSetService.pageListCount(param);
        return new PageVO<SafetyVisitorSetDO>(total, list);
    }

    /**
     * 新增
     * @param param
     * @return
     */
    public Object add(VisitorSetAddParam param) {
        safetyVisitorSetService.add(param);
        return null;
    }

    /**
     * 更新
     * @param param
     * @return
     */
    public Object update(VisitorSetUpdateParam param) {
        safetyVisitorSetService.update(param);
        return null;
    }

    /**
     * 数据删除
     * @param param
     * @return
     */
    public Object delete(VisitorSetDelParam param) {
        safetyVisitorSetService.delete(param);
        return null;
    }
}
