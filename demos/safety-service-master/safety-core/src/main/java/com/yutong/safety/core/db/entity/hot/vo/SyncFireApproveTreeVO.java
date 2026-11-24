package com.yutong.safety.core.db.entity.hot.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SyncFireApproveTreeVO {

    private String title;

    private String key;

    private List<SyncFireApproveTreeVO> children;

    private boolean checkable;

    private Long id;

    private String workCompany;

    private String region;

    private String fireLevel;

    private String principalName;

    private String principalNum;

    private Date fireStartTime;

    private Date fireEndTime;

    private Integer lensId;

    private String lensNum;

    private String lensName;

    public SyncFireApproveTreeVO() {
        super();
    }
    public SyncFireApproveTreeVO(SyncFireApproveListVO syncFireApproveListVO) {
        super();
        this.title = syncFireApproveListVO.getLensName();
        this.key = (syncFireApproveListVO.getLensNum()+"-"+syncFireApproveListVO.getId()+"-"+syncFireApproveListVO.getLensId());
        this.id = syncFireApproveListVO.getId();
        this.workCompany = syncFireApproveListVO.getWorkCompany();
        this.region = syncFireApproveListVO.getRegion();
        this.fireLevel = syncFireApproveListVO.getFireLevel();
        this.principalName = syncFireApproveListVO.getPrincipalName();
        this.principalNum = syncFireApproveListVO.getPrincipalNum();
        this.fireStartTime = syncFireApproveListVO.getFireStartTime();
        this.fireEndTime = syncFireApproveListVO.getFireEndTime();
        this.lensId = syncFireApproveListVO.getLensId();
        this.lensNum = syncFireApproveListVO.getLensNum();
        this.lensName = syncFireApproveListVO.getLensName();
        this.checkable = true;
    }

}
