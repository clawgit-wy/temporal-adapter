package com.yutong.safety.core.db.entity.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEntryCompanyRegisterHistoryDO extends SafetyWebBaseEntity implements Serializable {
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

    private String remark;

    private Date applyTime;

    private String applyAudit;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean saveOrUpdate() {
        // 历史表不会进行修改每次都是新增，所以此处为true
        return true;
    }
}