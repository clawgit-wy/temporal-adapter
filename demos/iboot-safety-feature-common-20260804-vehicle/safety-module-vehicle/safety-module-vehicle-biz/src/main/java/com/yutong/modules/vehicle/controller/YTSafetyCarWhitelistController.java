package com.yutong.modules.vehicle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.entity.SafetySysUser;
import com.yutong.modules.vehicle.dto.CarWhitelistAddParam;
import com.yutong.modules.vehicle.dto.CarWhitelistListParam;

import com.yutong.modules.vehicle.entity.YTSafetyCarWhitelist;
import com.yutong.modules.vehicle.service.IYTSafetyCarWhitelistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * @Description: 车辆白名单管理
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Api(tags = "车辆白名单管理")
@RestController
@RequestMapping("/web/safety/car/whitelist")
public class YTSafetyCarWhitelistController {

    @Autowired
    private IYTSafetyCarWhitelistService carWhitelistService;

    @Autowired
    private ISafetySysUserService safetySysUserService;

    /**
     * 分页查询
     * @param param 查询参数
     * @return 分页结果
     */
    @AutoLog(value = "车辆白名单-分页数据列表查询", operateType = 1)
    @ApiOperation(value = "车辆白名单-分页数据列表查询接口")
    @PostMapping("/listPage")
    public Result<IPage<YTSafetyCarWhitelist>> listPage(@RequestBody CarWhitelistListParam param) {
        Page<YTSafetyCarWhitelist> page = new Page<>(param.getPageNo(), param.getPageSize());
        IPage<YTSafetyCarWhitelist> pageList = carWhitelistService.pageList(page, param);
        return Result.OK(pageList);
    }

    /**
     * 查询列表（不分页）
     * @param param 查询参数
     * @return 列表
     */
    @AutoLog(value = "车辆白名单-列表查询", operateType = 1)
    @ApiOperation(value = "车辆白名单-列表查询接口")
    @PostMapping("/list")
    public Result<List<YTSafetyCarWhitelist>> list(@RequestBody CarWhitelistListParam param) {
        List<YTSafetyCarWhitelist> list = carWhitelistService.selectList(param);
        return Result.OK(list);
    }

    /**
     * 根据ID查询
     * @param id 主键
     * @return 实体
     */
    @AutoLog(value = "车辆白名单-根据ID查询", operateType = 1)
    @ApiOperation(value = "车辆白名单-根据ID查询接口")
    @GetMapping("/queryById")
    public Result<YTSafetyCarWhitelist> queryById(@RequestParam("id") Long id) {
        YTSafetyCarWhitelist entity = carWhitelistService.getById(id);
        return Result.OK(entity);
    }

    /**
     * 根据员工号查询（用于自动带出姓名和部门）
     * @param empNo 员工号
     * @return 实体
     */
    @AutoLog(value = "车辆白名单-根据员工号查询", operateType = 1)
    @ApiOperation(value = "车辆白名单-根据员工号查询接口")
    @GetMapping("/queryByEmpNo")
    public Result<YTSafetyCarWhitelist> queryByEmpNo(@RequestParam("empNo") String empNo) {
        if (StringUtils.isBlank(empNo)) {
            return Result.OK(null);
        }
        // 从系统用户表查询员工信息
        SafetySysUser user = safetySysUserService.lambdaQuery()
                .eq(SafetySysUser::getEmpNo, empNo)
                .eq(SafetySysUser::getDeleteFalg, 0)
                .one();
        if (user != null) {
            YTSafetyCarWhitelist whitelist = new YTSafetyCarWhitelist();
            whitelist.setEmpNo(user.getEmpNo());
            whitelist.setEmpName(user.getEmpName());
            return Result.OK(whitelist);
        }
        return Result.OK(null);
    }

    /**
     * 新增
     * @param param 新增参数
     * @return 新增记录的主键ID
     */
    @AutoLog(value = "车辆白名单-新增", operateType = 2)
    @ApiOperation(value = "车辆白名单-新增接口")
    @PostMapping("/add")
    public Result<Long> add(@RequestBody CarWhitelistAddParam param) {
        // 设置创建人
        LoginUser loginUser = BusUtits.getLoginUser();
        if (loginUser != null) {
            param.setCreateBy(loginUser.getRealname());
            param.setCreateUser(loginUser.getEmpNo());
        }
        Long id = carWhitelistService.save(param);
        return Result.OK(id);
    }

    /**
     * 修改
     * @param param 修改参数
     * @return 修改结果
     */
    @AutoLog(value = "车辆白名单-修改", operateType = 3)
    @ApiOperation(value = "车辆白名单-修改接口")
    @PostMapping("/update")
    public Result<Integer> update(@RequestBody CarWhitelistAddParam param) {
        // 设置更新人
        LoginUser loginUser = BusUtits.getLoginUser();
        if (loginUser != null) {
            param.setUpdateBy(loginUser.getRealname());
            param.setUpdateUser(loginUser.getEmpNo());
        }
        int result = carWhitelistService.update(param);
        return Result.OK(result);
    }

    /**
     * 删除（逻辑删除）
     * @param id 主键
     * @return 删除结果
     */
    @AutoLog(value = "车辆白名单-删除", operateType = 2)
    @ApiOperation(value = "车辆白名单-删除接口")
    @PostMapping("/delete")
    public Result<Integer> delete(@RequestParam("id") Long id) {
        int result = carWhitelistService.deleteById(id);
        return Result.OK(result);
    }

    /**
     * 启用/停用
     *
     * @param ids    主键列表
     * @param status 状态：0-停用 1-启用
     * @return 修改结果
     */
    @AutoLog(value = "车辆白名单-启用/停用", operateType = 3)
    @ApiOperation(value = "车辆白名单-启用/停用接口")
    @PostMapping("/updateStatus")
    public Result<Integer> updateStatus(@RequestParam(name = "ids", required = true) List<Long> ids,
                                        @RequestParam(name = "status", required = true) Integer status) {
        int result = carWhitelistService.updateStatus(ids, status);
        return Result.OK(result);
    }
}
