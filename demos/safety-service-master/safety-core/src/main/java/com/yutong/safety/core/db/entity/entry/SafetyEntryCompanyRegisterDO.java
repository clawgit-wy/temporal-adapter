package com.yutong.safety.core.db.entity.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEntryCompanyRegisterDO extends SafetyWebBaseEntity implements Serializable {

    private Integer id;

    private String fdName;

    private String fdPhone;

    private String fdCompany;

    private String fdCompanyCode;

    private String buttJointCode;

    private String buttJointName;

    private String buttJointPhone;

    private String reason;

    private String status;

    private Date applyTime;

    private String applyAudit;

    private static final long serialVersionUID = 1L;

    public void setCreates(String name) {
        this.createTime = new Date();
        this.createBy = name;
    }

    @Override
    public boolean saveOrUpdate() {
        return id == null;
    }

    public void setUpdates(String name) {
        this.updateTime = new Date();
        this.updateBy = name;
    }
    public void setCreatesAndUpdates(String name) {
        Date date = new Date();
        this.createTime = date;
        this.createBy = name;
        this.updateTime = date;
        this.updateBy = name;
    }
}