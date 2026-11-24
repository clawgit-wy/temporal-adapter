package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.entity.YTSafetyRiskLevelDict;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: 风险等级字典表 Mapper
 * @Author: jeecg-boot
 * @Version: V1.0
 */
@Mapper
public interface YTSafetyRiskLevelDictMapper extends BaseMapper<YTSafetyRiskLevelDict> {

    /**
     * 查询所有风险等级字典，按风险值升序排列
     * @return 风险等级字典列表
     */
    List<YTSafetyRiskLevelDict> selectAllOrderByRiskValue();
}
