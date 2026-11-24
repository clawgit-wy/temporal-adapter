package com.yutong.safety.common.utils.excel;

import com.yutong.safety.common.utils.StreamUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OutHSSFUtil {
    /**
     * @param sheetName 工作表名称
     * @param titles    数据表 标题
     * @param values    数据
     * @param wb
     * @param style     单元格样式
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, List<String> titles, Object values[][],
                                               HSSFWorkbook wb, HSSFCellStyle style, int width) {
        //1 创建webbook对象，对应一个excel文件对象
        if (wb == null) wb = new HSSFWorkbook();
        //2 在wb中添加一个sheet，对应excel中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        //3 在sheet中添加标题行,第一行从0开始
        HSSFRow row = sheet.createRow(0);
        //创建excel的单元格
        HSSFCell cell = null;
        //循环title，创建标题
        for (int i = 0; i < titles.size(); i++) {
            sheet.setColumnWidth(i, PubExcelUtil.setColumnWidth(width));
            cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
            cell.setCellStyle(style);
        }

        //循环创建内容
        HSSFCellStyle nextStyle = setHSSCellStyle(wb, 12);
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);//+1表示从第二行开始
            for (int j = 0; j < values[i].length; j++) {
                Object obj = values[i][j];
                //设置单元格样式
                HSSFCell cellNext = row.createCell(j);
                cellNext.setCellStyle(nextStyle);
                setHSSFCellValue(obj, cellNext, sheet, i, j);
            }
        }
        return wb;
    }

    //批量设置单元格数据
    public static synchronized void batchSetHSSFCellValue(HSSFCellStyle nextStyle, HSSFSheet sheet, Object values[][], int dataRow) {
        HSSFRow row = null;
        //循环创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + dataRow);//数据开始行
            for (int j = 0; j < values[i].length; j++) {
                Object obj = values[i][j];
                //设置单元格样式
                HSSFCell cellNext = row.createCell(j);
                cellNext.setCellStyle(nextStyle);
                setHSSFCellValue(obj, cellNext, sheet, i, j);
            }
        }
    }

    //设置单元格数据
    public static void setHSSFCellValue(Object obj, HSSFCell cellNext, HSSFSheet sheet,
                                        int i, int j) {
        if (obj != null) {
            if (obj instanceof String) {
                cellNext.setCellValue(new HSSFRichTextString(obj.toString()));
            } else if (obj instanceof Date) {
                cellNext.setCellValue((Date) obj);
            } else if (obj instanceof Integer) {
                cellNext.setCellValue(((Integer) obj).intValue());
            } else if (obj instanceof Double) {
                cellNext.setCellValue(((Double) obj).doubleValue());
            } else if (obj instanceof Float) {
                cellNext.setCellValue(((Float) obj).floatValue());
            } else if (obj instanceof BigDecimal) {
                cellNext.setCellValue(((BigDecimal) obj).floatValue());
            } else if (obj instanceof Long) {
                cellNext.setCellValue(((Long) obj).longValue());
            } else if (obj instanceof Boolean) {
                cellNext.setCellValue(((Boolean) obj).booleanValue());
            } else if (obj.getClass().isArray()) {
                // 设置第一列的1-10行为下拉列表
                CellRangeAddressList regions = new CellRangeAddressList(i + 1, i + 1, j, j);
                // 创建下拉列表数据
                String[] arr = (String[]) obj;
                DVConstraint constraint = DVConstraint.createExplicitListConstraint(arr);
                // 绑定
                HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
                //dataValidation.setSuppressDropDownArrow(true);
                sheet.addValidationData(dataValidation);
                cellNext.setCellValue(arr[0]);
            } else {
                cellNext.setCellValue(obj.toString());
            }
        } else {
            cellNext.setCellValue("");
        }
    }

    /*添加图片*/
    public static void setImage(HSSFWorkbook wb, HSSFSheet sheet, String filePath){
        ByteArrayOutputStream byteArrayOut = null;
        try {
            //先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
            byteArrayOut = new ByteArrayOutputStream();
            BufferedImage bufferImg = ImageIO.read(new File(filePath));
            String formatName = filePath.substring(filePath.lastIndexOf("."));
            ImageIO.write(bufferImg, formatName, byteArrayOut);

            //画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            //anchor主要用于设置图片的属性
            /**
             dx1 - the x coordinate within the first cell.//定义了图片在第一个cell内的偏移x坐标，既左上角所在cell的偏移x坐标，一般可设0
             dy1 - the y coordinate within the first cell.//定义了图片在第一个cell的偏移y坐标，既左上角所在cell的偏移y坐标，一般可设0
             dx2 - the x coordinate within the second cell.//定义了图片在第二个cell的偏移x坐标，既右下角所在cell的偏移x坐标，一般可设0
             dy2 - the y coordinate within the second cell.//定义了图片在第二个cell的偏移y坐标，既右下角所在cell的偏移y坐标，一般可设0
             col1 - the column (0 based) of the first cell.//第一个cell所在列，既图片左上角所在列
             row1 - the row (0 based) of the first cell.//图片左上角所在行
             col2 - the column (0 based) of the second cell.//图片右下角所在列
             row2 - the row (0 based) of the second cell.//图片右下角所在行
             */
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 255, 255,(short) 1, 1, (short) 5, 8);
            anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
            //插入图片
            patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            StreamUtil.close(byteArrayOut);
        }

    }

    /*设置导出数据excel文件单元格的样式*/
    public static HSSFCellStyle setHSSCellStyle(HSSFWorkbook wb, int fontSize) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        style.setAlignment(HorizontalAlignment.CENTER);//字体居中
        style.setWrapText(true);//设置不自动换行
        setHSSFCellBorder(style);
        font.setBold(true);//粗体显示
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName("宋体");
        style.setFont(font);
        return style;
    }

    public static HSSFCellStyle setTitleHSSCellStyle(HSSFWorkbook wb, int fontSize) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        style.setAlignment(HorizontalAlignment.CENTER);//字体居中
        style.setWrapText(true);//设置不自动换行
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setHSSFCellBorder(style);
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(true);//粗体显示
        font.setFontName("黑体");
        style.setFont(font);

        return style;
    }

    public static HSSFCellStyle setHSSCellStyleRegion(HSSFWorkbook wb, int fontSize) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        style.setAlignment(HorizontalAlignment.CENTER);//字体居中
        style.setWrapText(true);//设置不自动换行
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName("宋体");
        style.setFont(font);
        return style;
    }

    public static void setHSSFCellBorder(HSSFCellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
    }

    //创建合并单元格
    public static CellRangeAddress setHSSFMergedRegion(HSSFSheet sheet, HSSFCell cell, HSSFCellStyle style,
                                                       String cellValue, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol); // 起始行, 终止行, 起始列, 终止列
        sheet.addMergedRegion(cra);
        cell.setCellValue(cellValue);
        cell.setCellStyle(style);
        setRegionBorder(cra, sheet);
        return cra;
    }

    public static void setRegionBorder(CellRangeAddress range, HSSFSheet sheet) {
        // 使用RegionUtil类为合并后的单元格添加边框
        RegionUtil.setBorderBottom(BorderStyle.THIN, range, sheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, range, sheet); // 左边框
        RegionUtil.setBorderRight(BorderStyle.THIN, range, sheet); // 有边框
        RegionUtil.setBorderTop(BorderStyle.THIN, range, sheet); // 上边框
    }

    public static byte[] toByteArray(HSSFWorkbook wb) {
        byte[] content = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
            content = os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            StreamUtil.close(os);
            PubExcelUtil.close(wb);
        }
        return content;
    }
}
