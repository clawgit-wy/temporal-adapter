package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResultDTO implements Serializable {
    public List<SafetyEmployeeMessageDO> belongList;
    public List<SafetyEmployeeMessageDO> notBelongList;
    public boolean accessEight;
}
