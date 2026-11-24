package com.yutong.safety.core.db.entity.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEntryImportFailDO extends SafetyWebBaseEntity implements Serializable {

    private Integer id;

    private String buttJointCode;

    private String buttJointName;

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

    private Integer nightFlag;

    private String versionNo;

    private String failRemark;

    private String num;
    private String blankType;
    private String competitorDirector;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean saveOrUpdate() {
        return false;
    }
}