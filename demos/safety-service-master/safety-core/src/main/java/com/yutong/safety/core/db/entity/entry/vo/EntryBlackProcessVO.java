package com.yutong.safety.core.db.entity.entry.vo;

import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import lombok.Data;

import java.util.List;

@Data
public class EntryBlackProcessVO extends SafetyEntryBlankProcessDO {

    private List<SafetyEntryBlankProcessDetailDO> detailList;

}
