package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcessDetails;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessDetailsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 位置信息变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface YtsafetyLocationInfoProcessDetailsMapper extends BaseMapper<YtsafetyLocationInfoProcessDetails> {

    /**
     * 根据流程主键删除详情数据
     * @param processId
     */
    void deleteByProcessId(@Param("processId") Long processId);

    /**
     * 根据流程主键查询详情列表
     * @param processId
     * @return
     */
    List<YtsafetyLocationInfoProcessDetailsVO> queryList(@Param("processId") Long processId);

    /**
     * 根据位置编码列表查询详情列表
     * @param locationCodes 位置编码列表
     * @return
     */
    List<YtsafetyLocationInfoProcessDetailsVO> queryByLocationCodes(@Param("locationCodes") List<String> locationCodes);

    /**
     * 批量插入明细数据（单条多值 INSERT，性能远优于逐条 saveBatch）
     * @param list 明细实体列表
     * @return 插入行数
     */
    int batchInsert(@Param("list") List<YtsafetyLocationInfoProcessDetails> list);

}
