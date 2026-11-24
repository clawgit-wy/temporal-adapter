package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 单个人员权限下发结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResult implements Serializable {
    private String staffNo;
    private String staffName;
    private boolean passed;
    private String operationType;
    private String errorMsg;
    private String rawResponse;

    public static StaffResult success(String staffNo, String staffName, String operationType, String rawResponse) {
        return StaffResult.builder()
                .staffNo(staffNo)
                .staffName(staffName)
                .passed(true)
                .operationType(operationType)
                .rawResponse(rawResponse)
                .build();
    }

    public static StaffResult fail(String staffNo, String staffName, String operationType, String errorMsg) {
        return StaffResult.builder()
                .staffNo(staffNo)
                .staffName(staffName)
                .passed(false)
                .operationType(operationType)
                .errorMsg(errorMsg)
                .build();
    }
}
