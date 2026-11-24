package com.yutong.modules.vehicle.mapper;

import java.util.List;

import com.yutong.modules.entity.SafetyEmployeeMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 人员信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
public interface SafetyEmployeeMessageJobMapper extends BaseMapper<SafetyEmployeeMessage> {

    /**
     * 批量新增/更新员工信息（按 emp_no upsert）
     * @param employeeList 员工信息列表
     * @return 受影响行数
     */
    int batchUpdateNew(@Param("employeeList") List<SafetyEmployeeMessage> employeeList);
}
