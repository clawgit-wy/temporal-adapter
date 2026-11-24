package com.yutong.modules.risk.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量导出二维码请求
 */
@Data
public class QrExportReq {

    /** 选中位置ID集合 */
    @NotEmpty(message = "请选择至少一个位置")
    private String  ids;

    /**
     * 需要生成的码类型集合
     * 可选值：sign 签到码，pass 通行码
     * 例：["sign","pass"] 两种都生成
     */
    @NotEmpty(message = "请选择需要导出的二维码类型")
    private List<String> codeTypeList;


    /** 选中位置ID集合 */
    @NotEmpty(message = "二维码图片宽")
    private int  width=200;

    /** 选中位置ID集合 */
    @NotEmpty(message = "二维码图片高")
    private int  height=200;







}