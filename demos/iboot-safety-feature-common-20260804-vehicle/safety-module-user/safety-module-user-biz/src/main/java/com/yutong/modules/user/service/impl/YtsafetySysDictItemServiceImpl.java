package com.yutong.modules.user.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.api.IYtsafetySysDictItemService;
import com.yutong.modules.user.api.fallback.IYtsafetyFieldManageFileService;
import com.yutong.modules.user.constant.CustomConstant;
import com.yutong.modules.user.convert.DictConvert;
import com.yutong.modules.user.dto.SafetyDictItemDTO;
import com.yutong.modules.user.entity.SafetyDictItem;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.entity.YtsafetyFieldManageFile;
import com.yutong.modules.user.mapper.SafetyFieldManageMapper;
import com.yutong.modules.user.mapper.YtsafetySysDictItemMapper;
import com.yutong.modules.user.query.SafetyDictItemQuery;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Description: 字典子表
 * @Author: jeecg-boot
 * @Date: 2026-05-27
 * @Version: V1.0
 */
@Log4j2
@Service
public class YtsafetySysDictItemServiceImpl extends ServiceImpl<YtsafetySysDictItemMapper, SafetyDictItem> implements IYtsafetySysDictItemService {

    @Autowired
    private YtsafetySysDictItemMapper sysDictItemMapper;
    @Autowired
    private ISafetyFieldManageService fieldManageService;
    @Autowired
    private IYtsafetyFieldManageFileService fieldManageFileService;

    @Resource
    private SafetyFieldManageMapper safetyFieldManageMapper;


    @Override
    public void add(SafetyDictItemDTO sysDictItemDto) {
        if ("dict".equals(sysDictItemDto.getType())) {
            if (StrUtil.isBlank(sysDictItemDto.getSupperCode())){
                throw new RuntimeException("字典编码不能为空");
            }
            if (StrUtil.isBlank(sysDictItemDto.getItemCode())){
                throw new RuntimeException("字典子项目编码不能为空");
            }
            if (ObjectUtil.isEmpty(sysDictItemDto.getSupperId())){
                throw new RuntimeException("字典id不能为空");
            }

            SafetyDictItem selectDictItem = this.lambdaQuery()
                    .eq(SafetyDictItem::getItemCode, sysDictItemDto.getItemCode())
                    .eq(sysDictItemDto.getPid() == null ,SafetyDictItem::getSupperId, sysDictItemDto.getSupperId())
                    .eq(sysDictItemDto.getPid() != null ,SafetyDictItem::getPid, sysDictItemDto.getPid())
                    .ne(SafetyDictItem::getIsUse, "2")
                    .last("limit 1")
                    .one();
            if (BeanUtil.isNotEmpty(selectDictItem)) {
                throw new RuntimeException("当前子项已存在");
            }
            SafetyDictItem safetyDictItem = BeanUtil.toBean(sysDictItemDto, SafetyDictItem.class);
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (BeanUtil.isNotEmpty(user)) {
                safetyDictItem.setUpdateUser(user.getRealname());
            }
            safetyDictItem.setUpdateTime(new Date());
            safetyDictItem.setIsUse("1");
            sysDictItemMapper.insert(safetyDictItem);
        } else if ("field".equals(sysDictItemDto.getType())) {
            LoginUser user =  BusUtits.getLoginUser();
            if("risk_region".equals(sysDictItemDto.getSupperCode())){
                if (ObjectUtil.isEmpty(sysDictItemDto.getItemCode())){
                    String itemCode = generateUniqueItemCode(sysDictItemMapper);
                    sysDictItemDto.setItemCode(itemCode);
                }
            }
            SafetyFieldManage field = DictConvert.getField(sysDictItemDto);
            SafetyFieldManage fieldManageParnet = fieldManageService.lambdaQuery()
                    .eq(SafetyFieldManage::getId, field.getParentId())
                    .eq(SafetyFieldManage::getDeleteFalg, CommonConstant.DEL_FLAG_0)
                    .one();
            if(BeanUtil.isNotEmpty(fieldManageParnet)){
                /**不修改之前逻辑，如果类型是隐患区域，字段类型是前端传入的类型*/
                if(StringUtils.equals(sysDictItemDto.getSupperCode(),CustomConstant.RISK_REGION_FIELD_TYPE)){
                    field.setFieldType(sysDictItemDto.getSupperCode());
                }else {
                    field.setFieldType(fieldManageParnet.getFieldCode());
                }
            }
            List<SafetyFieldManage> list = fieldManageService.lambdaQuery()
                    .eq(SafetyFieldManage::getParentId, field.getParentId())
                    .eq(SafetyFieldManage::getDeleteFalg, CommonConstant.DEL_FLAG_0)
                    .eq(SafetyFieldManage::getFieldName, field.getFieldName())
                    .list();
            if (CollUtil.isNotEmpty(list)) {
                throw new RuntimeException("当前字段已存在");
            }
            Date date = new Date();
            field.setCreateTime(date);
            field.setUpdateTime(date);
            fieldManageService.save(field);
            if (field.getId() != null && StrUtil.isNotBlank(sysDictItemDto.getFileUrl()) && StrUtil.isNotBlank(sysDictItemDto.getFileId()) ) {
                YtsafetyFieldManageFile fieldManageFile = new YtsafetyFieldManageFile();
                fieldManageFile.setFileUrl(sysDictItemDto.getFileUrl());
                fieldManageFile.setFileId(sysDictItemDto.getFileId());
                fieldManageFile.setId(field.getId());
                fieldManageFile.setUpdateUser(user.getRealname());
                fieldManageFile.setUpdateTime(date);
                fieldManageFileService.save(fieldManageFile);
            }


        }

    }

