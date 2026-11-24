package com.yutong.modules.user.mapper;

import java.util.List;

import com.yutong.modules.user.entity.YtsafetyResponsibleProcessDetails;
import com.yutong.modules.user.vo.YtsafetyResponsibleProcessDetailsVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 安全责任人变更流程明细表
 * @Author: lvss
 * @Date:   2026-06-22
 * @Version: V1.0
 */
public interface YtsafetyResponsibleProcessDetailsMapper extends BaseMapper<YtsafetyResponsibleProcessDetails> {

    public List<YtsafetyResponsibleProcessDetailsVO> queryList(@Param("processId") Long processId);

    @Delete("delete from ytsafety_responsible_process_details where process_id=#{processId}")
    public void deleteProcessDetailsByProcessId(@Param("processId") Long processId);

}
