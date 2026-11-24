package com.yutong.modules.common.entity.bpm;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessApprover {

    /**
     * eip账号名
     */
    @JsonAlias(value = "LoginName")
    private String loginName;

    /**
     * 员工名
     */
    private String name;

    /**
     * 主键
     */
    private String id;

    /**
     * 是否审批人
     */
    private Boolean isApprover;
}
