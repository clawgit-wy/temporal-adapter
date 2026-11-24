package com.yutong.safety.common.param;

import org.springframework.stereotype.Component;

@Component
public class Page extends Export{
    private Integer pageNum = 1;//当前页
    private Integer pageSize = 10;//每页记录数

    private Integer limit;

    private Integer offset;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getLimit() {
        return getPageSize();
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

}
