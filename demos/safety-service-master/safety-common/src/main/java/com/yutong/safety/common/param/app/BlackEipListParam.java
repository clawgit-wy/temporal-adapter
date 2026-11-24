package com.yutong.safety.common.param.app;

import lombok.Data;

import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-1-16 15:27
 */
@Data
public class BlackEipListParam {
    /**
     * 离职人员数据列表
     */
    List<BlackDepartAddParam> departList;
}
