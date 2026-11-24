package com.yutong.safety.common.utils.excel.style;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Font;

import java.io.Serializable;

@Data
public class FontSet implements Serializable {
    private static final long serialVersionUID = 1L;

    //字体大小
    private short fontSize = 11;
    //字体粗细
    private Boolean fontBold = false;
    //字体类型:微软雅黑
    private String fontType = "宋体";
    //字体颜色
    private short fontColor = Font.COLOR_NORMAL;

    //斜体
    private Boolean italic = false;
    //下划线
    private byte underline = HSSFFont.U_DOUBLE;


    public FontSet() {
    }

    public FontSet(Boolean fontBold) {
        this.fontBold = fontBold;
    }

    public FontSet(Boolean fontBold, String fontType) {
        this.fontBold = fontBold;
        this.fontType = fontType;
    }

    public FontSet(int fontSize, Boolean fontBold, String fontType) {
        this.fontSize = (short) fontSize;
        this.fontBold = fontBold;
        this.fontType = fontType;
    }

    public FontSet(int fontSize, Boolean fontBold, String fontType, short fontColor) {
        this.fontSize = (short) fontSize;
        this.fontBold = fontBold;
        this.fontType = fontType;
        this.fontColor = fontColor;
    }
}
