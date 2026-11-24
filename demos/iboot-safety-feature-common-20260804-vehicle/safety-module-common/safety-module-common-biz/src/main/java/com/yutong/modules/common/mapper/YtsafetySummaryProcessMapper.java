package com.yutong.modules.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.entity.YtsafetySummaryProcess;
import com.yutong.modules.common.vo.YtsafetySummaryProcessVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @Description: 流程汇总信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
public interface YtsafetySummaryProcessMapper extends BaseMapper<YtsafetySummaryProcess> {

    /**
     * 我的
     * @param page
     * @param wrapper
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryMyApply(Page<YtsafetySummaryProcessVO> page, @Param(Constants.WRAPPER) Wrapper<YtsafetySummaryProcess> wrapper);


    /**
     * 待办
     * @param page
     * @param wrapper
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryProcessTodo(Page<YtsafetySummaryProcessVO> page, @Param(Constants.WRAPPER) Wrapper<YtsafetySummaryProcess> wrapper,@Param("empNo") String empNo);

    /**
     * 已办
     * @param page
     * @param wrapper
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryProcessCompleted(Page<YtsafetySummaryProcessVO> page, @Param(Constants.WRAPPER) Wrapper<YtsafetySummaryProcess> wrapper,@Param("empNo") String empNo);

    /**
     * 所有
     * @param page
     * @param wrapper
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryAllProcess(Page<YtsafetySummaryProcessVO> page, @Param(Constants.WRAPPER) Wrapper<YtsafetySummaryProcess> wrapper,@Param("empNo") String empNo);

    /**
     * 草稿箱
     * @param page
     * @param wrapper
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryProcessDraft(Page<YtsafetySummaryProcessVO> page, @Param(Constants.WRAPPER) Wrapper<YtsafetySummaryProcess> wrapper);


    @Delete("delete from ytsafety_summary_process where id=#{id} and flow_status=0;")
    public void deleteDraftById(@Param("id") Long id,@Param("tableCode") String tableCode);

    @Delete("delete from ${tableCode} where id=#{id}; ")
    public void deleteDraftExtendById(@Param("id") Long id,@Param("tableCode") String tableCode);

    @Delete("delete from ${tableCode} where process_id=#{id}")
    public void deleteDraftExtendDetailsById(@Param("id") Long id,@Param("tableCode") String tableCode);

    @Update("UPDATE ytsafety_summary_process SET flow_status =#{status} WHERE id = #{id}")
    int updateSummaryProcessStatus(@Param("status") int status, @Param("id") String id);

}
