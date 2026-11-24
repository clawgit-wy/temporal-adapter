package com.yutong.safety.common.utils.excel;

import com.yutong.safety.common.utils.StreamUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OutXSSFUtil {
    /**
     * @param sheetName 工作表名称
     * @param titles    数据表 标题
     * @param values    数据
     * @param wb
     * @param style     单元格样式
     * @return
     */
    public static XSSFWorkbook getXSSFWorkbook(String sheetName, List<String> titles, Object values[][],
                                               XSSFWorkbook wb, XSSFCellStyle style, int width) {
        //1 创建webbook对象，对应一个excel文件对象
        if (wb == null) wb = new XSSFWorkbook();
        //SXSSFWorkbook workbook = new SXSSFWorkbook();
        //Workbook book = new SXSSFWorkbook();
        //Font font = book.createFont();
        //2 在wb中添加一个sheet，对应excel中的sheet
        XSSFSheet sheet = wb.createSheet(sheetName);
        //3 在sheet中添加标题行,第一行从0开始
        XSSFRow row = sheet.createRow(0);
        //创建excel的单元格
        XSSFCell cell = null;
        //循环title，创建标题
        for (int i = 0; i < titles.size(); i++) {
            sheet.setColumnWidth(i, PubExcelUtil.setColumnWidth(width));
            cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
            cell.setCellStyle(style);
        }

        //循环创建内容
        XSSFCellStyle nextStyle = setXSSCellStyle(wb, 12);
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);//+1表示从第二行开始
            for (int j = 0; j < values[i].length; j++) {
                Object obj = values[i][j];
                //设置单元格样式
                XSSFCell cellNext = row.createCell(j);
                cellNext.setCellStyle(nextStyle);
                if (obj != null) {
                    if (obj instanceof String) {
                        cellNext.setCellValue(obj.toString());
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
                    } else {
                        cellNext.setCellValue(obj.toString());
                    }
                } else {
                    cellNext.setCellValue("");
                }
            }
        }
        return wb;
    }

    public static XSSFWorkbook getXSSFWorkbook(Object values[][], XSSFWorkbook wb, XSSFSheet sheet, int dataRow, int fontSize) {
        XSSFRow row;
        //循环创建内容
        XSSFCellStyle nextStyle = setXSSCellStyle(wb, fontSize);
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + dataRow);
            for (int j = 0; j < values[i].length; j++) {
                Object obj = values[i][j];
                //设置单元格样式
                XSSFCell cellNext = row.createCell(j);
                cellNext.setCellStyle(nextStyle);
                if (obj != null) {
                    if (obj instanceof String) {
                        cellNext.setCellValue(obj.toString());
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
                    } else {
                        cellNext.setCellValue(obj.toString());
                    }
                } else {
                    cellNext.setCellValue("");
                }
            }
        }
        return wb;
    }

    public static void getXSSFWorkbook(Object obj, XSSFRow row, XSSFCellStyle nextStyle, int columnIndex) {
        XSSFCell cellNext = row.createCell(columnIndex);
        cellNext.setCellStyle(nextStyle);
        if (obj != null) {
            if (obj instanceof String) {
                cellNext.setCellValue(obj.toString());
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
            } else {
                cellNext.setCellValue(obj.toString());
            }
        } else {
            cellNext.setCellValue("");
        }
    }

    /*部分文本加粗*/
    public static XSSFWorkbook getXSSFWorkbookPartBorder(Object values[][], List<Integer> partCell, String part,
                                                         XSSFWorkbook wb, XSSFSheet sheet, int dataRow, int fontSize) {
        XSSFRow row;
        //循环创建内容
        XSSFFont font = createFont(wb, fontSize);
        XSSFCellStyle nextStyle = setXSSCellStyle(wb, font);
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + dataRow);
            for (int j = 0; j < values[i].length; j++) {
                Object obj = values[i][j];
                //设置单元格样式
                XSSFCell cellNext = row.createCell(j);
                cellNext.setCellStyle(nextStyle);
                if (obj != null) {
                    if (obj instanceof String) {
                        String content = obj.toString();
                        if (partCell.contains(j)){
                            int s = content.indexOf(part);
                            int e = s + part.length();
                            XSSFRichTextString text = setPartBorder(font, content, s, e);
                            cellNext.setCellValue(text);
                        }else {
                            cellNext.setCellValue(content);
                        }
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
                    } else {
                        cellNext.setCellValue(obj.toString());
                    }
                } else {
                    cellNext.setCellValue("");
                }
            }
        }
        return wb;
    }

    //批量设置单元格数据
    public static synchronized void batchSetXSSFCellValue(XSSFCellStyle nextStyle, XSSFSheet sheet, Object values[][], int dataRow) {
        XSSFRow row = null;
        //循环创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + dataRow);//数据开始行
            for (int j = 0; j < values[i].length; j++) {
                Object obj = values[i][j];
                //设置单元格样式
                XSSFCell cellNext = row.createCell(j);
                cellNext.setCellStyle(nextStyle);
                setXSSFCellValue(obj, cellNext, sheet, i, j);
            }
        }
    }

    //设置单元格数据
    public static void setXSSFCellValue(Object obj, XSSFCell cellNext, XSSFSheet sheet,
                                        int i, int j) {
        if (obj != null) {
            if (obj instanceof String) {
                cellNext.setCellValue(new XSSFRichTextString(obj.toString()));
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
                DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
                DataValidationConstraint createExplicitListConstraint = dataValidationHelper.createExplicitListConstraint(arr);
                DataValidation createValidation = dataValidationHelper.createValidation(createExplicitListConstraint, regions);
                //处理Excel兼容性问题
                if (createValidation instanceof XSSFDataValidation) {
                    createValidation.setSuppressDropDownArrow(true);
                    createValidation.setShowErrorBox(true);
                } else {
                    createValidation.setSuppressDropDownArrow(false);
                }
                // 绑定
                //createValidation.setSuppressDropDownArrow(true);
                sheet.addValidationData(createValidation);
                cellNext.setCellValue(arr[0]);
            } else {
                cellNext.setCellValue(obj.toString());
            }
        } else {
            cellNext.setCellValue("");
        }
    }

    /*设置导出数据excel文件单元格的样式*/
    public static XSSFCellStyle setXSSCellStyle(XSSFWorkbook wb, XSSFFont font) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);//字体居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);//设置不自动换行
        style.setFont(font);
        setXSSFCellBorder(style);
        return style;
    }
    public static XSSFCellStyle setXSSCellStyle(XSSFWorkbook wb, int fontSize) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        style.setAlignment(HorizontalAlignment.CENTER);//字体居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);//设置不自动换行
        setXSSFCellBorder(style);
        font.setBold(true);//粗体显示
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName("宋体");
        style.setFont(font);
        return style;
    }

    public static XSSFCellStyle setTitleXSSCellStyle(XSSFWorkbook wb, int fontSize) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        style.setAlignment(HorizontalAlignment.CENTER);//字体居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);//设置不自动换行
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setXSSFCellBorder(style);
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(true);//粗体显示
        font.setFontName("黑体");
        style.setFont(font);

        return style;
    }

    public static void setXSSFCellBorder(XSSFCellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }

    //创建合并单元格
    public static CellRangeAddress setXSSFMergedRegion(XSSFSheet sheet, XSSFCell cell, XSSFCellStyle style,
                                                       String cellValue, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol); // 起始行, 终止行, 起始列, 终止列
        sheet.addMergedRegion(cra);
        cell.setCellValue(cellValue);
        cell.setCellStyle(style);
        setRegionBorder(cra, sheet);
        return cra;
    }

    public static void setRegionBorder(CellRangeAddress range, XSSFSheet sheet) {
        // 使用RegionUtil类为合并后的单元格添加边框
        RegionUtil.setBorderBottom(BorderStyle.THIN, range, sheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, range, sheet); // 左边框
        RegionUtil.setBorderRight(BorderStyle.THIN, range, sheet); // 有边框
        RegionUtil.setBorderTop(BorderStyle.THIN, range, sheet); // 上边框
    }

    //部分文本加粗
    public static XSSFRichTextString setPartBorder(XSSFFont font, String text, int s, int e){
        XSSFRichTextString string = new XSSFRichTextString(text);
        font.setBold(true);
        string.applyFont(s,e,font);
        return string;
    }

    public static XSSFFont createFont(XSSFWorkbook wb, int fontSize){
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName("宋体");
        return font;
    }

    public static byte[] toByteArray(XSSFWorkbook wb) {
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
