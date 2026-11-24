package com.yutong.safety.core.db.entity.entry.vo;

import lombok.Data;

/**
 * 备案过期提醒
 */
@Data
public class EntryOverdueTaskVO {
    /**
     * 对接人工号
     */
    private String buttJointCode;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 备案人名称
     */
    private String name;
    /**
     * 备案人手机号
     */
    private String phone;
    /**
     * 所属公司
     */
    private String companyName;
    /**
     * 备案类型
     */
    private String recordType;
    /**
     * 备案编码
     */
    private String recordTypeCode;
    /**
     * 准入厂区
     */
    private String enterFactory;
    /**
     * 准入厂区编码
     */
    private String enterFactoryCode;
    /**
     * 准入时间-开始
     */
    private String enterStartTime;
    /**
     * 准入时间-截止
     */
    private String enterEndTime;
}
