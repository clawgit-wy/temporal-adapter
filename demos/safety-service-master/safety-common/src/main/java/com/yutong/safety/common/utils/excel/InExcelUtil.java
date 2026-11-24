package com.yutong.safety.common.utils.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 任昊翔 on 2017/7/25.
 * excel导入公共类
 */
public class InExcelUtil {

    /*遍历excel文件流，将数据放入list集合当中*/
    public static List<List<String>> getExcelStringList(InputStream in, int cellCount) throws Exception {
        Workbook work = WorkbookFactory.create(in);
        if(work == null) throw new Exception("上传文件为空");
        Row row = null;
        Cell cell = null;
        List<List<String>> list = new ArrayList<List<String>>();
        //int sheetnum = work.getNumberOfSheets();//excel中sheet总数
        Sheet sheet = work.getSheetAt(0);//获取excel中第一个sheet
        if(sheet != null){
            int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
            for(int j=sheet.getFirstRowNum();j<rowCount;j++){
                row = sheet.getRow(j);
                if(row == null || row.getFirstCellNum() == j) continue;
                //int cellCount = row.getPhysicalNumberOfCells(); //是获取不为空的列个数
                //int cellCount = row.getLastCellNum();//获取最后一个不为空的列是第几个
                List<String> li = new ArrayList<String>();
                for(int k=row.getFirstCellNum();k<cellCount;k++){
                    cell = row.getCell(k);
                    if(cell != null){
                        li.add(PubExcelUtil.getCellStringValue(cell).trim());
                    }else {
                        li.add("");
                    }
                }
                list.add(li);
            }
        }else {
            throw new Exception("无法获取excel中Sheet数据");
        }
        in.close();
        work.close();
        return list;
    }

    /*获取标题值 单个sheet*/
    public static List<String> getTitleList(InputStream in) throws Exception {
        Workbook work = WorkbookFactory.create(in);
        if(work == null) throw new Exception("上传文件为空");
        Sheet sheet = work.getSheetAt(0);
        Row row = sheet.getRow(0);
        Cell cell = null;
        List<String> list = new LinkedList<String>();
        int cellCount = row.getPhysicalNumberOfCells(); //获取总列数
        for(int k=row.getFirstCellNum();k<=cellCount;k++){
            cell = row.getCell(k);
            if(cell != null){
                String cellValue = PubExcelUtil.getCellStringValue(cell).trim();
                if(!StringUtils.isBlank(cellValue)){
                    list.add(cellValue);
                }
            }
        }
        in.close();
        work.close();
        return list;
    }
    public static List<String> getTitleList(InputStream in, int titleRow) throws Exception {
        Workbook work = WorkbookFactory.create(in);
        if(work == null) throw new Exception("上传文件为空");
        Sheet sheet = work.getSheetAt(0);
        Row row = sheet.getRow(titleRow-1);
        Cell cell = null;
        List<String> list = new LinkedList<String>();
        int cellCount = row.getPhysicalNumberOfCells(); //获取总列数
        for(int k=row.getFirstCellNum();k<=cellCount;k++){
            cell = row.getCell(k);
            if(cell != null){
                String cellValue = PubExcelUtil.getCellStringValue(cell).trim();
                if(!StringUtils.isBlank(cellValue)){
                    list.add(cellValue);
                }
            }
        }
        in.close();
        work.close();
        return list;
    }

