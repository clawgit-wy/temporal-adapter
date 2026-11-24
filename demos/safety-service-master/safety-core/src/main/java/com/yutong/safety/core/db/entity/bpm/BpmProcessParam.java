package com.yutong.safety.core.db.entity.bpm;

import lombok.Data;

import java.io.Serializable;

@Data
public class BpmProcessParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String formInstanceId;
    private String modelId;
    private String templateFormId;
    private String processId;
    private String bpmTemplateId;
    private String processParam;
    private String subject;
    private String userName;

}
