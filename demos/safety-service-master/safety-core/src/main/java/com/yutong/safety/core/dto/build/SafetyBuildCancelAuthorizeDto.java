package com.yutong.safety.core.dto.build;

import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 取消门禁的实体类
 */
@Data
public class SafetyBuildCancelAuthorizeDto {
    /**
     * 取消门禁的员工号（入参，必要，判空）
     */
    private String empNo;
    /**
     * 员工类型（入参，必要，判空， 两种类型： WX，ZS）
     */
    private String userType;
    /**
     * 取消的门禁编码 （入参，必要，判空）
     */
    private List<String> deviceCode;
    /**
     * 操作人（入参，必要，不判空）
     */
    private String createBy;

    /**
     * 最新的厂区编码
     * (其中外协的厂区数据业根据此字段处理，因为外协的厂区编码是由一行数据拼接存储的，所以单独处理)
     * 说明：为空表示不做处理，DELETE 表示要做清空处理，有值：表示要做覆盖处理; 具体的执行操作在wxDataUpdate
     */
    private String lastFactoryCode;
    /**
     * 取消员工的厂区编码（方法中使用 -正式工）
     */
    private List<String> deleteFactoryCode = Lists.newArrayList();
    /**
     * 增加的厂区编码 （方法中使用 - 正式工）
     */
    private List<String> saveFactoryCode = Lists.newArrayList();
    /**
     * 是否回传EHR true表示回传
     */
    private boolean sendEhr = false;
}
