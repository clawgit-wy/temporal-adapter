package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YtsafetyRiskDataQueryDTO;
import com.yutong.modules.risk.entity.YtsafetyRiskData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.vo.YtsafetyRiskDataExportVO;
import com.yutong.modules.risk.vo.YtsafetyRiskDataVO;
import com.yutong.modules.risk.vo.YtsafetyTaskRiskDataVO;

import java.util.Date;
import java.util.List;

/**
 * @Description: 风险数据表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
public interface IYtsafetyRiskDataService extends IService<YtsafetyRiskData> {

    IPage<YtsafetyRiskDataVO> queryPageList(Page<YtsafetyRiskDataVO> page, YtsafetyRiskDataQueryDTO query);

    List<YtsafetyRiskDataVO> queryList(YtsafetyRiskDataQueryDTO query);

    YtsafetyRiskDataVO queryByDataId(Long id);

    /**
     * 推送更新时间超过半年的风险数据，按风险类型和厂区分组，发起批量推送流程
     */
    void pushOverdueData(Date currentDate);

}
