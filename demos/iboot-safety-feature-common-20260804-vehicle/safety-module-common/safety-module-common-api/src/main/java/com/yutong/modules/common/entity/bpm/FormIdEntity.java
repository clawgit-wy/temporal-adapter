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
public class FormIdEntity {

    /**
     * 系统标识
     */
    private String sysId;

    /**
     * 模型标识
     */
    private String modelId;

    /**
     * 表单模板标识
     */
    private String templateFormId;

    /**
     * 实例主键
     */
    private String formInstanceId;

    @JsonCreator
    public FormIdEntity(String jsonStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FormIdEntity temp = mapper.readValue(jsonStr, FormIdEntity.class);
        this.sysId = temp.getSysId();
        this.modelId = temp.getModelId();
        this.templateFormId = temp.getTemplateFormId();
        this.formInstanceId = temp.getFormInstanceId();
    }

}
