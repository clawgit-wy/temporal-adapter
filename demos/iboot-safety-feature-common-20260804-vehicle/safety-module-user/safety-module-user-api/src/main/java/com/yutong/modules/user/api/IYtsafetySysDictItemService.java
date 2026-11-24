package com.yutong.modules.user.api;


import com.yutong.modules.user.dto.SafetyDictItemDTO;
import com.yutong.modules.user.entity.SafetyDictItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.yutong.modules.user.query.SafetyDictItemQuery;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @Description: 字典子表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
public interface IYtsafetySysDictItemService extends IService<SafetyDictItem> {


    void add(SafetyDictItemDTO sysDictItemDto);

    void updateDictItem(SafetyDictItemDTO sysDictItem);

    void deleteItem(String id, String type);

    SafetyDictItemVO selectById(Long id);

    List<SafetyDictItemVO> selectByName(SafetyDictItemQuery query);

    List<SafetyDictItemVO> selectList(SafetyDictItemQuery query);

    List<SafetyDictItemVO> selectFlat(SafetyDictItemQuery query);

}
