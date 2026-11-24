package com.yutong.safety.core.handler.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutExceptionDO;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.OutputStream;
import java.util.List;

/**
 * @author caomg
 * @date 2024-9-24 15:03
 * @description SafetyGoodsOutExceptionHandler
 */
public interface SafetyGoodsOutExceptionHandler {
    //页面查询
    Object getList(SafetyGoodsOutExceptionDO en);

    //导出Excel
    void exportExcel(SafetyGoodsOutExceptionDO en, OutputStream response);

    //列表查询
    List<SafetyGoodsOutExceptionDO> selectByExampleList(SafetyGoodsOutExceptionDO en);

    //新增
    void insert(@RequestBody SafetyGoodsOutExceptionDO en);
    void update(@RequestBody SafetyGoodsOutExceptionDO en);
}
