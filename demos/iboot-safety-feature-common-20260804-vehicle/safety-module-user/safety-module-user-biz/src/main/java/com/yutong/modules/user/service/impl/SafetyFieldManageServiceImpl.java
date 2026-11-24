package com.yutong.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.dto.FieldManageListParam;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.mapper.SafetyFieldManageMapper;
import com.yutong.modules.user.vo.FieldLinkageVO;
import com.yutong.modules.user.vo.FieldManagerTreeVO;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description: 厂区表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Service
public class SafetyFieldManageServiceImpl extends ServiceImpl<SafetyFieldManageMapper, SafetyFieldManage> implements ISafetyFieldManageService {

    @Autowired
    private SafetyFieldManageMapper safetyFieldManageMapper;

    @Override
    public List<SafetyFieldManageVO> getList() {
        return safetyFieldManageMapper.getList();
    }

    @Override
    public List<SafetyDictItemVO> selectList(String supperCode) {
        return safetyFieldManageMapper.selectListByCode(supperCode);
    }

    @Override
    public List<SafetyDictItemVO> selectListItem(String itemCode, Integer id,String fieldType) {
        return safetyFieldManageMapper.selectListItem(itemCode, id,fieldType);
    }

    @Override
    public List<FieldManagerTreeVO> getSubTree(FieldManageListParam param) {
        // 如果parentId为空且fieldType不为空，先根据fieldCode查找根节点
        if (param.getParentId() == null && StringUtils.isNotEmpty(param.getFieldType())) {
            LambdaQueryWrapper<SafetyFieldManage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SafetyFieldManage::getDeleteFalg, 0)
                    .eq(SafetyFieldManage::getFieldCode, param.getFieldType());
            List<SafetyFieldManage> rootList = baseMapper.selectList(wrapper);
            if (rootList != null && !rootList.isEmpty()) {
                param.setParentId(rootList.get(0).getId());
            }
        }

        // 获取第一级子节点
        List<SafetyFieldManage> childList = getChildList(param);
        if (childList == null || childList.isEmpty()) {
            return new ArrayList<>();
        }

        // 计算最大排序值
        Optional<SafetyFieldManage> maxSortOpt = childList.stream()
                .max(Comparator.comparing(SafetyFieldManage::getFieldSort));
        Integer maxSort = maxSortOpt.map(SafetyFieldManage::getFieldSort).orElse(0);

        List<FieldManagerTreeVO> voList = new ArrayList<>();
        for (SafetyFieldManage fieldManage : childList) {
            FieldManagerTreeVO vo = new FieldManagerTreeVO();
            BeanUtils.copyProperties(fieldManage, vo);
            vo.setMaxSort(maxSort);

            // 递归构建子树
            if (!param.isTopFlag()) {
                FieldManageListParam subParam = new FieldManageListParam();
                subParam.setFieldType(param.getFieldType());
                subParam.setParentId(fieldManage.getId());
                List<FieldManagerTreeVO> subList = getChileList(subParam);
                vo.setRegionList(subList);
            }

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 递归获取子节点列表
     */
    private List<FieldManagerTreeVO> getChileList(FieldManageListParam param) {
        List<SafetyFieldManage> doList = getChildList(param);
        if (doList == null || doList.isEmpty()) {
            return new ArrayList<>();
        }

        Optional<SafetyFieldManage> maxSortOpt = doList.stream()
                .max(Comparator.comparing(SafetyFieldManage::getFieldSort));
        Integer maxSort = maxSortOpt.map(SafetyFieldManage::getFieldSort).orElse(0);

        List<FieldManagerTreeVO> voList = new ArrayList<>();
        for (SafetyFieldManage fieldManage : doList) {
            FieldManagerTreeVO vo = new FieldManagerTreeVO();
            BeanUtils.copyProperties(fieldManage, vo);
            vo.setMaxSort(maxSort);

            FieldManageListParam subParam = new FieldManageListParam();
            subParam.setFieldType(param.getFieldType());
            subParam.setParentId(fieldManage.getId());
            List<FieldManagerTreeVO> subList = getChileList(subParam);
            vo.setRegionList(subList);

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 根据条件查询子节点列表
     */
    private List<SafetyFieldManage> getChildList(FieldManageListParam param) {
        LambdaQueryWrapper<SafetyFieldManage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SafetyFieldManage::getDeleteFalg, 0);

        if (StringUtils.isNotEmpty(param.getFieldType())) {
            wrapper.eq(SafetyFieldManage::getFieldType, param.getFieldType());
        }
        if (param.getParentId() != null) {
            wrapper.eq(SafetyFieldManage::getParentId, param.getParentId());
        } else {
            wrapper.isNull(SafetyFieldManage::getParentId);
        }

        wrapper.orderByAsc(SafetyFieldManage::getFieldType)
                .orderByAsc(SafetyFieldManage::getFieldSort);

        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<FieldLinkageVO> selectLinkageList() {
        List<FieldLinkageVO> list = safetyFieldManageMapper.selectLinkageList();
        return buildFactoryRegionTree(list);
    }

    @Override
    public List<FieldLinkageVO> selectLinkageListApp() {
        return safetyFieldManageMapper.selectLinkageListApp();
    }


    /**
     * 将厂区-区域平铺列表构建为树形结构
     */
    private List<FieldLinkageVO> buildFactoryRegionTree(List<FieldLinkageVO> list) {
        List<FieldLinkageVO> factoryList = new ArrayList<>();
        List<FieldLinkageVO> regionList = new ArrayList<>();

        if (list != null && !list.isEmpty()) {
            for (FieldLinkageVO vo : list) {
                if (vo.getParentId() != null) {
                    regionList.add(vo);
                } else {
                    factoryList.add(vo);
                }
            }
        }

        for (FieldLinkageVO factory : factoryList) {
            factory.setRegionList(getChildren(factory.getId(), regionList));
        }

        return factoryList;
    }

    /**
     * 递归查找子节点
     */
    private List<FieldLinkageVO> getChildren(Long parentId, List<FieldLinkageVO> regionList) {
        List<FieldLinkageVO> children = new ArrayList<>();
        for (FieldLinkageVO region : regionList) {
            if (Objects.equals(region.getParentId(), parentId)) {
                children.add(region);
            }
        }
        return children;
    }
}
