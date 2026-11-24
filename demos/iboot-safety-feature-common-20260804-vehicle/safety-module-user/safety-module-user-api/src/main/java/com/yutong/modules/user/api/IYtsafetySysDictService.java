package com.yutong.modules.user.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.dto.SafetyDictSupperDTO;
import com.yutong.modules.user.entity.SafetyDictSupper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.query.SafetyDictSupperQuery;
import com.yutong.modules.user.vo.SafetyDictSupperVO;

import javax.validation.constraints.NotNull;

/**
 * @Description: 字典表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
public interface IYtsafetySysDictService extends IService<SafetyDictSupper> {


    IPage<SafetyDictSupperVO> pageList(Page<SafetyDictSupperVO> page, SafetyDictSupperQuery safetyDictSupperQuery);

    void add(SafetyDictSupperDTO sysDict);

    void edit(SafetyDictSupperDTO dto);

    void deleteDict(Long id);

    SafetyDictSupperVO getTreeById(@NotNull(message = "id不能为空") Long id);
}
