package com.yutong.modules.risk.util;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.yutong.modules.risk.constants.YTResponseEnum;
import com.yutong.modules.risk.exception.YTBizException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by gwcuiyx on 23-07-10.
 */
public class YTExcelUtils {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    /**
     * 获取excel表格Book
     * @param sheetName
     * @param title
     * @param values
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values) {
        HSSFWorkbook wb = new HSSFWorkbook() ;
        HSSFSheet sheet = wb.createSheet(sheetName) ;
        HSSFRow row = sheet.createRow(0) ;
        HSSFCellStyle style = wb.createCellStyle() ;
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //自动换行
        style.setWrapText(true);
        //设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        HSSFCell cell = null ;
        for(int i=0; i<title.length; i++) {
            cell = row.createCell(i) ;
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //自动换行
        style.setWrapText(false);
        for(int i=0; i<values.length; i++) {
            row = sheet.createRow(i + 1) ;
            for(int j=0; j<values[i].length; j++) {
                cell = row.createCell(j);
                cell.setCellStyle(style);
                cell.setCellValue(values[i][j]);
            }
        }
        return wb ;
    }

    /**
     * 追加多sheet
     * @param wb
     * @param sheetName
     * @param title
     * @param values
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbookWithSheets(HSSFWorkbook wb,String sheetName, String[] title, String[][] values) {
        HSSFSheet sheet = wb.createSheet(sheetName) ;
        HSSFRow row = sheet.createRow(0) ;
        HSSFCellStyle style = wb.createCellStyle() ;
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //自动换行
        style.setWrapText(true);
        //设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        HSSFCell cell = null ;
        for(int i=0; i<title.length; i++) {
            cell = row.createCell(i) ;
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //自动换行
        style.setWrapText(false);
        for(int i=0; i<values.length; i++) {
            row = sheet.createRow(i + 1) ;
            for(int j=0; j<values[i].length; j++) {
                cell = row.createCell(j);
                cell.setCellStyle(style);
                cell.setCellValue(values[i][j]);
            }
        }
        return wb ;
    }

    /**
     * 判断Excel的版本,获取Workbook
     *
     * @param in
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream in, String fileName) throws IOException {
        Workbook wb = null;
        if (fileName.endsWith(EXCEL_XLS)) {  //Excel 2003
            wb = new HSSFWorkbook(in);
        } else if (fileName.endsWith(EXCEL_XLSX)) {  // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
     * 判断文件是否是excel
     *
     * @param file
     * @return
     */
    public static Boolean checkExcelVaild(File file) {
        Boolean flag = true;
        if (!file.exists()) {
            flag = false;
        }
        if (!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))) {
            flag = false;
        }
        return flag;
    }

    public static String encodeDownloadFilename(String filename, String agent)
            throws IOException {
        if (agent.contains("Firefox")) { // 火狐浏览器
            filename = "=?UTF-8?B?"
                    + new BASE64Encoder().encode(filename.getBytes("utf-8"))
                    + "?=";
            filename = filename.replaceAll("\r\n", "");
        } else { // IE及其他浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        return filename;
    }

    //表头单元格类型
    public static XSSFCellStyle getHeadStyle(XSSFWorkbook workbook, short color) {
        XSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headStyle.setWrapText(true);
        headStyle.setFillForegroundColor(color);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setBorderBottom(BorderStyle.THIN);
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderRight(BorderStyle.THIN);
        headStyle.setBorderTop(BorderStyle.THIN);
        return headStyle;
    }

    //表头单元格类型
    public  static XSSFCellStyle getHeadStyleWithBorder(XSSFWorkbook workbook, XSSFColor color,String word,Short wordSize,Boolean isBold) {
        XSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headStyle.setWrapText(true);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setBorderBottom(BorderStyle.THIN);
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderRight(BorderStyle.THIN);
        headStyle.setBorderTop(BorderStyle.THIN);
        headStyle.setFillForegroundColor(color);
        XSSFFont font = workbook.createFont();
        font.setFontName(word);
        font.setFontHeightInPoints(wordSize);
        font.setBold(isBold);
        headStyle.setFont(font);
        return headStyle;
    }

    //表头单元格类型
    public  static XSSFCellStyle getHeadStyleWithBorder(XSSFWorkbook workbook, short color,String word,Short wordSize,Boolean isBold) {
        XSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headStyle.setWrapText(true);
        headStyle.setFillForegroundColor(color);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setBorderBottom(BorderStyle.THIN);
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderRight(BorderStyle.THIN);
        headStyle.setBorderTop(BorderStyle.THIN);
        XSSFFont font = workbook.createFont();
        font.setFontName(word);
        font.setFontHeightInPoints(wordSize);
        font.setBold(isBold);
        headStyle.setFont(font);
        return headStyle;
    }

    //解决excel类型问题，获得数值
    public static String getValue(Cell cell) {
        String cellValue = "";
        if (null != cell) {
            //以下是判断数据类型
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) { //判断是否为日期类型
                        Date date = cell.getDateCellValue();
                        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                        cellValue = formater.format(date);
                    } else {
                        DecimalFormat df = new DecimalFormat("####.####");
                        cellValue = df.format(cell.getNumericCellValue());
                    }
                    break;
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case BOOLEAN:
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case BLANK: //空值
                    cellValue = "";
                    break;
                case ERROR:
                    cellValue = "非法字符";
                    break;
            }
        }
        return cellValue;
    }
    public static Workbook getWorkbook(MultipartFile file) {
        //效验文件是否存在
        if(file ==null){
            throw new YTBizException(YTResponseEnum.File_NOT_EXIT.getCode(),YTResponseEnum.File_NOT_EXIT.getName());
        }
        //效验文件格式是否正确
        String fileName = file.getOriginalFilename();
        if(!fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
            throw new YTBizException(YTResponseEnum.File_FORMART_ERROR.getCode(),YTResponseEnum.File_FORMART_ERROR.getName());
        }
        Workbook workbook = null;
        InputStream is = null;
        try {
            is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith("xls")){
                //2003
                workbook = new HSSFWorkbook(is);
            }else {
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new YTBizException(YTResponseEnum.FAIL.getCode(),YTResponseEnum.FAIL.getName());
        }
        return workbook;
    }
    //内容单元格类型
    public static XSSFCellStyle getContentStyle(XSSFWorkbook workbook) {
        XSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setWrapText(true);
        return contentStyle;
    }

    //带边框内容单元格类型
    public static XSSFCellStyle getContentStyleWithBorder(XSSFWorkbook workbook) {
        XSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setWrapText(true);
        return contentStyle;
    }

    //获取字体
    public static Font getFont(XSSFWorkbook workbook,String word,short wordSize,boolean isBold){
        XSSFFont font = workbook.createFont();
        font.setFontName(word);
        font.setBold(isBold);
        font.setFontHeightInPoints(wordSize);
        return font;
    }

    /**
     * 将Object对象输出,自动判断类型
     * @param response response
     * @param obj obj
     * @param fileName 文件名(支持中文)
     */
    public static void outWriteObject(HttpServletResponse response, Object obj, String fileName) throws Exception {
        fileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
        response.reset();
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        // 初始化输出流
        BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream());
        InputStream is = null;
        try {
            if (null == obj) {
                return;
            } else if (obj instanceof byte[]) {
                byte[] buffer = (byte[]) obj;
                os.write(buffer, 0, buffer.length);
            } else if (obj instanceof HSSFWorkbook) {
                HSSFWorkbook wbook = (HSSFWorkbook) obj;
                wbook.write(os);
            }else if (obj instanceof XSSFWorkbook) {
                XSSFWorkbook wbook = (XSSFWorkbook) obj;
                wbook.write(os);
            }else if (obj instanceof SXSSFWorkbook) {
                SXSSFWorkbook wbook = (SXSSFWorkbook) obj;
                wbook.write(os);
            }  else if (obj instanceof XWPFDocument) {
                XWPFDocument doc = (XWPFDocument) obj;
                doc.write(os);
            }  else if (obj instanceof InputStream) {
                is = (InputStream) obj;
                int maxFileSize = 3 * 1024 * 1024; // 3M
                byte[] buffer = new byte[maxFileSize];
                int length = 0;
                while ((length = is.read(buffer)) != -1) {
                    os.write(buffer, 0, length);
                }
            }
            response.flushBuffer();
        } catch (Exception e) {
            YTLogUtils.error("附件下载出现异常",e);
            throw e;
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static ExcelReader checkFile(MultipartFile file) {
        // 效验文件是否存在
        if(file == null){
            throw new YTBizException(YTResponseEnum.File_NOT_EXIT.getCode(),YTResponseEnum.File_NOT_EXIT.getName());
        }
        //效验文件格式是否正确
        String fileName = file.getOriginalFilename();
        if(StringUtils.isNotBlank(fileName) && !fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
            throw new YTBizException(YTResponseEnum.File_FORMART_ERROR.getCode(),YTResponseEnum.File_FORMART_ERROR.getName());
        }
        ExcelReader reader;
        List<Sheet> sheets;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
            sheets = reader.getSheets();
        } catch (IOException e) {
            throw new YTBizException(YTResponseEnum.FAIL.getCode(), "读取EXCEL异常");
        }
        if (CollectionUtils.isEmpty(sheets)) {
            throw new YTBizException(YTResponseEnum.FAIL.getCode(), "工作表为空");
        }
        Sheet sheet = sheets.get(0);
        if (sheet.getLastRowNum() <= 0) {
            throw new YTBizException(YTResponseEnum.FAIL.getCode(), "sheet中数据集为空");
        }
        return reader;
    }
}
