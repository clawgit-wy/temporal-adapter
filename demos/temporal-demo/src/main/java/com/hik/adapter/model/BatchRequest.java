package com.hik.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 批量校验请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequest implements Serializable {

    /** 批次ID */
    private String batchId;

    /** 待校验的人员列表 */
    private List<PersonInfo> persons;
}