    @Override
    public void updateDictItem(SafetyDictItemDTO sysDictItemDto) {
        if (StrUtil.isBlank(sysDictItemDto.getType())) {
            throw new RuntimeException("类型不能为空");
        }
        LoginUser user =  BusUtits.getLoginUser();
        if ("dict".equals(sysDictItemDto.getType())) {
            SafetyDictItem selectDictItem = this.lambdaQuery()
                    .eq(SafetyDictItem::getItemCode, sysDictItemDto.getItemCode())
                    .eq(sysDictItemDto.getPid() == null ,SafetyDictItem::getSupperId, sysDictItemDto.getSupperId())
                    .eq(sysDictItemDto.getPid() != null ,SafetyDictItem::getPid, sysDictItemDto.getPid())
                    .ne(SafetyDictItem::getId, sysDictItemDto.getId())
                    .ne(SafetyDictItem::getIsUse, "2")
                    .last("limit 1")
                    .one();
            if (BeanUtil.isNotEmpty(selectDictItem)) {
                throw new RuntimeException("当前子项已存在");
            }
            SafetyDictItem bean = BeanUtil.toBean(sysDictItemDto, SafetyDictItem.class);
            if (BeanUtil.isNotEmpty(user)) {
                bean.setUpdateUser(user.getRealname());
                bean.setUpdateTime(new Date());
            }
            sysDictItemMapper.updateById(bean);

        } else if ("field".equals(sysDictItemDto.getType())) {
            if("risk_region".equals(sysDictItemDto.getSupperCode())){
                if (ObjectUtil.isEmpty(sysDictItemDto.getItemCode())){
                    String itemCode = generateUniqueItemCode(sysDictItemMapper);
                    sysDictItemDto.setItemCode(itemCode);
                }else{
                    SafetyDictItem exist = this.lambdaQuery()
                            .eq(SafetyDictItem::getItemCode, sysDictItemDto.getItemCode())
                            .ne(sysDictItemDto.getId() != null, SafetyDictItem::getId, sysDictItemDto.getId())
                            .ne(SafetyDictItem::getIsUse, "2")
                            .last("limit 1")
                            .one();
                    if (BeanUtil.isNotEmpty(exist)) {
                        throw new JeecgBootException("当前子项编码已存在");
                    }
                }
            }
            SafetyFieldManage field = DictConvert.getField(sysDictItemDto);
            List<SafetyFieldManage> list = fieldManageService.lambdaQuery()
                    .eq(SafetyFieldManage::getFieldType, field.getFieldType())
                    .eq(SafetyFieldManage::getDeleteFalg, CommonConstant.DEL_FLAG_0)
                    .eq(SafetyFieldManage::getFieldCode, field.getFieldCode())
                    .ne(SafetyFieldManage::getId, field.getId())
                    .list();
            if (CollUtil.isNotEmpty(list)) {
                throw new RuntimeException("当前字段已存在");
            }
            fieldManageService.lambdaUpdate()
                    .eq(SafetyFieldManage::getId, field.getId())
                    .set(SafetyFieldManage::getFieldName, field.getFieldName())
                    .set(SafetyFieldManage::getFieldCode, field.getFieldCode())
                    .set(SafetyFieldManage::getUpdateTime, new Date())
                    .set(SafetyFieldManage::getFieldSort, field.getFieldSort())
                    .update();
            if (StrUtil.isNotBlank(sysDictItemDto.getFileUrl()) || StrUtil.isNotBlank(sysDictItemDto.getFileId()) ) {
                YtsafetyFieldManageFile fieldManageFile = new YtsafetyFieldManageFile();
                if(ObjectUtil.isNotNull(sysDictItemDto.getFileUrl())){
                    fieldManageFile.setFileUrl(sysDictItemDto.getFileUrl());
                }
                if(ObjectUtil.isNotNull(sysDictItemDto.getFileId())){
                    fieldManageFile.setFileId(sysDictItemDto.getFileId());
                }
                fieldManageFile.setId(field.getId());
                fieldManageFile.setUpdateUser(user.getRealname());
                fieldManageFile.setUpdateTime(new Date());
                fieldManageFileService.saveOrUpdate(fieldManageFile);
            } else {
                fieldManageFileService.removeById(field.getId());
            }
        }
    }

