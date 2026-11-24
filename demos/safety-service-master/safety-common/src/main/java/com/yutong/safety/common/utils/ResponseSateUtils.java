package com.yutong.safety.common.utils;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseSateUtils implements Serializable {
        @JSONField(name = "IsSucess")
        private boolean isSucess;
        @JSONField(name = "IsError")
        private boolean isError;
        @JSONField(name = "Code")
        private int code;
        @JSONField(name = "RecordAffected")
        private int recordAffected;
        @JSONField(name = "Describe")
        private Object describe;
}
