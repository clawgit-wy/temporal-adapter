package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildProcessDOWithBLOBs extends SafetyBuildProcessDO implements Serializable {

    private byte[] montageDeviceName;

    private byte[] montageBuildName;

    private byte[] montageTime;

    private byte[] montageFactoryName;

    private byte[] montageDept;

    private static final long serialVersionUID = 1L;
}