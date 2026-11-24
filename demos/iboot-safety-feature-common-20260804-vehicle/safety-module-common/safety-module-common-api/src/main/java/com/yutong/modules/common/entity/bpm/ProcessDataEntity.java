package com.yutong.modules.common.entity.bpm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessDataEntity {

    /**
     * 流程主键
     */
    private String processId;

    /**
     * 节点主键
     */
    private String nodeFactId;

    /**
     * 节点状态
     */
    private Integer docStatus;

    @JsonCreator
    public ProcessDataEntity(String jsonStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ProcessDataEntity temp = mapper.readValue(jsonStr, ProcessDataEntity.class);
        this.processId = temp.getProcessId();
        this.nodeFactId = temp.getNodeFactId();
        this.docStatus = temp.getDocStatus();
    }
}
