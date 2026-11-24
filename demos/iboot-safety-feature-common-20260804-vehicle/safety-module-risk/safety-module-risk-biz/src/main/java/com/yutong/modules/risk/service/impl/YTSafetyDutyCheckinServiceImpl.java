package com.yutong.modules.risk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.bpm.ISafetyBpmModelService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.entity.SafetyBpmModel;
import com.yutong.modules.risk.entity.YTSafetyDutyCheckin;
import com.yutong.modules.risk.entity.YTSafetyShiftSchedule;
import com.yutong.modules.risk.mapper.YTSafetyDutyCheckinMapper;
import com.yutong.modules.risk.mapper.YTSafetyShiftScheduleMapper;
import com.yutong.modules.risk.service.IYTSafetyDutyCheckinService;
import com.yutong.modules.risk.service.YTSafetyWaterFlowNoService;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.risk.web.DutyCheckinAddParam;
import com.yutong.modules.risk.web.DutyCheckinListParam;
import com.yutong.modules.risk.web.MakeupApplyParam;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.mapper.SafetyEmployeeMessageMapper;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description: 在岗签到表 Service 实现类
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Service
public class YTSafetyDutyCheckinServiceImpl extends ServiceImpl<YTSafetyDutyCheckinMapper, YTSafetyDutyCheckin> implements IYTSafetyDutyCheckinService {

    private static final String MODEL_FORM_ID = "ytsafety_duty_checkin";
    private static final String FLOW_CODE_PREFIX = "BG";

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    private YTSafetyWaterFlowNoService ytSafetyWaterFlowNoService;

    @Autowired
    private ISafetyBpmModelService safetyBpmModelService;

    @Autowired
    private YTSafetyShiftScheduleMapper shiftScheduleMapper;

    @Autowired
    private SafetyEmployeeMessageMapper employeeMessageMapper;

    @Override
    public IPage<DutyCheckinPageVO> pageList(Page<DutyCheckinPageVO> page, DutyCheckinListParam param) {
        param.setToday(param.getDutyDate() != null && param.getDutyDate().equals(LocalDate.now().toString()));
        return baseMapper.selectPageListVO(page, param);
    }

    @Override
    public List<DutyCheckinExportVO> selectListExport(DutyCheckinListParam param) {
        param.setToday(param.getDutyDate() != null && param.getDutyDate().equals(LocalDate.now().toString()));
        return baseMapper.selectListExport(param);
    }

