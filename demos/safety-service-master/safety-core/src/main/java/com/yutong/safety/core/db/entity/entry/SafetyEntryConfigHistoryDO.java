package com.yutong.safety.core.db.entity.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEntryConfigHistoryDO extends SafetyWebBaseEntity implements Serializable {

    private Integer id;

    private String buttJointCode;

    private String buttJointName;

    private String buttJointPhone;

    private String recordType;

    private String recordTypeCode;

    private String companyName;

    private String companyCode;

    private String name;

    private String cardNo;

    private String phone;

    private String enterFactory;

    private String enterFactoryCode;

    private String enterStartTime;

    private String enterEndTime;

    private String photoUrl;

    private String submitPhone;

    private String qrCode;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String remark;

    private Integer nightFlag;

    private String status;

    private Date applyTime;

    private String applyAudit;

    private Integer unEnterNum;

    private Integer delFlag;

    private String createByNo;

    private Integer targetId;

    private Integer blankFlag;

    private Integer entryMethod;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean saveOrUpdate() {
        return this.id == null;
    }
}