package com.yutong.safety.core.handler.goodsOut;

import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutValidDTO;

import java.io.OutputStream;

/**
 * @author caomg
 * @date 2024-9-26 14:44
 * @description SafetyGoodsOutValidHandler
 */
public interface SafetyGoodsOutValidHandler {
    //查询
    Object getList(SafetyValidateEnvironmentDO en);
    Object getPageList(SafetyGoodsOutValidDTO en,String token);

    //导出
    void exportExcel(SafetyValidateEnvironmentDO en, OutputStream outputStream);

    //业务验证详情页
    Object getDetailInfo(String processId,String businessType);
    Object getDetailInfoAll(String processId);

    //搜索货物明细
    Object getGoodsDetail(String processId, String selectContent);

    //业务详情保存
    Object saveDetailInfo(SafetyValidateEnvironmentDO en);
    void updateDetails(SafetyValidateEnvironmentDO en);
}
