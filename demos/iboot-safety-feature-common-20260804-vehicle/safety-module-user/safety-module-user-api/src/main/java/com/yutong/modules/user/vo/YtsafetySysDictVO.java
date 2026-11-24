package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yutong.modules.user.entity.SafetyDictItem;
import lombok.Data;

import java.util.List;

@Data
public class YtsafetySysDictVO {
    /**
     * 字典id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 应用id
     */
    private String lowAppId;

    /**
     * 字典子项
     */
    private List<SafetyDictItem> dictItemsList;
}
