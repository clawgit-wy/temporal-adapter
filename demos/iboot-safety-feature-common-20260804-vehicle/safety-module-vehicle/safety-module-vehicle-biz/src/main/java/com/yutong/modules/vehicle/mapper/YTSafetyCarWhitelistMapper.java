package com.yutong.modules.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.dto.CarWhitelistListParam;
import com.yutong.modules.vehicle.entity.YTSafetyCarWhitelist;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 车辆白名单表 Mapper 接口
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
public interface YTSafetyCarWhitelistMapper extends BaseMapper<YTSafetyCarWhitelist> {

    /**
     * 分页查询
     * @param page 分页对象
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<YTSafetyCarWhitelist> selectPageList(Page<YTSafetyCarWhitelist> page, @Param("param") CarWhitelistListParam param);

    /**
     * 查询列表
     * @param param 查询参数
     * @return 列表
     */
    List<YTSafetyCarWhitelist> selectList(@Param("param") CarWhitelistListParam param);

    /**
     * 根据ID查询
     * @param id 主键
     * @return 实体
     */
    YTSafetyCarWhitelist selectById(@Param("id") Long id);

    /**
     * 根据员工号查询
     * @param empNo 员工号
     * @return 实体
     */
    YTSafetyCarWhitelist selectByEmpNo(@Param("empNo") String empNo);

    /**
     * 根据车辆类型查询
     * @param carType 车辆类型
     * @return 实体
     */
    YTSafetyCarWhitelist selectByCarType(@Param("carType") String carType);
}
