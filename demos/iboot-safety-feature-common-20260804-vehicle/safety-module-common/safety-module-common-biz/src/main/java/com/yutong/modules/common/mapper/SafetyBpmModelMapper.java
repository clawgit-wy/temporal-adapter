package com.yutong.modules.common.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.entity.SafetyBpmModel;
import com.yutong.modules.common.vo.SafetyBpmModelVO;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: BPM模型内容
 * @Author: lvss
 * @Date:   2026-06-18
 * @Version: V1.0
 */
public interface SafetyBpmModelMapper extends BaseMapper<SafetyBpmModel> {

    @Select("select count(1) from safety_bpm_model where model_id=#{modelId}")
    public Boolean isModelExist(@Param("modelId") String modelId);

    @Select("select * from safety_bpm_model where id=#{id}")
    public SafetyBpmModelVO queryById(@Param("id") Long id);

    @Select("select id from safety_sys_user where emp_no=#{empNo} and delete_falg='0' limit 1 ")
    public Long queryUserIdByempNo(@Param("empNo") String empNo);

    @Select("select * from safety_bpm_model where model_id=#{modelId}")
    public SafetyBpmModel queryModelByFlag(@Param("modelId") String modelId);

    @Select("select perm_url from safety_sys_perm where id =#{id}")
    public String queryModelFormUrl(@Param("id") Long id);

    /**
     * 获取用户流程菜单Id
     * @param userId
     * @return
     */
    public List<Long> queryUserFlowPerm(@Param("userId") Long userId);

    /**
     * 获取用户流程菜单Id
     * @param roleCode
     * @return
     */
    public List<Long> queryUserFlowPermByRole(@Param("roleCode") String roleCode);

    public IPage<SafetyBpmModelVO> queryPage(Page<SafetyBpmModelVO> page, @Param(Constants.WRAPPER) Wrapper<SafetyBpmModel> wrapper);

    public List<SafetyBpmModelVO> queryList(@Param(Constants.WRAPPER) Wrapper<SafetyBpmModel> wrapper);

}
