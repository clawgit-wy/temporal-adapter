package com.yutong.safety.common.utils.excel.style;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;

import java.io.Serializable;

@Data
public class CellSet implements Serializable {
    private static final long serialVersionUID = 1L;

    //边框样式
    private BorderStyle borderTop = BorderStyle.THIN;
    private BorderStyle borderBottom = BorderStyle.THIN;
    private BorderStyle borderLeft = BorderStyle.THIN;
    private BorderStyle borderRight = BorderStyle.THIN;
    //文本水平对齐
    private HorizontalAlignment horAlign = HorizontalAlignment.CENTER;
    //文本垂直对齐
    private VerticalAlignment verAlign = VerticalAlignment.CENTER;
    //是否自动换行
    private Boolean wrapped = true;
    //背景色
    private short background = -1;
    //背景色填充方式
    private FillPatternType patternType = FillPatternType.SOLID_FOREGROUND;

    //是否自定义背景色
    private Boolean colorAt = false;
    //自定义背景色编号
    private byte red = (byte) 255;
    private byte green = (byte) 255;
    private byte blue = (byte) 255;

    public CellSet() {
    }

    public CellSet(Boolean colorAt, int red, int green, int blue) {
        this.colorAt = colorAt ;
        this.red = (byte) red;
        this.green = (byte) green;
        this.blue = (byte) blue;
        this.background = IndexedColors.GREY_80_PERCENT.getIndex();
    }

    public CellSet(short background) {
        this.background = background ;
    }

    public CellSet(HorizontalAlignment horAlign) {
        this.horAlign = horAlign ;
    }
}
