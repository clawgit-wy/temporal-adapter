package com.yutong.modules.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.common.entity.SafetyProcessAbandonConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Description: BPM模型内容
 * @Author: lvss
 * @Date:   2026-06-18
 * @Version: V1.0
 */
public interface SafetyProcessAbandonConfiglMapper extends BaseMapper<SafetyProcessAbandonConfig> {

    @Select("select * from safety_process_abandon_config where model_id=#{modelId}")
    public SafetyProcessAbandonConfig queryProcessAbandonByModelId(@Param("modelId") String modelId);

    @Update("UPDATE ${tableName} SET ${paramKey} = #{paramValue} WHERE id = #{id}")
    int updateParamValue(
            @Param("paramKey") String paramKey,
            @Param("paramValue") String paramValue,
            @Param("tableName") String tableName,
            @Param("id") String id
    );

}
