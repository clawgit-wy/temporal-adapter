package com.yutong.modules.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.dto.SafetyEmployeePrivateCarQueryDTO;
import com.yutong.modules.vehicle.entity.SafetyEmployeePrivateCar;
import com.yutong.modules.vehicle.vo.SafetyEmployeePrivateCarQueryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 员工入厂私家车信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-05
 * @Version: V1.0
 */
public interface SafetyEmployeePrivateCarMapper extends BaseMapper<SafetyEmployeePrivateCar> {

    Page<SafetyEmployeePrivateCarQueryVO> listPage(@Param("page")Page<SafetyEmployeePrivateCarQueryVO> page, @Param("query")SafetyEmployeePrivateCarQueryDTO query);
}
