package com.yutong.modules.common.query;

import lombok.Data;

@Data
public class CrkSnapshotPhotoQuery {

    private Integer pageSize;
    private String orderBy;
    private Boolean orderType = true;
    private String where;
    private String append = "";
    private Integer totalCount = 1;
    /**
     * 是否分页， true分页，false 不分页
     */
    private Boolean pageInfo = true;
}
