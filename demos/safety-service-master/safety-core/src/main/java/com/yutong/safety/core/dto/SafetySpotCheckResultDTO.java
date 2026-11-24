package com.yutong.safety.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetySpotCheckResultDTO implements Serializable {

    private String id;
    private String recordId;
    private Date planTime;
    private Date finishTime;
    private String reformFinish;
    private String reformImg;
    private String reformName;
    private Date reformTime;
    private String reformDelay;
    private Date delayTime;
    private String reformLeader;
    private Date reformLeaderTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getReformFinish() {
        return reformFinish;
    }

    public void setReformFinish(String reformFinish) {
        this.reformFinish = reformFinish;
    }

    public String getReformImg() {
        return reformImg;
    }

    public void setReformImg(String reformImg) {
        this.reformImg = reformImg;
    }

    public String getReformName() {
        return reformName;
    }

    public void setReformName(String reformName) {
        this.reformName = reformName;
    }

    public Date getReformTime() {
        return reformTime;
    }

    public void setReformTime(Date reformTime) {
        this.reformTime = reformTime;
    }

    public String getReformDelay() {
        return reformDelay;
    }

    public void setReformDelay(String reformDelay) {
        this.reformDelay = reformDelay;
    }

    public Date getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Date delayTime) {
        this.delayTime = delayTime;
    }

    public String getReformLeader() {
        return reformLeader;
    }

    public void setReformLeader(String reformLeader) {
        this.reformLeader = reformLeader;
    }

    public Date getReformLeaderTime() {
        return reformLeaderTime;
    }

    public void setReformLeaderTime(Date reformLeaderTime) {
        this.reformLeaderTime = reformLeaderTime;
    }
}