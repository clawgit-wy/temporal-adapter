package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.vo.FieldLinkageVO;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import com.yutong.modules.user.vo.SafetyFieldManageVO;

import java.util.List;

/**
 * @Description: 厂区表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface SafetyFieldManageMapper extends BaseMapper<SafetyFieldManage> {

    // todo 赵喜阳 删除，使用BaseMapper中自带的方法
    List<SafetyFieldManageVO> getList();

    List<SafetyDictItemVO> selectListByCode(String code);

    List<SafetyDictItemVO> selectListItem(String itemCode, Integer id,String fieldType);

    /**
     * 查询联动列表（厂区+区域）
     * @return 联动列表
     */
    List<FieldLinkageVO> selectLinkageList();
    List<FieldLinkageVO> selectLinkageListApp();

    /**
     * 查询联动列表（厂区+区域，不限制remark）
     * @return 联动列表
     */
    List<FieldLinkageVO> selectLinkageListNew();
}
