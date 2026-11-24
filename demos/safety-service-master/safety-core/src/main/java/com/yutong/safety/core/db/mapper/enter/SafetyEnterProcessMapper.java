package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEnterProcess;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterProcessRt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SafetyEnterProcessMapper {

    SafetyEnterProcessRt selectEnterProcessByInstanceId(@Param("formInstanceId") String formInstanceId);
    Integer insertEnterProcess(SafetyEnterProcess process);
    Integer insertEnterProcessBatch(List<SafetyEnterProcess> processList);
}
