package com.yutong.safety.common.utils.excel;

import com.yutong.safety.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelCheck {

    public static void check(List<ErrorInfo> errorlist, String sheetName, String title, Integer row, Object obj,
                             SimpleDateFormat sdf, Integer cate){
        if(cate == 1){
            if(obj instanceof String){
                if(StringUtils.isBlank(String.valueOf(obj))){
                    errorlist.add(addError(sheetName,row+2, title,"字段值不能为空"));
                }
            }else {
                errorlist.add(addError(sheetName,row+2, title,"字段值须是字符串类型"));
            }
        }else if(cate == 2){
            if(obj instanceof Date){
                String time = sdf.format(obj);
                if(!DateUtils.isDate(time)){
                    errorlist.add(addError(sheetName,row+2, title, "字段必须是日期类型"));
                }
            }else if(obj instanceof String){
                if(!DateUtils.isDate(String.valueOf(obj))){
                    errorlist.add(addError(sheetName,row+2, title, "字段必须是日期类型"));
                }
            }
        }

    }

    /*创建错误对象*/
    public static ErrorInfo addError(String sheet, Integer row, String columnName, String info){
        //定义错误对象
        ErrorInfo error = new ErrorInfo();
        error.setSheetName(sheet);
        error.setRowNum(row);
        error.setColumnName(columnName);
        error.setErrorInfo(info);
        return error;
    }

    public static ErrorInfo addError(Integer row, String columnName, String info){
        //定义错误对象
        ErrorInfo error = new ErrorInfo();
        error.setRowNum(row);
        error.setColumnName(columnName);
        error.setErrorInfo(info);
        return error;
    }
}
