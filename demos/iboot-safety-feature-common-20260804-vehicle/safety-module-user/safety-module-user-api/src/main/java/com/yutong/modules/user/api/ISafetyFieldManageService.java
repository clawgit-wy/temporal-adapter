package com.yutong.modules.user.api;

import com.yutong.modules.user.dto.FieldManageListParam;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.vo.FieldLinkageVO;
import com.yutong.modules.user.vo.FieldManagerTreeVO;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import com.yutong.modules.user.vo.SafetyFieldManageVO;

import java.util.List;

/**
 * @Description: 厂区表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface ISafetyFieldManageService extends IService<SafetyFieldManage> {


    List<SafetyFieldManageVO> getList();

    List<SafetyDictItemVO> selectList(String supperCode);

    List<SafetyDictItemVO> selectListItem(String itemCode, Integer id,String fieldType);

    /**
     * 获取子类别树
     * @param param 查询参数
     * @return 树形结构列表
     */
    List<FieldManagerTreeVO> getSubTree(FieldManageListParam param);

    /**
     * 获取联动列表（厂区-区域树形结构）
     * @return 联动列表
     */
    List<FieldLinkageVO> selectLinkageList();

    List<FieldLinkageVO> selectLinkageListApp();
}
