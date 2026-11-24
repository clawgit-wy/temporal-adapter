package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.param.Check;
import com.yutong.safety.core.db.entity.entry.SafetyEntryImportFailDO;
import lombok.Data;

@Data
public class SafetyEntryImportFailAddParams extends SafetyEntryImportFailDO implements Check {
    @Override
    public void check() {

    }
}