    public static List<List<Object>> getExcelObjectList(InputStream in, int cellCount) throws Exception {
        Workbook work = WorkbookFactory.create(in);
        if(work == null) throw new Exception("上传文件为空");
        Row row = null;
        Cell cell = null;
        List<List<Object>> list = new LinkedList<List<Object>>();
        //int sheetnum = work.getNumberOfSheets();//excel中sheet总数
        Sheet sheet = work.getSheetAt(0);//获取excel中第一个sheet
        if(sheet != null){
            int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
            for(int j=sheet.getFirstRowNum();j<rowCount;j++){
                row = sheet.getRow(j);
                if(row == null || row.getFirstCellNum() == j) continue;
                //int cellCount = row.getPhysicalNumberOfCells(); //是获取不为空的列个数
                //int cellCount = row.getLastCellNum();//获取最后一个不为空的列是第几个
                List<Object> li = new LinkedList<Object>();
                for(int k=row.getFirstCellNum();k<cellCount;k++){
                    cell = row.getCell(k);
                    if(cell != null){
                        li.add(PubExcelUtil.getCellObjectValue(cell));
                    }else {
                        li.add("");
                    }
                }
                list.add(li);
            }
        }else {
            throw new Exception("无法获取excel中Sheet数据");
        }
        in.close();
        work.close();
        return list;
    }

    public static List<List<Object>> getExcelObjectList(InputStream in, int dateRow, int cellCount) throws Exception {
        Workbook work = WorkbookFactory.create(in);
        if(work == null) throw new Exception("上传文件为空");
        Row row = null;
        Cell cell = null;
        List<List<Object>> list = new LinkedList<List<Object>>();
        //int sheetnum = work.getNumberOfSheets();//excel中sheet总数
        Sheet sheet = work.getSheetAt(0);//获取excel中第一个sheet
        if(sheet != null){
            int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
            for(int j=(dateRow-1);j<rowCount;j++){
                row = sheet.getRow(j);
                if(row == null || row.getFirstCellNum() == j) continue;
                //int cellCount = row.getPhysicalNumberOfCells(); //是获取不为空的列个数
                //int cellCount = row.getLastCellNum();//获取最后一个不为空的列是第几个
                List<Object> li = new LinkedList<Object>();
                for(int k=row.getFirstCellNum();k<cellCount;k++){
                    cell = row.getCell(k);
                    if(cell != null){
                        li.add(PubExcelUtil.getCellObjectValue(cell));
                    }else {
                        li.add("");
                    }
                }
                list.add(li);
            }
        }else {
            throw new Exception("无法获取excel中Sheet数据");
        }
        in.close();
        work.close();
        return list;
    }

    /*获取标题值 多个sheet*/
    public static List<String> getTitleListAt(Sheet sheet) throws Exception {
        //Workbook work = WorkbookFactory.create(in);
        //if(work == null) throw new Exception("上传文件为空");
        //Sheet sheet = work.getSheetAt(0);
        Row row = sheet.getRow(0);
        Cell cell = null;
        List<String> list = new LinkedList<String>();
        int cellCount = row.getPhysicalNumberOfCells(); //获取总列数
        for(int k=row.getFirstCellNum();k<=cellCount;k++){
            cell = row.getCell(k);
            if(cell != null){
                String cellValue = PubExcelUtil.getCellStringValue(cell).trim();
                if(!StringUtils.isBlank(cellValue)){
                    list.add(cellValue);
                }
            }
        }
        return list;
    }

    public static List<List<Object>> getExcelObjectListAt(Sheet sheet, int cellCount) throws Exception {
        //int sheetnum = work.getNumberOfSheets();//excel中sheet总数
        Row row = null;
        Cell cell = null;
        List<List<Object>> list = new LinkedList<List<Object>>();
        if(sheet != null){
            int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
            for(int j=sheet.getFirstRowNum();j<rowCount;j++){
                row = sheet.getRow(j);
                if(row == null || row.getFirstCellNum() == j) continue;
                //int cellCount = row.getPhysicalNumberOfCells(); //是获取不为空的列个数
                //int cellCount = row.getLastCellNum();//获取最后一个不为空的列是第几个
                List<Object> li = new LinkedList<Object>();
                for(int k=row.getFirstCellNum();k<cellCount;k++){
                    cell = row.getCell(k);
                    if(cell != null){
                        li.add(PubExcelUtil.getCellObjectValue(cell));
                    }else {
                        li.add("");
                    }
                }
                list.add(li);
            }
        }else {
            throw new Exception("无法获取excel中Sheet数据");
        }
        return list;
    }
}
