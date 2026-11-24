package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.entity.YTSafetyDutyCheckin;
import com.yutong.modules.risk.vo.DutyCheckinExportVO;
import com.yutong.modules.risk.vo.DutyCheckinMonthVO;
import com.yutong.modules.risk.vo.DutyCheckinPageVO;
import com.yutong.modules.risk.vo.SecurityDutyPersonVO;
import com.yutong.modules.risk.web.DutyCheckinListParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 在岗签到表 Mapper 接口
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
public interface YTSafetyDutyCheckinMapper extends BaseMapper<YTSafetyDutyCheckin> {

    /**
     * 分页查询
     * @param page 分页对象
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<YTSafetyDutyCheckin> selectPageList(Page<YTSafetyDutyCheckin> page, @Param("param") DutyCheckinListParam param);

    /**
     * 分页查询（直接返回 VO，含人员类型/岗位状态中文翻译）
     * @param page 分页对象
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<DutyCheckinPageVO> selectPageListVO(Page<DutyCheckinPageVO> page, @Param("param") DutyCheckinListParam param);

    /**
     * 按月份查询签到记录（LEFT JOIN 员工表，含姓名、岗位）
     * @param month 月份（yyyy-MM）
     * @return 月度签到 VO 列表
     */
    List<DutyCheckinMonthVO> selectByMonthVO(@Param("month") String month);

    /**
     * 导出查询（LEFT JOIN 员工表，直接返回导出 VO）
     * @param param 查询参数
     * @return 导出 VO 列表
     */
    List<DutyCheckinExportVO> selectListExport(@Param("param") DutyCheckinListParam param);

    /**
     * 根据厂区查询今日安保带班人员（关联员工表获取姓名和手机号，返回一条）
     * @param factory 厂区名称
     * @param dutyDate 值班日期（yyyy-MM-dd）
     * @return 安保带班人员信息
     */
    SecurityDutyPersonVO selectDutyPersonByFactory(@Param("factory") String factory,
                                                    @Param("dutyDate") String dutyDate);
}
