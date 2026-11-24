package com.hik.mokapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 人员信息（模拟海康接口请求体）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo implements Serializable {

    /** 人员ID */
    private String personId;

    /** 姓名 */
    private String name;

    /** 人脸图片URL */
    private String faceUrl;
}
