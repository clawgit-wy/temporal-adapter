package com.yutong.modules.common.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.entity.SafetyBpmCallBack;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.common.vo.SafetyBpmCallBackVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 对接BPM回调方法表
 * @Author: lvss
 * @Date:   2026-06-18
 * @Version: V1.0
 */
public interface SafetyBpmCallBackMapper extends BaseMapper<SafetyBpmCallBack> {

    @Select("select count(1) from safety_bpm_call_back where model_id=#{modelId} and function_id=#{functionId} ")
    public Boolean isFuncExist(@Param("functionId") String functionId,@Param("modelId") String modelFlag);

    @Select("select * from safety_bpm_call_back where id=#{id}")
    public SafetyBpmCallBackVO queryById(@Param("id") Long id);

    public List<SafetyBpmCallBackVO> queryList(@Param(Constants.WRAPPER) Wrapper<SafetyBpmCallBack> wrapper);

    public IPage<SafetyBpmCallBackVO> queryPage(Page<SafetyBpmCallBackVO> page, @Param(Constants.WRAPPER) Wrapper<SafetyBpmCallBack> wrapper);
}
