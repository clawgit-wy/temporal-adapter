package com.yutong.modules.vehicle.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.vehicle.dto.CarWhitelistAddParam;
import com.yutong.modules.vehicle.dto.CarWhitelistListParam;
import com.yutong.modules.vehicle.entity.YTSafetyCarWhitelist;
import com.yutong.modules.vehicle.mapper.YTSafetyCarWhitelistMapper;
import com.yutong.modules.vehicle.service.IYTSafetyCarWhitelistService;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: 车辆白名单表 Service 实现类
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Service
public class YTSafetyCarWhitelistServiceImpl extends ServiceImpl<YTSafetyCarWhitelistMapper, YTSafetyCarWhitelist> implements IYTSafetyCarWhitelistService {

    @Override
    public IPage<YTSafetyCarWhitelist> pageList(Page<YTSafetyCarWhitelist> page, CarWhitelistListParam param) {
        return baseMapper.selectPageList(page, param);
    }

    @Override
    public List<YTSafetyCarWhitelist> selectList(CarWhitelistListParam param) {
        return baseMapper.selectList(param);
    }

    @Override
    public YTSafetyCarWhitelist getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public YTSafetyCarWhitelist getByEmpNo(String empNo) {
        return baseMapper.selectByEmpNo(empNo);
    }

    @Override
    public Long save(CarWhitelistAddParam param) {
        checkDuplicate(param);
        YTSafetyCarWhitelist entity = new YTSafetyCarWhitelist();
        BeanUtils.copyProperties(param, entity);

        entity.setDelFlag(0);
        entity.setCreateTime(new Date());
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public int update(CarWhitelistAddParam param) {
        checkDuplicate(param);
        YTSafetyCarWhitelist entity = new YTSafetyCarWhitelist();
        BeanUtils.copyProperties(param, entity);

        entity.setUpdateTime(new Date());
        return baseMapper.updateById(entity);
    }

    @Override
    public int deleteById(Long id) {
        YTSafetyCarWhitelist entity = new YTSafetyCarWhitelist();
        entity.setId(id);
        entity.setDelFlag(1);
        entity.setUpdateTime(new Date());
        return baseMapper.updateById(entity);
    }

    @Override
    public int deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Long id : ids) {
            count += deleteById(id);
        }
        return count;
    }

    @Override
    public int updateStatus(List<Long> ids, Integer status) {
        YTSafetyCarWhitelist entity = new YTSafetyCarWhitelist();
        entity.setStatus(status);
        entity.setUpdateTime(new Date());
        UpdateWrapper<YTSafetyCarWhitelist> wrapper = new UpdateWrapper<>();
        wrapper.in("id", ids);
        return baseMapper.update(entity, wrapper);
    }

    /**
     * 检查重复：人员白名单根据员工号，车辆类型白名单根据车辆类型
     * @param param 参数
     */
    private void checkDuplicate(CarWhitelistAddParam param) {
        YTSafetyCarWhitelist exist = null;
        if (param.getWhitelistType() != null && param.getWhitelistType() == 1) {
            // 人员白名单：根据员工号查重
            exist = baseMapper.selectByEmpNo(param.getEmpNo());
            if (exist != null && !exist.getId().equals(param.getId())) {
                throw new JeecgBootException("人员白名单中已存在");
            }
        } else if (param.getWhitelistType() != null && param.getWhitelistType() == 2) {
            // 车辆类型白名单：根据车辆类型查重
            exist = baseMapper.selectByCarType(param.getCarType());
            if (exist != null && !exist.getId().equals(param.getId())) {
                throw new JeecgBootException("车辆类型白名单中已存在");
            }
        }
    }
}
