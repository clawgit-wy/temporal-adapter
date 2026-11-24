package com.yutong.safety.common.utils.excel.style;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;

import java.io.Serializable;

@Data
public class PrintSet implements Serializable {
    private static final long serialVersionUID = 1L;

    //打印方向，false表示纵向
    private Boolean landscape = false;
    //打印纸张类型
    private short paperSize = HSSFPrintSetup.A4_PAPERSIZE;
    //页边距
    private double margin = 0.5;
    //放大/缩小(1-100)，100表示无缩放
    private short scale = 100;
    //印刷品质
    private short resolution = 600;
    //网格线
    private Boolean displayLine = false;
    //打印线
    private Boolean printLine = false;
    //设置打印页面为水平居中
    private Boolean horCenter = true;
    //设置打印页面为垂直居中
    private Boolean verCenter = false;

    public PrintSet() {
    }

    public PrintSet(Boolean landscape, short paperSize) {
        this.landscape = landscape;
        this.paperSize = paperSize;
    }

    public PrintSet(Boolean landscape, short paperSize, double margin) {
        this.landscape = landscape;
        this.paperSize = paperSize;
        this.margin = margin;
    }
}
