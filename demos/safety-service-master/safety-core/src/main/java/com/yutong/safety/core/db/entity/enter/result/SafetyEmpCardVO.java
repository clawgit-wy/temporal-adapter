package com.yutong.safety.core.db.entity.enter.result;

import com.yutong.safety.core.db.entity.enter.SafetyEmpCard;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class SafetyEmpCardVO extends SafetyEmpCard {

    private String orgId;
    private String curRid;
}
