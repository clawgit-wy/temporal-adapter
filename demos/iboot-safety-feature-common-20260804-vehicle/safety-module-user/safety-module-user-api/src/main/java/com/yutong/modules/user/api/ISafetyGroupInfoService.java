package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.vo.SafetyGroupInfoOptionListVO;
import com.yutong.modules.user.vo.SafetyGroupInfoOptionVO;

import java.util.List;

/**
 * @Description: 部门表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface ISafetyGroupInfoService extends IService<SafetyGroupInfo> {

    /**
     * 下拉列表
     * @return
     */
    List<SafetyGroupInfoOptionListVO> optionlist();

    List<SafetyGroupInfoOptionVO> getList();


}
