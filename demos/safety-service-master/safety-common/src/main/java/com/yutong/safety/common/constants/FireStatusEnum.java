/**
 * @copyright: Copyright (c) 2015-2030 yutong.com All Rights Reserved
 * @file: EnvironmentTypeEnum.java project: environ
 * @creator: zhangpand
 * @date: 2020/10/29
 */


package com.yutong.safety.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: zhangpand
 * @requireNo:
 * @createdate: 2020-10-29 19:28
 * @lastdate:
 */
@Getter
@AllArgsConstructor
public enum FireStatusEnum {
    DISCARD("00", "废弃"),
    PASS("30", "审批通过");

    private final String approveStatus;
    private final String name;
}
