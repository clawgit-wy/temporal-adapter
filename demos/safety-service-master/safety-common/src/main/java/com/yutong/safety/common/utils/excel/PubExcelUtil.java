package com.yutong.safety.common.utils.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * excel文件类型，数据类型处理
 */
public class PubExcelUtil {

    private final static String excel_xls = ".xls";
    private final static String excel_xlsx = ".xlsx";

    public static Integer checkExcelType(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(fileType.equals(excel_xls)){
            return 1;
        }else if(fileType.equals(excel_xlsx)){
            return 2;
        }else {
            return 3;
        }
    }

    public static String getCellStringValue(Cell cell){
        String cellValue = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CellType cellType = cell.getCellType();
        if(cellType.equals(CellType.STRING)){//文本
            cellValue = excelXSS(cell.getStringCellValue());
        }else if(cellType.equals(CellType.NUMERIC)){
            if(DateUtil.isCellDateFormatted(cell)) {
                cellValue = sdf.format(cell.getDateCellValue()); //日期型
            } else {
                cellValue = String.valueOf(cell.getNumericCellValue()); //数字
            }
        }else if(cellType.equals(CellType.BOOLEAN)){//布尔型
            cellValue = String.valueOf(cell.getBooleanCellValue());
        }else if(cellType.equals(CellType.BLANK)){//空白
            cellValue = cell.getStringCellValue();
        }else if(cellType.equals(CellType.ERROR)){//错误
            cellValue = "错误";
        }else if(cellType.equals(CellType.FORMULA)){//公式
            cellValue = "公式";
        }else {
            cellValue = excelXSS(cell.getStringCellValue());
        }
        return cellValue;
    }

    public static Object getCellObjectValue(Cell cell){
        Object cellValue = "";
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CellType cellType = cell.getCellType();
        if(cellType.equals(CellType.STRING)){//文本
            cellValue = excelXSS(cell.getStringCellValue());
        }else if(cellType.equals(CellType.NUMERIC)){
            if(DateUtil.isCellDateFormatted(cell)) {
                cellValue = cell.getDateCellValue(); //日期型
            } else {
                cellValue = cell.getNumericCellValue(); //数字
            }
        }else if(cellType.equals(CellType.BOOLEAN)){//布尔型
            cellValue = cell.getBooleanCellValue();
        }else if(cellType.equals(CellType.BLANK)){//空白
            cellValue = cell.getStringCellValue();
        }else if(cellType.equals(CellType.ERROR)){//错误
            cellValue = "错误";
        }else if(cellType.equals(CellType.FORMULA)){//公式
            cellValue = "公式";
        }else {
            cellValue = excelXSS(cell.getStringCellValue());
        }
        return cellValue;
    }

    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "iso-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpHeaders setResponseHeader(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gbk"), "iso8859-1"));
            headers.setContentType(MediaType.valueOf("application/vnd.ms-excel;charset=UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headers;
    }

    public static HttpHeaders setResponseHeaderStream(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gbk"), "iso8859-1"));
            headers.setContentType(MediaType.valueOf("application/octet-stream;charset=UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headers;
    }

    /*设置单元格宽度*/
    public static int setColumnWidth(int width){
        return 256*width+184;
    }
    /*设置行高*/
    public static short setRowHigh(int high){
        return Short.valueOf(String.valueOf(high*20));
    }

    public static void close(Workbook workbook){
        try {
            if(workbook != null){
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String excelXSS(String value){
        String valueNew = value;
        if (StringUtils.isNotEmpty(valueNew)) {
            Pattern pattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            if(pattern.matcher(valueNew).find()){
                throw new RuntimeException("文本中包括特殊字符，限制导入");
            }

            // 避免 onerror= 表达式
            pattern = Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            if(pattern.matcher(valueNew).find()){
                throw new RuntimeException("文本中包括特殊字符，限制导入");
            }

            // 避免 onload= 表达式
            pattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            if(pattern.matcher(valueNew).find()){
                throw new RuntimeException("文本中包括特殊字符，限制导入");
            }
        }
        return valueNew;
    }
}
