package com.yutong.safety.common.vo;

import java.util.List;

/**
 * Created by gwcuiyx on 23-07-10.
 */
public class PageVO<T> {
    private long total ;
    private List<T> dataList ;

    public PageVO(){}

    public PageVO(long total, List<T> dataList) {
        this.dataList = dataList ;
        this.total = total ;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
