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
public class FunctionIdEntity {

    /**
     * 功能表能
     */
    private String functionId;

    @JsonCreator
    public FunctionIdEntity(String jsonStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FunctionIdEntity temp = mapper.readValue(jsonStr, FunctionIdEntity.class);
        this.functionId = temp.getFunctionId();
    }
}
