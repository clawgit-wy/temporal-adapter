package com.yutong.modules.risk.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 治安巡查任务实体类
 */
public class YTSafetyPatrolTaskDO implements Serializable {
    private Integer id;
    private Integer planId;
    private String planName;
    private Integer typeId;
    private String typeName;
    private Integer teamId;
    private String teamName;
    private Integer factoryId;
    private String taskBeginDate;
    private String taskEndDate;
    private Integer status;
    private String remark;
    private Date createTime;
    private Integer curOrder;
    private String userCode;
    private static final long serialVersionUID = 1L;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName == null ? null : planName.trim(); }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName == null ? null : typeName.trim(); }
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName == null ? null : teamName.trim(); }
    public Integer getFactoryId() { return factoryId; }
    public void setFactoryId(Integer factoryId) { this.factoryId = factoryId; }
    public String getTaskBeginDate() { return taskBeginDate; }
    public void setTaskBeginDate(String taskBeginDate) { this.taskBeginDate = taskBeginDate == null ? null : taskBeginDate.trim(); }
    public String getTaskEndDate() { return taskEndDate; }
    public void setTaskEndDate(String taskEndDate) { this.taskEndDate = taskEndDate == null ? null : taskEndDate.trim(); }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark == null ? null : remark.trim(); }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Integer getCurOrder() { return curOrder; }
    public void setCurOrder(Integer curOrder) { this.curOrder = curOrder; }
    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode == null ? null : userCode.trim(); }
}
