package com.yutong.safety.common.param.web;


import com.alibaba.excel.annotation.ExcelIgnore;
import com.yutong.safety.common.param.Check;
import org.springframework.util.ObjectUtils;

/**
 * Created by gwcuiyx on 2023.07.10
 */
public class WebPageParam implements Check {
    @ExcelIgnore
    private int pageNum ;
    @ExcelIgnore
    private int pageSize;
    @ExcelIgnore
    private String orderByClause;

    public int getOffset() {
        if(ObjectUtils.isEmpty(pageSize)){
            pageSize = 15;
        }
        if(pageNum == 0 || pageNum == 1) {
            return 0 ;
        }
        return (pageNum-1) * pageSize ;
    }

    public int getLimit() {
        if(ObjectUtils.isEmpty(pageSize) || pageSize == 0){
            pageSize = 15;
        }
        return pageSize ;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByClause() {
        return orderByClause ;
    }

    public void setOrderByClause(String orderByClause){this.orderByClause = orderByClause;}

    public void check() { }

}


