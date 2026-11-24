package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 人脸发行/改写请求（接口2: FaceIssue, 接口3: FaceModify）
 * OperType: 0=发行, 1=改写
 * StaffState: 0=不变, 1=新增, 2=修改
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceIssueRequest implements Serializable {
    private int OperType;
    private StaffInfo Staff;
    private String BeginTime;
    private String EndTime;
    private List<Integer> AuthorizeDeviceNo;
    private List<String> ProjectGids;
    private int StaffState;
    private String FacePicture;
}
