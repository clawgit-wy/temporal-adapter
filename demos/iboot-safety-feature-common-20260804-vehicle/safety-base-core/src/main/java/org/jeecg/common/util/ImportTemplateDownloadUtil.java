package org.jeecg.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ImportTemplateDownloadUtil {

    /**
     * 生成导入模板并写入响应流
     *
     * @param fileName     文件名（不含后缀）
     * @param title        标题
     * @param headers      表头数组
     * @param instructions 说明文字（支持换行符\n）
     * @param examples     示例数据数组，长度需与 headers 一致
     */
    public static void buildImportTemplate(String fileName, String title,
                                           String[] headers, String instructions,
                                           List<String> examples, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

        XSSFWorkbook workbook = new XSSFWorkbook();

        // 说明文字样式（红色字体、自动换行）
        CellStyle noteStyle = workbook.createCellStyle();
        Font noteFont = workbook.createFont();
        noteFont.setColor(IndexedColors.RED.getIndex());
        noteStyle.setFont(noteFont);
        noteStyle.setWrapText(true);

        // 表头样式（加粗、居中、自动换行）
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 示例数据样式（自动换行）
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setWrapText(true);

        Sheet sheet = workbook.createSheet(fileName);

        // 设置列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 30 * 256);
        }

        // 第1行：标题（合并整行居中）
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(25);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellStyle(headerStyle);
            if (i == 0) {
                cell.setCellValue(title);
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        // 第2行：说明文字（合并整行）
        Row noteRow = sheet.createRow(1);
        noteRow.setHeightInPoints(60);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = noteRow.createCell(i);
            cell.setCellStyle(noteStyle);
            if (i == 0) {
                cell.setCellValue(instructions);
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));

        // 第3行：表头
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(22);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 第4行：示例数据
        Row dataRow = sheet.createRow(3);
        for (int i = 0; i < examples.size(); i++) {
            Cell cell = dataRow.createCell(i);
            cell.setCellValue(examples.get(i));
            cell.setCellStyle(dataStyle);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 基于 VO 类的 @Excel 注解生成导入模板
     *
     * @param clazz        带有 @Excel 注解的 VO 类
     * @param fileName     文件名（不含后缀）
     * @param title        标题
     * @param instructions 说明文字（支持换行符\n）
     * @param examples     示例数据数组
     */
    public static void buildImportTemplate(Class<?> clazz, String fileName, String title,
                                           String instructions, List<String> examples,
                                           HttpServletResponse response) throws IOException {
        String[] headers = extractHeaders(clazz);
        buildImportTemplate(fileName, title, headers, instructions, examples, response);
    }

    /**
     * 从类的 @Excel 注解中提取表头名称（按字段声明顺序）
     */
    public static String[] extractHeaders(Class<?> clazz) {
        List<String> headers = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel != null) {
                headers.add(excel.name());
            }
        }
        return headers.toArray(new String[0]);
    }
}