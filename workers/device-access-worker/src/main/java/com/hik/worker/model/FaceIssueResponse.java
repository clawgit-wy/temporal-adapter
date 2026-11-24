package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 人脸发行/改写响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceIssueResponse implements Serializable {
    private int code;
    private String msg;
    private String serverTime;
    private Object result;
}
