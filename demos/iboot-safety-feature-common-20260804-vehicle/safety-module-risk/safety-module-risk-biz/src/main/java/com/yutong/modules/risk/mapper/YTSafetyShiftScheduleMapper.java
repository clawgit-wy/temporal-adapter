package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.entity.YTSafetyShiftSchedule;
import com.yutong.modules.risk.vo.ShiftSchedulePageVO;
import com.yutong.modules.risk.web.ShiftScheduleListParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 排班表 Mapper 接口
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
public interface YTSafetyShiftScheduleMapper extends BaseMapper<YTSafetyShiftSchedule> {

    /**
     * 分页查询（关联员工表查询姓名、岗位）
     * @param page 分页对象
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<ShiftSchedulePageVO> selectPageList(Page<ShiftSchedulePageVO> page, @Param("param") ShiftScheduleListParam param);

    /**
     * 查询列表（关联员工表查询姓名、岗位）
     * @param param 查询参数
     * @return 列表
     */
    List<ShiftSchedulePageVO> selectVoList(@Param("param") ShiftScheduleListParam param);

    /**
     * 根据 ID 查询（关联员工表查询姓名、岗位）
     * @param id 主键
     * @return VO
     */
    ShiftSchedulePageVO selectVoById(@Param("id") Long id);
}
