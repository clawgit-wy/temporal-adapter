package com.yutong.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.convert.DictConvert;
import com.yutong.modules.user.dto.SafetyDictSupperDTO;
import com.yutong.modules.user.entity.SafetyDictSupper;
import com.yutong.modules.user.entity.SafetyDictItem;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.mapper.YtsafetySysDictItemMapper;
import com.yutong.modules.user.mapper.YtsafetySysDictMapper;
import com.yutong.modules.user.query.SafetyDictSupperQuery;
import com.yutong.modules.user.api.IYtsafetySysDictService;
import com.yutong.modules.user.vo.SafetyDictSupperVO;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 字典表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Log4j2
@Service
public class YtsafetySysDictServiceImpl extends ServiceImpl<YtsafetySysDictMapper, SafetyDictSupper> implements IYtsafetySysDictService {


    @Autowired
    private YtsafetySysDictMapper sysDictMapper;
    @Autowired
    private YtsafetySysDictItemMapper sysDictItemMapper;
    @Autowired
    private ISafetyFieldManageService fieldManageService;


    @Override
    public IPage<SafetyDictSupperVO> pageList(Page<SafetyDictSupperVO> page, SafetyDictSupperQuery safetyDictSupperQuery) {

        return sysDictMapper.queryPageList(page, safetyDictSupperQuery);
    }

    @Override
    public void add(SafetyDictSupperDTO sysDict) {
            LambdaQueryWrapper<SafetyDictSupper> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SafetyDictSupper::getSupperCode, sysDict.getSupperCode());
            wrapper.eq(SafetyDictSupper::getDel, String.valueOf(CommonConstant.DEL_FLAG_0));
            List<SafetyDictSupper> safetyDictSuppers = sysDictMapper.selectList(wrapper);
            if (CollUtil.isNotEmpty(safetyDictSuppers)) {
                throw new RuntimeException("当前字典已存在");
            }
            SafetyDictSupper safetyDictSupper = BeanUtil.toBean(sysDict, SafetyDictSupper.class);
            safetyDictSupper.setDel(String.valueOf(CommonConstant.DEL_FLAG_0));
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (BeanUtil.isNotEmpty(user)) {
                safetyDictSupper.setUpdateUser(user.getRealname());
            }
            safetyDictSupper.setUpdateTime(new Date());
            sysDictMapper.insert(safetyDictSupper);


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SafetyDictSupperDTO dictSupperDTO) {
        //修改字典数据
        if ("dict".equals(dictSupperDTO.getType())) {
            LambdaQueryWrapper<SafetyDictSupper> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SafetyDictSupper::getSupperCode, dictSupperDTO.getSupperCode());
            queryWrapper.eq(SafetyDictSupper::getDel, String.valueOf(CommonConstant.DEL_FLAG_0));
            queryWrapper.ne(SafetyDictSupper::getId, dictSupperDTO.getId());
            List<SafetyDictSupper> safetyDictSuppers = sysDictMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(safetyDictSuppers)) {
                throw new RuntimeException("当前字典编码已存在");
            }
            SafetyDictSupper selected = sysDictMapper.selectById(dictSupperDTO.getId());
            //修改字典编码，同时修改子项编码
            if (!selected.getSupperCode().equals(dictSupperDTO.getSupperCode())){
                LambdaQueryWrapper<SafetyDictItem> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SafetyDictItem::getSupperId, dictSupperDTO.getId());
                List<SafetyDictItem> safetyDictItems = sysDictItemMapper.selectList(wrapper);
                List<Long> collect = safetyDictItems.stream().map(SafetyDictItem::getId).collect(Collectors.toList());
                LambdaUpdateWrapper<SafetyDictItem> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(SafetyDictItem::getSupperCode, dictSupperDTO.getSupperCode());
                updateWrapper.in(SafetyDictItem::getId, collect);
                sysDictItemMapper.update(null, updateWrapper);

            }
            SafetyDictSupper safetyDictSupper = new SafetyDictSupper();
            safetyDictSupper.setId(dictSupperDTO.getId())
                    .setSupperName(dictSupperDTO.getSupperName())
                    .setSupperCode(dictSupperDTO.getSupperCode())
                    .setSupperDesc(dictSupperDTO.getSupperDesc())
                    .setSupperOrder(dictSupperDTO.getSupperOrder());
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (BeanUtil.isNotEmpty(user)) {
                safetyDictSupper.setUpdateUser(user.getRealname());
            }
            safetyDictSupper.setUpdateTime(new Date());
            sysDictMapper.updateById(safetyDictSupper);
            //修改字段数据
        }else if("field".equals(dictSupperDTO.getType())) {
            SafetyFieldManage fieldManage = DictConvert.getField(dictSupperDTO);
            List<SafetyFieldManage> list = fieldManageService.lambdaQuery()
                    .ne(SafetyFieldManage::getId, fieldManage.getId())
                    .eq(SafetyFieldManage::getFieldName, fieldManage.getFieldName())
                    .list();
            if (CollUtil.isNotEmpty(list)){
                throw new RuntimeException("当前字段已存在");
            }
            fieldManageService.lambdaUpdate()
                    .eq(SafetyFieldManage::getId, fieldManage.getId())
                    .set(SafetyFieldManage::getFieldName, fieldManage.getFieldName())
                    .set(SafetyFieldManage::getUpdateTime, new Date())
                    .set(SafetyFieldManage::getFieldSort, fieldManage.getFieldSort())
                    .update();
        }



    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteDict(Long id) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        if("dict".equals(type)){
            LambdaUpdateWrapper<SafetyDictSupper> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(SafetyDictSupper::getId, id)
                    .set(SafetyDictSupper::getDel, "1");
            if (BeanUtil.isNotEmpty(user)) {
                wrapper.set(SafetyDictSupper::getUpdateUser ,user.getRealname());
            }
            sysDictMapper.update(null, wrapper);

            LambdaQueryWrapper<SafetyDictItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SafetyDictItem::getSupperId, id);
            List<SafetyDictItem> safetyDictItems = sysDictItemMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(safetyDictItems)) {
                List<Long> collect = safetyDictItems.stream().map(SafetyDictItem::getId).collect(Collectors.toList());
                LambdaUpdateWrapper<SafetyDictItem> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(SafetyDictItem::getIsUse, "2");
                updateWrapper.in(SafetyDictItem::getId, collect);
                sysDictItemMapper.update(null, updateWrapper);
            }
            //删除字段
