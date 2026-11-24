package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.param.Check;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigHistoryDO;
import lombok.Data;

@Data
public class EntryConfigHistoryAddParam extends SafetyEntryConfigHistoryDO implements Check{

    @Override
    public void check() {

    }
}