    @Override
    public void deleteItem(String idStr, String type) {
        if("dict".equals(type)){
            Long id = Long.valueOf(idStr);
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

            // 1. 获取当前ID + 所有下级子孙ID
            List<Long> allIds = new ArrayList<>();
            allIds.add(id);
            // 递归查询全部子节点ID
            findAllChildrenIdsByDict(Collections.singletonList(id), allIds);

            // 2. 公共更新条件抽取
            LambdaUpdateWrapper<SafetyDictItem> wrapper = Wrappers.lambdaUpdate();
            wrapper.in(SafetyDictItem::getId, allIds)
                    .set(SafetyDictItem::getIsUse, "2")
                    .set(SafetyDictItem::getUpdateTime, new Date());
            if (loginUser.getRealname() != null) {
                wrapper.set(SafetyDictItem::getUpdateUser, loginUser.getRealname());
            }
            this.update(wrapper);
        } else if ("field".equals(type)) {

            SafetyFieldManage fieldManage = fieldManageService.getById(idStr);
            if (BeanUtil.isNotEmpty(fieldManage)) {

                Set<Long> allIds = new HashSet<>();
                Set<Long> collect = new HashSet<>();
                Set<String> collectType = new HashSet<>();
                allIds.add(Long.valueOf(idStr));
                collect.add(Long.valueOf(idStr));
                collectType.add(fieldManage.getFieldCode());
                findAllChildrenIdsByField(collect, collectType, allIds);
                fieldManageService.lambdaUpdate()
                        .in(SafetyFieldManage::getId, allIds)
                        .set(SafetyFieldManage::getDeleteFalg, CommonConstant.DEL_FLAG_1)
                        .set(SafetyFieldManage::getUpdateTime, new Date())
                        .update();
            }


        }
    }