//        } else if ("field".equals(type)) {
//            List<SafetyFieldManage> list = fieldManageService.lambdaQuery()
//                    .eq(SafetyFieldManage::getId, id)
//                    .eq(SafetyFieldManage::getDeleteFalg, CommonConstant.DEL_FLAG_0)
//                    .list();
//            if(CollUtil.isEmpty(list)){
//                throw new RuntimeException("删除的字段不存在");
//            }
//            fieldManageService.lambdaUpdate()
//                    .eq(SafetyFieldManage::getId, id)
//                    .set(SafetyFieldManage::getDeleteFalg, CommonConstant.DEL_FLAG_1)
//                    .set(SafetyFieldManage::getUpdateTime, new Date())
//                    .update();
//        }


    }

    @Override
    public SafetyDictSupperVO getTreeById(Long id) {
        return sysDictMapper.getById(id);
    }

    /**
     * 添加字典
     * @param dictName
     */
    private Long addDict(String dictName,String lowAppId) {
        SafetyDictSupper dict = new SafetyDictSupper();
        dict.setSupperName(dictName);
        dict.setSupperCode(RandomUtil.randomString(10));
        dict.setDel(String.valueOf(Integer.valueOf(CommonConstant.STATUS_0)));
//        dict.setLowAppId(lowAppId);
        baseMapper.insert(dict);
        return dict.getId();
    }

    /**
     * 添加字典子项
     * @param id
     * @param dictItemList
     */
    private void addDictItem(Long id, List<SafetyDictItem> dictItemList) {
        if(null!=dictItemList && dictItemList.size()>0){
            for (SafetyDictItem dictItem:dictItemList) {
                SafetyDictItem sysDictItem = new SafetyDictItem();
                BeanUtils.copyProperties(dictItem,sysDictItem);
                sysDictItem.setSupperId(id);
                sysDictItem.setId(null);
                sysDictItemMapper.insert(sysDictItem);
            }
        }
    }

    /**
     * 更新字典子项
     * @param id
     * @param dictItemList
     */
    private void updateDictItem(String id,List<SafetyDictItem> dictItemList){
        //先删除在新增 因为排序可能不一致
        LambdaQueryWrapper<SafetyDictItem> query = new LambdaQueryWrapper<>();
        query.eq(SafetyDictItem::getSupperId,id);
        sysDictItemMapper.delete(query);
        //新增子项
        this.addDictItem(Long.valueOf(id),dictItemList);
    }

}
