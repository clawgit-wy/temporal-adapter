package com.yutong.safety.core.db.entity.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class BpmModelRt implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelId;

    private String formId;

    private String formUrl;

    private String templateId;

}
