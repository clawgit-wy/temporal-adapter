package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.SafetyVisitorDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author gwcuiyx
 * @date 2023-10-23 9:24
 */
@Data
public class VisitorHirpinAppDTO {
    private Integer id;

    private String visitorName;

    private String visitorPhone;

    private String visitorIdCard;

    private String vendorFactory;

    private String vendorRegion;

    private String visitedName;

    private String visitedPhone;

    private String vendorMatter;

    private Date startTime;

    private Date endTime;

    private String vendorUnit;

    private String cardNo;

    private String photo;

    private Integer vendorState;

    private String hairpinNo;

    private String recoveryNo;

    private Date createTime;

    private Date updateTime;

    private Boolean deleteFalg;

    private String eipFlowId;

    private Integer secretState;

    private String secretRegion;

    private Date hairpinTime;

    private Date recoveryTime;

    private String factoryId;

    private String regionId;

    private String wxPhone;

    private String qrCode;

    private Date nowTime;

    private String visitedDept;

    private Date eipCreateTime;

    private Integer userId;

    private String hairpinFactory;
    private String actInFactory;
    private String inFactoryProcess;
    private String empNo;
    private String issueFactory;
    private String unIssueFactory;
    private String authorizedNo;
    private String authorizedName;
    private String authorizedByNo;
    private String authorizedByName;
    /**
     * 身份证照片
     */
    private String idCardPhoto;
    /**
     * 身份证照片是否保存，0： 否，1：是
     */
    private Integer isSaveCardPhoto;

    private List<SafetyVisitorDO> ids;
}
