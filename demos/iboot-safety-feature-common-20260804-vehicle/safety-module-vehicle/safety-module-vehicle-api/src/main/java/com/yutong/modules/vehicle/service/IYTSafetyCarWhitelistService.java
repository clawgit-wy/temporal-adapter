package com.yutong.modules.vehicle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.dto.CarWhitelistAddParam;
import com.yutong.modules.vehicle.dto.CarWhitelistListParam;
import com.yutong.modules.vehicle.entity.YTSafetyCarWhitelist;

import java.util.List;

/**
 * @Description: 车辆白名单表 Service 接口
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
public interface IYTSafetyCarWhitelistService extends IService<YTSafetyCarWhitelist> {

    /**
     * 分页查询
     * @param page 分页对象
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<YTSafetyCarWhitelist> pageList(Page<YTSafetyCarWhitelist> page, CarWhitelistListParam param);

    /**
     * 查询列表
     * @param param 查询参数
     * @return 列表
     */
    List<YTSafetyCarWhitelist> selectList(CarWhitelistListParam param);

    /**
     * 根据ID查询
     * @param id 主键
     * @return 实体
     */
    YTSafetyCarWhitelist getById(Long id);

    /**
     * 根据员工号查询
     * @param empNo 员工号
     * @return 实体
     */
    YTSafetyCarWhitelist getByEmpNo(String empNo);

    /**
     * 新增
     * @param param 新增参数
     * @return 新增记录的主键ID
     */
    Long save(CarWhitelistAddParam param);

    /**
     * 修改
     * @param param 修改参数
     * @return 修改结果
     */
    int update(CarWhitelistAddParam param);

    /**
     * 删除（逻辑删除）
     * @param id 主键
     * @return 删除结果
     */
    int deleteById(Long id);

    /**
     * 批量删除（逻辑删除）
     * @param ids 主键列表
     * @return 删除结果
     */
    int deleteBatch(List<Long> ids);

    /**
     * 启用/停用
     * @param ids 主键列表
     * @param status 状态：0-停用 1-启用
     * @return 修改结果
     */
    int updateStatus(List<Long> ids, Integer status);
}
