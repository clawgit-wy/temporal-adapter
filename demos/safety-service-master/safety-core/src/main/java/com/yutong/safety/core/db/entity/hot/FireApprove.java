package com.yutong.safety.core.db.entity.hot;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 动火审批对象 fire_approve
 *
 * @author zhangpand
 * @date 2021-06-18
 */
@Data
public class FireApprove {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 来源：掌宇、小程序
     */
    private String source;

    /**
     * 单据号
     */
    private String documentNum;

    /**
     * 审批状态
     */
    private String approveStatus;

    /**
     * 申请人编号，员工号或手机号
     */
    private String applicantNum;

    /**
     * 审批级别
     */
    private String approveLevel;

    /**
     * 厂区
     */
    private String region;

    /**
     * 动火级别
     */
    private String fireLevel;

    /**
     * 是否涉及车辆改造
     */
    private String carChange;

    /**
     * 施工单位
     */
    private String workCompany;

    /**
     * 动火部门
     */
    private String fireDept;

    /**
     * 动火详细位置
     */
    private String fireLocation;

    /**
     * 车工号
     */
    private String carNum;

    /**
     * 动火开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date fireStartTime;

    /**
     * 动火结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date fireEndTime;

    /**
     * 动火形式
     */
    private String fireForm;

    /**
     * 动火形式其他原因
     */
    private String otherReason;

    /**
     * 项目负责人姓名
     */
    private String principalName;

    /**
     * 项目负责人员工号
     */
    private String principalNum;

    /**
     * 共识过的安全防范措施
     */
    private String securityMeasure;

    /**
     * 审批过的动火安全方案照片
     */
    private String approvedPic;

    /**
     * 现场安全措施落实后的照片
     */
    private String fufilPic;

    /**
     * 动火人员信息
     */
    private String fireEmpInfo;

    /**
     * 监护人信息
     */
    private String monitorInfo;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date modifyTime;

    /**
     * 记录状态，0=已删除，1=有效
     */
    private Long status;


    /**
     * 当前所处节点信息
     */
    private String flowNodeMsg;

    /**
     * 动火作业内容
     */
    private String fireDesc;

    /**
     * 消防员审批意见
     */
    private String fireMenView;

    /**
     * 消防员组长审批意见
     */
    private String fireChiefView;

    /**
     * 监控镜头分配编码
     */
    private String lensNums;

    /**
     * 监控镜头分配名称
     */
    private String lensNames;

    /**
     * 具体动火部位照片
     */
    private String hotWorkArea;

    /**
     * 动火点周边大环境照片
     */
    private String hotPointEnvironment;

    /**
     * 现场防火布防护照片
     */
    private String fireproofClothProtection;

    /**
     * 消防器材及水桶照片
     */
    private String fireEquipmentAndBucket;

    /**
     * 其他照片
     */
    private String hotWorkOther;

}
