package com.yutong.modules.risk.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.risk.entity.YTSafetyDutyCheckin;
import com.yutong.modules.risk.entity.YTSafetyShiftSchedule;
import org.jeecg.common.exception.JeecgBootException;
import com.yutong.modules.risk.mapper.YTSafetyDutyCheckinMapper;
import com.yutong.modules.risk.mapper.YTSafetyShiftScheduleMapper;
import com.yutong.modules.risk.service.IYTSafetyShiftScheduleService;
import com.yutong.modules.risk.vo.ShiftScheduleImportVO;
import com.yutong.modules.risk.vo.ShiftSchedulePageVO;
import com.yutong.modules.risk.web.ShiftScheduleAddParam;
import com.yutong.modules.risk.web.ShiftScheduleListParam;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 排班表 Service 实现类
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Service
public class YTSafetyShiftScheduleServiceImpl extends ServiceImpl<YTSafetyShiftScheduleMapper, YTSafetyShiftSchedule> implements IYTSafetyShiftScheduleService {

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Autowired
    private YTSafetyDutyCheckinMapper dutyCheckinMapper;

    @Override
    public IPage<ShiftSchedulePageVO> pageList(Page<ShiftSchedulePageVO> page, ShiftScheduleListParam param) {
        IPage<ShiftSchedulePageVO> result = baseMapper.selectPageList(page, param);
        formatScheduleDates(result.getRecords());
        return result;
    }

    @Override
    public List<ShiftSchedulePageVO> selectList(ShiftScheduleListParam param) {
        List<ShiftSchedulePageVO> list = baseMapper.selectVoList(param);
        formatScheduleDates(list);
        return list;
    }

    @Override
    public Long save(ShiftScheduleAddParam param) {
        checkDuplicate(param);
        YTSafetyShiftSchedule entity = new YTSafetyShiftSchedule();
        BeanUtils.copyProperties(param, entity);
        entity.setDelFlag(0);
        entity.setCreateTime(new Date());
        baseMapper.insert(entity);
        syncDutyCheckin(param);
        return entity.getId();
    }

    @Override
    public int update(ShiftScheduleAddParam param) {
        checkDuplicate(param);
        YTSafetyShiftSchedule entity = new YTSafetyShiftSchedule();
        BeanUtils.copyProperties(param, entity);
        entity.setUpdateTime(new Date());
        int result = baseMapper.updateById(entity);
        syncDutyCheckin(param);
        return result;
    }

    /**
     * 检查排班记录是否已存在（同一日期+同一人员编号）
     */
    private void checkDuplicate(ShiftScheduleAddParam param) {
        YTSafetyShiftSchedule exist = lambdaQuery()
                .eq(YTSafetyShiftSchedule::getScheduleDate, param.getScheduleDate())
                .eq(YTSafetyShiftSchedule::getEmpNo, param.getEmpNo())
                .one();
        if (exist != null && !exist.getId().equals(param.getId())) {
            throw new JeecgBootException("排班记录已存在");
        }
    }

    /**
     * 同步更新对应日期的在岗签到记录的人员信息
     */
    private void syncDutyCheckin(ShiftScheduleAddParam param) {
        LambdaUpdateWrapper<YTSafetyDutyCheckin> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(YTSafetyDutyCheckin::getEmpNo, param.getEmpNo())
               .eq(YTSafetyDutyCheckin::getDutyDate, param.getScheduleDate())
               .eq(YTSafetyDutyCheckin::getDelFlag, 0);
        wrapper.set(YTSafetyDutyCheckin::getEmpName, param.getEmpName())
               .set(YTSafetyDutyCheckin::getPersonType, param.getPersonType())
               .set(YTSafetyDutyCheckin::getPosition, param.getPosition())
               .set(YTSafetyDutyCheckin::getFactoryId, param.getFactoryId())
               .set(YTSafetyDutyCheckin::getFactory, param.getFactory());
        dutyCheckinMapper.update(null, wrapper);
    }