    private void findAllChildrenIdsByField(Set<Long> parentIdList, Set<String> collectType, Set<Long> resultIds) {
        if (CollUtil.isEmpty(parentIdList) && CollUtil.isEmpty(collectType)) {
            return;
        }
        List<SafetyFieldManage> children = fieldManageService.lambdaQuery()
                .eq(SafetyFieldManage::getDeleteFalg, CommonConstant.DEL_FLAG_0)
                .and(wr ->wr.in(SafetyFieldManage::getFieldType, collectType)
                        .or().in(SafetyFieldManage::getFieldType, collectType))
                .list();

        if (CollUtil.isNotEmpty(children)) {
            Set<Long> childIds = children.stream()
                    .map(SafetyFieldManage::getId)
                    .collect(Collectors.toSet());
            Set<String> types = children.stream()
                    .map(SafetyFieldManage::getFieldCode)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());
            resultIds.addAll(childIds);

            // 继续递归查找下一级
            findAllChildrenIdsByField(childIds, types, resultIds);
        }

    }

    /**
     * 递归查询所有子孙节点ID，避免N层只查一层
     */
    private void findAllChildrenIdsByDict(List<Long> parentIdList, List<Long> resultIds) {
        List<SafetyDictItem> children = this.lambdaQuery()
                .in(SafetyDictItem::getPid, parentIdList)
                .ne(SafetyDictItem::getIsUse, "2")
                .list();
        if (CollUtil.isEmpty(children)) {
            return;
        }
        List<Long> childIds = children.stream()
                .map(SafetyDictItem::getId)
                .collect(Collectors.toList());
        resultIds.addAll(childIds);
        // 继续递归查找下一级
        findAllChildrenIdsByDict(childIds, resultIds);
    }

    /**
     * 生成唯一的itemCode，前缀为region_，总长度不超过19位
     */
    private String generateUniqueItemCode(YtsafetySysDictItemMapper mapper) {
        String prefix = "region_";
        for (int i = 0; i < 10; i++) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String itemCode = prefix + uuid.substring(0, 12).toUpperCase();
            SafetyDictItem exist = mapper.selectOne(
                    Wrappers.<SafetyDictItem>lambdaQuery().eq(SafetyDictItem::getItemCode, itemCode)
            );
            if (exist == null) {
                return itemCode;
            }
        }
        throw new JeecgBootException("生成唯一区域编码失败，请重试");
    }

    @Override
    public SafetyDictItemVO selectById(Long id) {
        SafetyDictItem safetyDictItem = sysDictItemMapper.selectById(id);

        return BeanUtil.toBean(safetyDictItem, SafetyDictItemVO.class);
    }

    @Override
    public List<SafetyDictItemVO> selectByName(SafetyDictItemQuery query) {
        List<SafetyDictItemVO> safetyDictItemVOList = sysDictItemMapper.selectByName(query);
        //父级
        if (CollUtil.isNotEmpty(safetyDictItemVOList)) {
            for (int i = 0; i < safetyDictItemVOList.size(); i++) {
                SafetyDictItemVO safetyDictItemVO = safetyDictItemVOList.get(i);
                Long pid = safetyDictItemVO.getPid();
                if (pid == null) {
                    continue;
                }
                SafetyDictItemVO parent = getParent(safetyDictItemVO);
                if (BeanUtil.isNotEmpty(parent)) {
                    safetyDictItemVOList.set(i, parent);
                }
            }
        }

        //子集
        if (CollUtil.isNotEmpty(safetyDictItemVOList)) {
            for (SafetyDictItemVO safetyDictItem : safetyDictItemVOList) {
                List<SafetyDictItemVO> safetyDictItemVOs = getSafetyDictItemVOs(safetyDictItem.getId());
                if (CollUtil.isNotEmpty(safetyDictItemVOs)) {
                    safetyDictItem.setChildren(safetyDictItemVOs);
                } else {
                    safetyDictItem.setChildren(null);
                }
            }
        }
        List<SafetyDictItemVO> distinctList = new ArrayList<>(
                safetyDictItemVOList.stream()
                        .collect(Collectors.toMap(
                                SafetyDictItemVO::getId,   // 唯一key
                                Function.identity(),
                                (oldItem, newItem) -> oldItem // 重复取旧值；newItem则取新值
                        ))
                        .values()
        );
        return distinctList;
    }

    /**
     * 查询父级数据
     *
     * @param safetyDictItemVO
     * @return
     */
    private SafetyDictItemVO getParent(SafetyDictItemVO safetyDictItemVO) {

        SafetyDictItem Parent = this.lambdaQuery()
                .eq(SafetyDictItem::getId, safetyDictItemVO.getPid())
                .eq(SafetyDictItem::getIsUse, "1")
                .one();
        if (Parent != null) {
            SafetyDictItemVO parentVo = BeanUtil.toBean(Parent, SafetyDictItemVO.class);
            List<SafetyDictItemVO> list = new ArrayList<>();
            list.add(safetyDictItemVO);
            parentVo.setChildren(list);
            if (parentVo.getPid() != null) {
                SafetyDictItemVO parent = getParent(parentVo);
                return parent;
            }
            return parentVo;
        }
        return null;
    }


    @Override
    public List<SafetyDictItemVO> selectList(SafetyDictItemQuery query) {
        List<SafetyDictItemVO> dictItemVOList = new ArrayList<>();
        if(StrUtil.isNotBlank(query.getItemName())){
            return selectByName(query);
        }else if ("dict".equals(query.getType())) {
            return selectDictList(query.getSupperCode());
        } else if ("field".equals(query.getType())) {
            return selectFieldList(query.getSupperCode());
        }
        return dictItemVOList;
    }

    @Override
    public List<SafetyDictItemVO> selectFlat(SafetyDictItemQuery query) {
        List<SafetyDictItemVO> dictItemVOList = new ArrayList<>();
        if ("dict".equals(query.getType())) {
            return this.lambdaQuery()
                    .eq(SafetyDictItem::getPid, query.getPid())
                    .eq(SafetyDictItem::getIsUse, "1")
                    .eq(ObjectUtil.isNotNull(query.getId()),SafetyDictItem::getId, query.getId())
                    .eq(StrUtil.isNotBlank(query.getSupperCode()),SafetyDictItem::getSupperCode, query.getSupperCode())
                    .like(StrUtil.isNotBlank(query.getItemName()), SafetyDictItem::getItemName, query.getItemName())
                    .list().stream()
                    .map(item -> BeanUtil.toBean(item, SafetyDictItemVO.class))
                    .sorted(Comparator.comparing(SafetyDictItemVO::getItemOrder))
                    .collect(Collectors.toList());
        } else if ("field".equals(query.getType())) {
            SafetyFieldManage safetyFieldManage = safetyFieldManageMapper.selectById(query.getPid());
            return safetyFieldManageMapper.selectListItem(safetyFieldManage.getFieldCode(), query.getPid(), null);
        }
        return dictItemVOList;
    }

    /**
     * 查询所有字典子项
     * @param supperCode
     * @return
     */
    public List<SafetyDictItemVO> selectDictList(String supperCode) {
        // 1.一次性查出所有数据
        List<SafetyDictItem> allItemList = this.lambdaQuery()
                .eq(SafetyDictItem::getSupperCode, supperCode)
                .eq(SafetyDictItem::getIsUse, "1")
//                .orderByAsc(SafetyDictItem::getItemOrder)
                .list();

        // 处理pid=null分组报错，null替换为0L
        Map<Long, List<SafetyDictItem>> pidGroupMap = allItemList.stream()
                .collect(Collectors.groupingBy(item -> item.getPid() == null ? 0L : item.getPid()));

        // 全部转为VO
        List<SafetyDictItemVO> voList = allItemList.stream()
                .map(item -> BeanUtil.toBean(item, SafetyDictItemVO.class))
                .sorted(Comparator.comparing(SafetyDictItemVO::getItemOrder))
                .collect(Collectors.toList());

        // 递归构建子节点
        buildChildren(voList, pidGroupMap);

        // 只返回根节点（pid为null）
        return voList.stream()
                .filter(vo -> vo.getPid() == null)
                .collect(Collectors.toList());
    }

    /**
     * 递归无限层级设置children
     */
    private void buildChildren(List<SafetyDictItemVO> voList, Map<Long, List<SafetyDictItem>> pidGroupMap) {
        for (SafetyDictItemVO vo : voList) {
            vo.setType("dict");
            // 根据当前ID找到所有子实体
            List<SafetyDictItem> childEntities = pidGroupMap.get(vo.getId());
            if (CollUtil.isEmpty(childEntities)) {
                continue;
            }
            // 子实体转VO
            List<SafetyDictItemVO> childVoList = childEntities.stream()
                    .map(item -> BeanUtil.toBean(item, SafetyDictItemVO.class))
                    .sorted(Comparator.comparing(SafetyDictItemVO::getItemOrder))
                    .collect(Collectors.toList());

            vo.setChildren(childVoList);
            // 递归继续给子集绑定下级，实现无限层级
            buildChildren(childVoList, pidGroupMap);
        }
    }

    /**
     * 递归查询所有的子集数据，并封装树形结构
     * @param id
     * @return
     */
    private List<SafetyDictItemVO> getSafetyDictItemVOs(Long id) {
        List<SafetyDictItem> list = this.lambdaQuery()
                .eq(SafetyDictItem::getPid, id)
                .eq(SafetyDictItem::getIsUse, "1")
                .list();
        List<SafetyDictItemVO> collect = list.stream().map(safetyDictItem -> {
            SafetyDictItemVO bean = BeanUtil.toBean(safetyDictItem, SafetyDictItemVO.class);
            return bean;
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(list)) {
            for (SafetyDictItemVO safetyDictItem : collect) {
                List<SafetyDictItemVO> safetyDictItemVOs = getSafetyDictItemVOs(safetyDictItem.getId());
                if (CollUtil.isNotEmpty(safetyDictItemVOs)) {
                    safetyDictItem.setChildren(safetyDictItemVOs);
                } else {
                    safetyDictItem.setChildren(null);
                }
            }
        }
        return collect;
    }

    // 去重工具方法
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    /**
     * 查询第一次字段数据
     * @param supperCode
     * @return
     */
    public List<SafetyDictItemVO> selectFieldList(String supperCode) {
        List<SafetyDictItemVO> list  = new ArrayList<>();
        /**特殊逻辑：如果是隐患区域，查询厂区级别*/
        if(StringUtils.equals(supperCode, CustomConstant.RISK_REGION_FIELD_TYPE)){
            list = fieldManageService.selectList(CustomConstant.FACTORY_FIELD_TYPE);
        }else{
            list = fieldManageService.selectList(supperCode);
        }
        // 根据主键id去重
        List<SafetyDictItemVO> collect = list.stream()
                .filter(distinctByKey(SafetyDictItemVO::getId))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(collect)) {
            // 传入已访问集合，防止循环递归
            Set<Long> visited = new HashSet<>();
            for (SafetyDictItemVO vo : collect) {
                getFieldItem(vo, visited,supperCode);
            }
        }
        // 重点：返回处理后的集合，不是原始list
        return collect;
    }

    /**
     * 递归查询所有字段子集数据，增加防循环集合
     * @param vo
     * @param visited 已访问ID，防止循环引用死循环
     */
    private void getFieldItem(SafetyDictItemVO vo, Set<Long> visited,String supperCode) {
        Long itemId = vo.getId();
        // 已经访问过直接终止，阻断循环
        if (visited.contains(itemId)) {
            vo.setChildren(null);
            return;
        }
        visited.add(itemId);
        vo.setType(CustomConstant.FIELD);
        String fieldType = null;
        /**特殊逻辑：如果是类型是factory，只查询下级region*/
        if(StringUtils.equals(CustomConstant.FACTORY_FIELD_TYPE,supperCode)){
            fieldType = CustomConstant.REGION_FIELD_TYPE;
        }else if(StringUtils.equals(CustomConstant.RISK_REGION_FIELD_TYPE,supperCode)){
            fieldType = CustomConstant.RISK_REGION_FIELD_TYPE;
        }
        List<SafetyDictItemVO> subList = fieldManageService.selectListItem(vo.getItemCode(), Objects.isNull(vo.getId())? null: vo.getId().intValue(),fieldType);
        // 按ID去重子节点
        List<SafetyDictItemVO> children = subList.stream()
                .filter(distinctByKey(SafetyDictItemVO::getId))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(children)) {
            vo.setChildren(children);
            for (SafetyDictItemVO child : children) {
                getFieldItem(child, visited,fieldType);
            }
        } else {
            vo.setChildren(null);
        }
    }


}
