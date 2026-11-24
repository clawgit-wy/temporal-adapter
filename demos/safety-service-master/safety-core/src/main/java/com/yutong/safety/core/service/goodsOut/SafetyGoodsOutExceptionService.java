package com.yutong.safety.core.service.goodsOut;

import com.alibaba.excel.EasyExcel;
import com.yutong.safety.common.easyExcel.ColumnHeightAuto;
import com.yutong.safety.common.easyExcel.ColumnWidthAuto;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutExceptionDO;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutExceptionDOMapper;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author caomg
 * @date 2024-9-24 15:06
 * @description SafetyGoodsOutExceptionService
 */
@Service
public class SafetyGoodsOutExceptionService implements SafetyGoodsOutExceptionHandler {
    @Autowired
    SafetyGoodsOutExceptionDOMapper safetyGoodsOutExceptionDOMapper;

    @Override
    public Object getList(SafetyGoodsOutExceptionDO en) {
        List<SafetyGoodsOutExceptionDO> list = safetyGoodsOutExceptionDOMapper.selectByExampleList(en);
        Integer total = safetyGoodsOutExceptionDOMapper.selectByExampleCount(en);

        return new PageVO<>(total, list);
    }

    @Override
    public void exportExcel(SafetyGoodsOutExceptionDO en, OutputStream outputStream) {
        en.setIsExport("Y");

        List<SafetyGoodsOutExceptionDO> list = safetyGoodsOutExceptionDOMapper.selectByExampleList(en);

        EasyExcel.write(outputStream, SafetyGoodsOutExceptionDO.class)
                .registerWriteHandler(new ColumnHeightAuto())
                .registerWriteHandler(new ColumnWidthAuto())
                .sheet("Sheet1")
                .doWrite(list);
    }

    @Override
    public List<SafetyGoodsOutExceptionDO> selectByExampleList(SafetyGoodsOutExceptionDO en) {
         return safetyGoodsOutExceptionDOMapper.selectByExampleList(en);
    }

    @Override
    public void insert(SafetyGoodsOutExceptionDO en) {
        en.setCreateTime(new Date());
        safetyGoodsOutExceptionDOMapper.insert(en);
    }

    @Override
    public void update(SafetyGoodsOutExceptionDO en) {
        safetyGoodsOutExceptionDOMapper.updateByPrimaryKeySelective(en);
    }
}
