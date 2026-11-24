package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.risk.entity.YTSafetyDutyCheckin;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.risk.web.DutyCheckinAddParam;
import com.yutong.modules.risk.web.DutyCheckinListParam;
import com.yutong.modules.risk.web.MakeupApplyParam;

import java.util.List;

/**
 * @Description: 在岗签到表 Service 接口
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
public interface IYTSafetyDutyCheckinService extends IService<YTSafetyDutyCheckin> {

    /**
     * 分页查询（含人员类型/岗位状态中文翻译）
     * @param page 分页对象
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<DutyCheckinPageVO> pageList(Page<DutyCheckinPageVO> page, DutyCheckinListParam param);

    /**
     * 签到
     * @param param 签到参数
     * @return 签到记录ID
     */
    Long checkin(DutyCheckinAddParam param);

    /**
     * 补岗申请
     * @param param 补岗申请参数
     * @return 更新结果
     */
    int makeupApply(MakeupApplyParam param);

    /**
     * 审批
     * @param processParamDTO 审批参数
     * @param approveUser 审批人工号
     * @return 更新结果
     */
    int approve(BpmProcessParamDTO processParamDTO, String approveUser);

    /**
     * 按月份查询在岗记录
     * @param month 月份（yyyy-MM），为空时默认当前月份
     * @return 月度汇总数据
     */
    DutyCheckinMonthlyVO queryMonthlyRecords(String month);

    /**
     * 根据 ID 查询详情（关联员工表查询姓名、岗位）
     * @param id 主键
     * @return 详情 VO
     */
    DutyCheckinDetailVO queryDetailById(Long id);

    /**
     * 导出查询（LEFT JOIN 员工表，直接返回导出 VO）
     * @param param 查询参数
     * @return 导出 VO 列表
     */
    List<DutyCheckinExportVO> selectListExport(DutyCheckinListParam param);

    /**
     * 根据厂区查询今日安保带班人员信息
     * @param factory 厂区名称
     * @return 安保带班人员信息（单条）
     */
    SecurityDutyPersonVO querySecurityDutyPerson(String factory);

    /**
     * 查询今日在岗状态
     * @param empNo 员工号
     * @return 今日日期、上岗状态、人员类型及翻译
     */
    DutyCheckinTodayVO queryTodayStatus(String empNo);
}