    @Override
    public SecurityDutyPersonVO querySecurityDutyPerson(String factory) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return baseMapper.selectDutyPersonByFactory(factory, today);
    }

    @Override
    public Long checkin(DutyCheckinAddParam param) {
        LoginUser loginUser = BusUtits.getLoginUser();
        String empNo = loginUser.getEmpNo();
        String empName = loginUser.getRealname();
        String dutyDate = param.getDutyDate();

        // 查询是否已有签到记录
        YTSafetyDutyCheckin entity = baseMapper.selectOne(
                new LambdaQueryWrapper<YTSafetyDutyCheckin>()
                        .eq(YTSafetyDutyCheckin::getEmpNo, empNo)
                        .eq(YTSafetyDutyCheckin::getDutyDate, dutyDate)
                        .eq(YTSafetyDutyCheckin::getDelFlag, 0).last("limit 1")
        );

        if (entity != null) {
            // 已有记录：更新签到信息
            entity.setCheckinTime(new Date());
            entity.setDutyStatus(1);
            entity.setFactoryId(param.getFactoryId());
            entity.setFactory(param.getFactory());
            entity.setDutyDate(dutyDate);
            entity.setLocation(param.getLocation());
            entity.setUpdateTime(new Date());
            entity.setUpdateBy(empName);
            entity.setUpdateUser(empNo);
            baseMapper.updateById(entity);
            return entity.getId();
        }

//        // 从排班表查询
//        YTSafetyShiftSchedule schedule = shiftScheduleMapper.selectOne(
//                new LambdaQueryWrapper<YTSafetyShiftSchedule>()
//                        .eq(YTSafetyShiftSchedule::getEmpNo, empNo)
//                        .eq(YTSafetyShiftSchedule::getScheduleDate, dutyDate).last("limit 1")
//        );
//
//        if (schedule != null) {
//            // 排班存在：根据排班记录创建签到记录
//            entity = new YTSafetyDutyCheckin();
//            entity.setFactoryId(schedule.getFactoryId() != null ? schedule.getFactoryId() : param.getFactoryId());
//            entity.setEmpNo(schedule.getEmpNo());
//            entity.setPersonType(schedule.getPersonType());
//            entity.setFactory(schedule.getFactory() != null ? schedule.getFactory() : param.getFactory());
//            entity.setLocation(param.getLocation());
//            entity.setShiftCode(schedule.getShiftCode());
//            entity.setDutyDate(dutyDate);
//            entity.setCheckinTime(new Date());
//            entity.setDutyStatus(1);
//            entity.setCreateTime(new Date());
//            entity.setCreateBy(empName);
//            entity.setCreateUser(empNo);
//            baseMapper.insert(entity);
//            return entity.getId();
//        }

        // 3. 不在打卡范围内
        throw new RuntimeException("不在打卡范围内");
    }

    @Override
    public int makeupApply(MakeupApplyParam param) {
        YTSafetyDutyCheckin entity = baseMapper.selectById(param.getId());
        if (entity == null) {
            throw new RuntimeException("签到记录不存在");
        }
        entity.setIsMakeup(1); // 1-是补签
        entity.setApplyReason(param.getApplyReason());
        entity.setApproveUser(param.getApproveUser());
        entity.setApplyTime(new Date());
        entity.setUpdateTime(new Date());

        // 创建并提交流程
        LoginUser user = BusUtits.getLoginUser();
        String flowCode = generateMakeupFlowCode();
        String flowSubject = DateUtils.formatDate() + user.getRealname() + "发起的补岗申请";

        SafetyBpmModel safetyBpmModel= safetyBpmModelService.list(
                new LambdaQueryWrapper<SafetyBpmModel>()
                        .eq(SafetyBpmModel::getFormId, MODEL_FORM_ID)
                        .last("limit 1")).get(0);

        BpmRequestParamsDTO dto = new BpmRequestParamsDTO();
        dto.setApplyEipName(user.getEipName());
        dto.setModelFlag(safetyBpmModel.getModelId());
        dto.setFormInstanceId(entity.getId());
        dto.setFlowCode(flowCode);
        dto.setFlowSubject(flowSubject);
        dto.setCurNodeHandler(user.getEipName());
        dto.setStartDate(new Date());
        dto.setApplyNo(user.getEmpNo());
        dto.setLoginName(user.getEipName());
        dto.setApplyName(user.getRealname());
        dto.setApplyDepartCode(user.getOrgCode());
        dto.setApplyDepartName(user.getOrgName());
        dto.setPreNodeHandler(user.getEipName());
        dto.setPreNodeHandlerName(user.getRealname() + "(" + user.getEmpNo() + ")");
        safetySummaryProcessService.createAndApproveProcess(dto);

        // 保存流程信息到实体
        entity.setModelFlag(safetyBpmModel.getModelId());
        entity.setFlowCode(flowCode);
        entity.setFlowSubject(flowSubject);

        return baseMapper.updateById(entity);
    }

    /**
     * 生成补岗申请流程编码
     */
    private String generateMakeupFlowCode() {
        String dateStr = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String sequence = ytSafetyWaterFlowNoService.getNowByTypeDayNum("MakeupFlowCode",
                DateUtils.parseShortDate(DateUtils.formatDate(new Date())));
        return String.format("%s%s%s", FLOW_CODE_PREFIX, dateStr, sequence);
    }

    @Override
    public DutyCheckinDetailVO queryDetailById(Long id) {
        YTSafetyDutyCheckin entity = baseMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        DutyCheckinDetailVO vo = new DutyCheckinDetailVO();
        BeanUtils.copyProperties(entity, vo);
        // 从员工表填充姓名、岗位和手机号
        SafetyEmployeeMessage emp = employeeMessageMapper.selectOne(
                new LambdaQueryWrapper<SafetyEmployeeMessage>()
                        .eq(SafetyEmployeeMessage::getEmpNo, entity.getEmpNo())
                        .select(SafetyEmployeeMessage::getEmpNo,
                                SafetyEmployeeMessage::getEmpName,
                                SafetyEmployeeMessage::getPositionName,
                                SafetyEmployeeMessage::getPhoneNo));
        if (emp != null) {
            vo.setEmpName(emp.getEmpName());
            vo.setPosition(emp.getPositionName());
            vo.setPhoneNo(emp.getPhoneNo());
        }
        return vo;
    }

    @Override
    public int approve(BpmProcessParamDTO processParamDTO, String approveUser) {
        YTSafetyDutyCheckin entity = baseMapper.selectById(processParamDTO.getFormInstanceId());
        if ("handler_pass".equals(processParamDTO.getOperationType())){
            // 1-在岗
            entity.setDutyStatus(1);
        }
        entity.setApproveUser(approveUser);
        entity.setUpdateTime(new Date());
        // 审批通过后更新岗位状态
        return baseMapper.updateById(entity);
    }

    @Override
    public DutyCheckinMonthlyVO queryMonthlyRecords(String month) {
        month = new SimpleDateFormat("yyyy-MM").format(new Date());
        List<DutyCheckinMonthVO> list = baseMapper.selectByMonthVO(month);

        DutyCheckinMonthlyVO result = new DutyCheckinMonthlyVO();
        int inDutyCount = 0;
        int notInDutyCount = 0;
        int makeupCount = 0;

        List<DutyCheckinRecordVO> records = new ArrayList<>();
        SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatSdf = new SimpleDateFormat("MM-dd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

        for (DutyCheckinMonthVO item : list) {
            // 统计计数
            if (item.getDutyStatus() != null && item.getDutyStatus() == 1) {
                inDutyCount++;
            } else {
                notInDutyCount++;
            }
            if (item.getIsMakeup() != null && item.getIsMakeup() == 1) {
                makeupCount++;
            }

            // 构建记录条目
            DutyCheckinRecordVO record = new DutyCheckinRecordVO();
            record.setEmpNo(item.getEmpNo());
            record.setEmpName(item.getEmpName());
            record.setPosition(item.getPosition());
            record.setFactory(item.getFactory());
            record.setLocation(item.getLocation());
            record.setPersonType(item.getPersonType());
            record.setIsMakeup(item.getIsMakeup());
            record.setDutyStatusText(item.getDutyStatus() != null && item.getDutyStatus() == 1 ? "在岗" : "未到岗");
            if (item.getCheckinTime() != null) {
                record.setCheckinTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getCheckinTime()));
            }

            // 格式化日期：07-09 星期二
            try {
                if (item.getDutyDate() != null && !item.getDutyDate().isEmpty()) {
                    Date date = parseSdf.parse(item.getDutyDate());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    String formatted = formatSdf.format(date) + " " + weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1];
                    record.setDutyDate(formatted);
                }
            } catch (Exception e) {
                record.setDutyDate(item.getDutyDate());
            }

            records.add(record);
        }

        result.setInDutyCount(inDutyCount);
        result.setNotInDutyCount(notInDutyCount);
        result.setMakeupCount(makeupCount);
        result.setRecords(records);
        return result;
    }

    @Override
    public DutyCheckinTodayVO queryTodayStatus(String empNo) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 格式化今日日期：2026年08月10日 星期三
        Calendar cal = Calendar.getInstance();
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String todayFormatted = new SimpleDateFormat("yyyy年MM月dd日").format(new Date())
                + " " + weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1];

        DutyCheckinTodayVO vo = new DutyCheckinTodayVO();
        vo.setTodayDate(todayFormatted);

        // 从签到表查询上岗状态
        YTSafetyDutyCheckin checkin = baseMapper.selectOne(
                new LambdaQueryWrapper<YTSafetyDutyCheckin>()
                        .eq(YTSafetyDutyCheckin::getEmpNo, empNo)
                        .eq(YTSafetyDutyCheckin::getDutyDate, today)
                        .eq(YTSafetyDutyCheckin::getDelFlag, 0).last("limit 1")
        );

        if (checkin != null) {
            vo.setDutyStatus(checkin.getDutyStatus());
        } else {
            vo.setDutyStatus(0);
        }

        // 从排班表查询当日人员类型
        YTSafetyShiftSchedule schedule = shiftScheduleMapper.selectOne(
                new LambdaQueryWrapper<YTSafetyShiftSchedule>()
                        .eq(YTSafetyShiftSchedule::getEmpNo, empNo)
                        .eq(YTSafetyShiftSchedule::getScheduleDate, today)
        );

        if (schedule != null) {
            vo.setPersonType(schedule.getPersonType());
        }

        return vo;
    }
}