    @Override
    public ShiftSchedulePageVO queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public int deleteById(Long id) {
        return baseMapper.deleteById(id);
    }


    @Override
    public int importData(List<ShiftScheduleImportVO> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        // 校验导入数据内部 & 数据库是否已存在相同日期+人员编号的排班记录
        Set<String> importKeys = new HashSet<>();
        Set<String> dbKeys = lambdaQuery()
                .in(YTSafetyShiftSchedule::getScheduleDate,
                        list.stream().map(v -> formatDate(v.getScheduleDate())).distinct().collect(Collectors.toList()))
                .list().stream()
                .map(e -> e.getScheduleDate() + "_" + e.getEmpNo())
                .collect(Collectors.toSet());

        for (ShiftScheduleImportVO vo : list) {
            String key = formatDate(vo.getScheduleDate()) + "_" + vo.getEmpNo();
            if (!importKeys.add(key)) {
                throw new JeecgBootException("导入数据中存在重复排班记录：" + vo.getScheduleDate() + " " + vo.getEmpNo());
            }
            if (dbKeys.contains(key)) {
                throw new JeecgBootException("排班记录已存在：" + vo.getScheduleDate() + " " + vo.getEmpNo());
            }
        }

        // 厂区名称 → ID 映射
        Map<String, Long> factoryMap = safetyCommonService.queryFactoryFilterDrop().stream()
                .filter(f -> StringUtils.isNotBlank(f.getFieldName()))
                .collect(Collectors.toMap(SafetyFieldManageVO::getFieldName, SafetyFieldManageVO::getId, (a, b) -> a));

        LoginUser loginUser = BusUtits.getLoginUser();
        String createUser = loginUser != null ? loginUser.getEmpNo() : "";
        String createBy = loginUser != null ? loginUser.getRealname() : "";
        Date now = new Date();

        List<YTSafetyShiftSchedule> entityList = new ArrayList<>();
        for (ShiftScheduleImportVO vo : list) {
            YTSafetyShiftSchedule entity = new YTSafetyShiftSchedule();
            entity.setScheduleDate(formatDate(vo.getScheduleDate()));
            entity.setEmpNo(vo.getEmpNo());
            // 人员类型转换 在岗人员→1 安保带班→2 消防值班→3
            switch (vo.getPersonType()) {
                case "在岗人员":
                    entity.setPersonType("1");
                    break;
                case "安保带班":
                    entity.setPersonType("2");
                    break;
                case "消防值班":
                    entity.setPersonType("3");
                    break;
                default:
                    entity.setPersonType(vo.getPersonType());
                    break;
            }
            entity.setFactory(vo.getFactory());
            entity.setShiftCode(vo.getShiftCode());
            // 通过厂区名称匹配 factoryId
            if (StringUtils.isNotBlank(vo.getFactory())) {
                Long factoryId = factoryMap.get(vo.getFactory());
                if (factoryId != null) {
                    entity.setFactoryId(factoryId);
                }
            }
            entity.setCreateTime(now);
            entity.setCreateUser(createUser);
            entity.setCreateBy(createBy);
            entityList.add(entity);
        }
        saveBatch(entityList);
        return entityList.size();
    }

    /**
     * 将导入的日期字符串统一格式化为 yyyy-MM-dd
     */
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
            DateTimeFormatter.ofPattern("yyyy年MM月dd日"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("yyyy.M.d"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("M/d/yyyy")
    );

    private String formatDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return dateStr;
        }
        String trimmed = dateStr.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(trimmed, formatter);
                return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ignored) {
            }
        }
        return trimmed;
    }

    /**
     * 格式化 scheduleDate 为 "yyyy-MM-dd 周X"
     */
    private void formatScheduleDates(List<ShiftSchedulePageVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        for (ShiftSchedulePageVO item : list) {
            if (StringUtils.isNotBlank(item.getScheduleDate())) {
                try {
                    Date date = parseSdf.parse(item.getScheduleDate());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    item.setScheduleDate(item.getScheduleDate() + " " + weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1]);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
