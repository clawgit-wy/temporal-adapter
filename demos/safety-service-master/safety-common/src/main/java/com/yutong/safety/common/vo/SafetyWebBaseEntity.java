package com.yutong.safety.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.safety.common.utils.TokenUtils;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public abstract class SafetyWebBaseEntity {
    /**
     * 删除
     */
    public static final int DELETE = 1;
    /**
     * 正常
     */
    public static final int NORMAL = 0;

//    /**
//     * 主键
//     */
//    public Long id;

    /**
     * 备注
     */
    public String remark;

    /**
     * 删除标记, 0-正常, 1-删除
     */
    public Integer delFlag;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date createTime;

    /**
     * 创建人
     */
    public String createBy;

    /**
     * 创建人工号
     */
    public String createByNo;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date updateTime;

    /**
     * 修改人
     */
    public String updateBy;

    /**
     * 修改人工号
     */
    public String updateByNo;

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

    public void setCreates(String empNo, String empName) {
        Date date = new Date();
        this.createTime = date;
        this.createBy = empName;
        this.createByNo = empNo;
        this.updateTime = date;
        this.updateBy = empName;
        this.updateByNo = empNo;
        this.delFlag = NORMAL;
    }


    /**
     * 判断新增还是修改 一般用ID来判断， ID存在是新增，不存在是修改， 因为ID的类型可能不一致，所以子类重写方法来进行判断
     * @return true 新增， false修改
     */
    public abstract boolean saveOrUpdate();

}