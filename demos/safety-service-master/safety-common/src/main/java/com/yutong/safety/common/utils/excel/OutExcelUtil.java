package com.yutong.safety.common.utils.excel;

import com.yutong.safety.common.utils.excel.style.OutStyle;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * excel下载公共类
 */
public class OutExcelUtil {
    /**
     * @param sheetName 工作表名称
     * @param titles 数据表 标题
     * @param values 数据
     * @param wb
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(HSSFWorkbook wb, String sheetName, String mainName,
                                               List<String> titles, Object values[][], int width){
        //创建workbook对象，对应一个excel文件对象
        if(wb == null) wb = new HSSFWorkbook();
        //定义excel的单元格对象
        HSSFCell cell;
        //在wb中添加一个sheet，对应excel中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        //创建主标题,从第一行0开始
        HSSFRow mainRow = sheet.createRow(0);
        cell = mainRow.createCell(0);
        mainRow.setHeight(PubExcelUtil.setRowHigh(20));
        HSSFCellStyle mainStyle = OutStyle.getHSSMainStyle(wb, sheet, 0, 0, 0, titles.size()-1);
        cell.setCellValue(mainName);
        cell.setCellStyle(mainStyle);
        //创建字段标题,从第二行1开始
        HSSFRow row = sheet.createRow(1);
        //循环title，创建标题
        HSSFCellStyle titleStyle = OutStyle.getHSSTitleStyle(wb, sheet);
        for(int i=0; i<titles.size(); i++){
            sheet.setColumnWidth(i, PubExcelUtil.setColumnWidth(width));
            row.setHeight(PubExcelUtil.setRowHigh(20));
            cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
            cell.setCellStyle(titleStyle);
        }
        //添加详细数据
        setCellValue(wb, sheet, values);
        return wb;
    }

    public static HSSFWorkbook getHSSFWorkbook(HSSFWorkbook wb, HSSFSheet sheet, String mainName,
                                               List<String[]> titles, Object values[][]){
        //创建workbook对象，对应一个excel文件对象
        if(wb == null) wb = new HSSFWorkbook();
        //定义excel的单元格对象
        HSSFCell cell;
        //创建主标题,从第一行0开始
        HSSFRow mainRow = sheet.createRow(0);
        cell = mainRow.createCell(0);
        mainRow.setHeight(PubExcelUtil.setRowHigh(20));
        HSSFCellStyle mainStyle = OutStyle.getHSSMainStyle(wb, sheet, 0, 0, 0, titles.size()-1);
        cell.setCellValue(mainName);
        cell.setCellStyle(mainStyle);
        //创建字段标题,从第二行1开始
        HSSFRow row = sheet.createRow(1);
        //循环title，创建标题
        HSSFCellStyle titleStyle = OutStyle.getHSSTitleStyle(wb, sheet);
        for(int i=0; i<titles.size(); i++){
            sheet.setColumnWidth(i, PubExcelUtil.setColumnWidth(Integer.valueOf(titles.get(i)[1])));
            row.setHeight(PubExcelUtil.setRowHigh(20));
            cell = row.createCell(i);
            cell.setCellValue(titles.get(i)[0]);
            cell.setCellStyle(titleStyle);
        }
        //添加详细数据
        setCellValue(wb, sheet, values);
        return wb;
    }

    public static void setCellValue(HSSFWorkbook wb, HSSFSheet sheet, Object values[][]){
        //循环创建内容
        HSSFCellStyle nextStyle = OutStyle.getHSSCellStyle(wb, sheet);
        HSSFRow row;
        for(int i=0; i<values.length; i++){
            row = sheet.createRow(i+2);//+2表示从第三行开始
            row.setHeight(PubExcelUtil.setRowHigh(26));
            for(int j=0; j<values[i].length; j++){
                Object obj = values[i][j];
                //设置单元格样式
                HSSFCell cellNext = row.createCell(j);
                cellNext.setCellStyle(nextStyle);
                setCellValue(obj,cellNext);
            }
        }
    }

    //设置单元格值
    public static void setCellValue(Object obj, HSSFCell cellNext){
        if(obj != null){
            if(obj instanceof String){
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
            } else {
                cellNext.setCellValue(obj.toString());
            }
        }else {
            cellNext.setCellValue("");
        }
    }
    //设置单元格值 包含下拉选项
    public static void setCellValue(Object obj, HSSFCell cellNext, HSSFSheet sheet,
                                    int i, int j){
        if(obj != null){
            if(obj instanceof String){
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
            } else if(obj.getClass().isArray()){
                // 设置第一列的1-10行为下拉列表
                CellRangeAddressList regions = new CellRangeAddressList(i+1, i+1, j, j);
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
        }else {
            cellNext.setCellValue("");
        }
    }

}
