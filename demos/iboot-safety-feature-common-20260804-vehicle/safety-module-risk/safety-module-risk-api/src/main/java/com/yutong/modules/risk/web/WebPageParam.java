package com.yutong.modules.risk.web;


import com.alibaba.excel.annotation.ExcelIgnore;
import com.yutong.modules.risk.common.YTCheck;
import org.springframework.util.ObjectUtils;

/**
 * Created by gwcuiyx on 2023.07.10
 */
public class WebPageParam implements YTCheck {
    @ExcelIgnore
    private Integer pageNo = 1;
    @ExcelIgnore
    private Integer pageSize = 10;
    @ExcelIgnore
    private String orderByClause;

    public int getOffset() {
        if(ObjectUtils.isEmpty(pageSize)){
            pageSize = 15;
        }
        if(pageNo == 0 || pageNo == 1) {
            return 0 ;
        }
        return (pageNo -1) * pageSize ;
    }

    public int getLimit() {
        if(ObjectUtils.isEmpty(pageSize) || pageSize == 0){
            pageSize = 15;
        }
        return pageSize ;
    }

    public int getPageNo() {
        return pageNo == null ? 1 : pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize == null ? 15 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByClause() {
        return orderByClause ;
    }

    public void setOrderByClause(String orderByClause){this.orderByClause = orderByClause;}

    public void check() { }

}


