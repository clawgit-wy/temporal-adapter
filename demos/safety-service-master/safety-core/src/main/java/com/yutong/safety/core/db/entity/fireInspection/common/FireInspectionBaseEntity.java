package com.yutong.safety.core.db.entity.fireInspection.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.safety.common.utils.TokenUtils;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 消防巡检模块公共的实体类信息
 */
@Data
public class FireInspectionBaseEntity {
    /**
     * 删除
     */
    public static final long DELETE = 1L;
    /**
     * 正常
     */
    public static final long NORMAL = 0L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记, 0-正常, 1-删除
     */
    private Long delFlag;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建人工号
     */
    private String createByNo;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改人工号
     */
    private String updateByNo;

    public void setCreateInfo(String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        this.createTime = new Date();
        this.createBy = tokenUser.get("realName");
        this.createByNo = tokenUser.get("workNo");
    }

    public void setUpdateInfo(String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        this.updateTime = new Date();
        this.updateBy = tokenUser.get("realName");
        this.updateByNo = tokenUser.get("workNo");
    }

    public void setCreates(String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        Date date = new Date();
        String realName = tokenUser.get("realName");
        String workNo = tokenUser.get("workNo");
        this.createTime = date;
        this.createBy = realName;
        this.createByNo = workNo;
        this.updateTime = date;
        this.updateBy = realName;
        this.updateByNo = workNo;
        this.delFlag = NORMAL;
    }

    /**
     * APP 接口导入固定人员
     */
    public void setCreates() {
        Date date = new Date();
        this.createTime = date;
        this.createBy = "司永奇";
        this.createByNo = "90062673";
        this.updateTime = date;
        this.updateBy = "司永奇";
        this.updateByNo = "90062673";
        this.delFlag = NORMAL;
    }
}
