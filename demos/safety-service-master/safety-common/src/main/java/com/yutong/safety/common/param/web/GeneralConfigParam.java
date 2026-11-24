package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-2-27 15:21
 */
@Data
public class GeneralConfigParam implements Check {

    /**
     * 数据id
     */
    private Integer id;

    /**
     * 数据类型
     */
    private String generalType;

    /**
     * 是否展示
     */
    public Integer generalShow;

    /**
     * 展示内容
     */
    public String content;

    @Override
    public void check() {
        PreconditonsEx.checkArgument((Objects.nonNull(generalType) || Objects.nonNull(id)), ResponseEnum.PARAM_ERROR.getCode(), "数据id或者数据类型不能为空!");
    }

}
