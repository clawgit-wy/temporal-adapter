package com.yutong.modules.risk.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoQueryDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.query.YtsafetyLocationInfoQuery;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoVO;

import java.util.List;

/**
 * @Description: 位置信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
public interface IYtsafetyLocationInfoService extends IService<YtsafetyLocationInfo> {

    IPage<YtsafetyLocationInfoVO> queryPageList(Page<YtsafetyLocationInfoVO> page, YtsafetyLocationInfoQueryDTO query);

    List<YtsafetyLocationInfoVO> queryList(YtsafetyLocationInfoQueryDTO query);

    YtsafetyLocationInfoVO queryById(Long id);

    void updateStatus(List<Long> ids, Integer status);

    /**
     * 更新风险等级
     * @param ids
     */
    public void syncLocationRiskLevel(List<Long> ids);
}
