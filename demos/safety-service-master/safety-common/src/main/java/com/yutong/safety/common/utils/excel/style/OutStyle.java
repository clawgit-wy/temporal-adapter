package com.yutong.safety.common.utils.excel.style;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

public class OutStyle{
    public static HSSFCellStyle getHSSMainStyle(HSSFWorkbook wb, HSSFSheet sheet,
                                                int firstRow, int lastRow, int firstCol, int lastCol){
        CellSet cellSet = new CellSet(IndexedColors.PALE_BLUE.getIndex());
        FontSet fontSet = new FontSet(12, true, "微软雅黑");
        PrintSet printSet = new PrintSet();
        //合并单元格  起始行, 终止行, 起始列, 终止列
        CellRangeAddress cra = new CellRangeAddress(firstRow,lastRow,firstCol,lastCol);
        sheet.addMergedRegion(cra);
        //使用RegionUtil类为合并后的单元格添加边框
        RegionUtil.setBorderTop(cellSet.getBorderTop(), cra, sheet); // 上边框
        RegionUtil.setBorderRight(cellSet.getBorderRight(), cra, sheet); // 下边框
        RegionUtil.setBorderLeft(cellSet.getBorderLeft(), cra, sheet); // 左边框
        RegionUtil.setBorderRight(cellSet.getBorderRight(), cra, sheet); // 右边框
        return getHSSStyle(wb, sheet, cellSet, fontSet, printSet);
    }

    public static HSSFCellStyle getHSSTitleStyle(HSSFWorkbook wb, HSSFSheet sheet){
        CellSet cellSet = new CellSet(true, (byte) 220, (byte) 230, (byte) 240);
        FontSet fontSet = new FontSet(true);
        PrintSet printSet = new PrintSet();
        return getHSSStyle(wb, sheet, cellSet, fontSet, printSet);
    }

    public static HSSFCellStyle getHSSCellStyle(HSSFWorkbook wb, HSSFSheet sheet){
        CellSet cellSet = new CellSet();
        FontSet fontSet = new FontSet();
        PrintSet printSet = new PrintSet();
        return getHSSStyle(wb, sheet, cellSet, fontSet, printSet);
    }

    public static HSSFCellStyle getHSSStyle(HSSFWorkbook wb, HSSFSheet sheet,
                                            CellSet cellSet, FontSet fontSet, PrintSet printSet){
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(cellSet.getHorAlign());
        style.setWrapText(cellSet.getWrapped());
        if(cellSet.getColorAt()){
            HSSFPalette palette = wb.getCustomPalette();
            palette.setColorAtIndex(cellSet.getBackground(), (byte)220,(byte)230,(byte)240);
        }
        if(cellSet.getBackground() != -1){
            style.setFillForegroundColor(cellSet.getBackground());
            style.setFillPattern(cellSet.getPatternType());
        }
        style.setVerticalAlignment(cellSet.getVerAlign());
        style.setBorderTop(cellSet.getBorderTop());
        style.setBorderBottom(cellSet.getBorderBottom());
        style.setBorderLeft(cellSet.getBorderLeft());
        style.setBorderRight(cellSet.getBorderRight());

        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints(fontSet.getFontSize());
        font.setBold(fontSet.getFontBold());
        font.setFontName(fontSet.getFontType());
        font.setColor(fontSet.getFontColor());
        style.setFont(font);

        //设置打印参数
        //页边距
        sheet.setMargin(HSSFSheet.LeftMargin, printSet.getMargin());// 页边距（上）
        sheet.setMargin(HSSFSheet.RightMargin, printSet.getMargin());// 页边距（下）
        sheet.setMargin(HSSFSheet.TopMargin, printSet.getMargin());// 页边距（左）
        sheet.setMargin(HSSFSheet.BottomMargin, printSet.getMargin());// 页边距（右）

        HSSFPrintSetup ps = sheet.getPrintSetup();
        //印刷品质
        //ps.setVResolution(printSet.getResolution());
        //放大/缩小(1-100)
        ps.setScale(printSet.getScale());
        //打印方向，true：横向，false：纵向(默认)
        ps.setLandscape(printSet.getLandscape());
        //纸张类型
        ps.setPaperSize(printSet.getPaperSize());

        //网格线
        sheet.setDisplayGridlines(printSet.getDisplayLine());
        //打印线
        sheet.setPrintGridlines(printSet.getPrintLine());
        //设置打印页面为水平居中
        sheet.setHorizontallyCenter(printSet.getHorCenter());
        //设置打印页面为垂直居中
        sheet.setVerticallyCenter(printSet.getVerCenter());

        return style;
    }

}
