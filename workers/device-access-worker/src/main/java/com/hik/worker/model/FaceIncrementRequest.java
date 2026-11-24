package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 人脸增量权限请求（接口5: FaceModifyBypowerType）
 * powerType: 0=增加设备权限, 1=删除设备权限
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceIncrementRequest implements Serializable {
    private int OperType;
    private StaffInfo Staff;
    private String BeginTime;
    private String EndTime;
    private List<Integer> AuthorizeDeviceNo;
    private int powerType;
    private int StaffState;
    private String FacePicture;
}
