package com.yutong.modules.common.entity.eip.user;

import lombok.Data;

import java.util.List;

/**
 * EIP 用户中心-人员信息返回 result
 */
@Data
public class EipUserResult {

    /**
     * 人员信息列表
     */
    private List<EipUser> records;
    private Integer total;
    private Integer size;
    private Integer current;
    private Integer pages;
}
