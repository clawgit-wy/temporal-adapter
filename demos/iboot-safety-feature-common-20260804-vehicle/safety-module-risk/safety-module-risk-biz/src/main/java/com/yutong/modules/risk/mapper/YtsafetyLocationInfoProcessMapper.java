package com.yutong.modules.risk.mapper;

import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcess;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 位置信息流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface YtsafetyLocationInfoProcessMapper extends BaseMapper<YtsafetyLocationInfoProcess> {

    /**
     * 通过主键查询流程数据
     * @param id
     * @return
     */
    YtsafetyLocationInfoProcessVO queryById(@Param("id") Long id);

}
