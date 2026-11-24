package com.yutong.safety.core.dto.goodsOut;

import com.alibaba.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author caomg
 * @date 2024-9-24 9:34
 * @description GoodsPage
 */
@ApiModel
public class GoodsPage {
    @ApiModelProperty("当前页（从1开始）")
    @ExcelIgnore
    private Integer pageNum;//当前页（从1开始）

    @ApiModelProperty("每页记录数")
    @ExcelIgnore
    private Integer pageSize;//每页记录数

    @ExcelIgnore
    private Integer limit;

    @ExcelIgnore
    private Integer offset;

    public Integer getPageNum() {
        if(pageNum == null || pageNum <= 0){
            pageNum = 1;
        }
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        if(pageSize == null || pageSize <= 0){
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getLimit() {
        return getPageSize();
    }

    public Integer getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }
}
