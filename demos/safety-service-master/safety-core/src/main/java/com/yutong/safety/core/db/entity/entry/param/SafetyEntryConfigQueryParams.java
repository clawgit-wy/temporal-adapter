package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SafetyEntryConfigQueryParams extends SafetyWebBaseParam {

    @ApiModelProperty("对接人")
    private String buttJoint;

    @ApiModelProperty("备案类型：接收编码值")
    private String recordTypeCode;

    @ApiModelProperty("准入厂区：接收准入厂区编码")
    private String enterFactoryCode;

    @ApiModelProperty("准入厂区：接收准入厂区编码-支持模糊匹配")
    private String factoryCode;

    @ApiModelProperty("准入时间-开始")
    private String enterStartTime;

    @ApiModelProperty("准入日期-开始")
    private String enterStartDay;

    @ApiModelProperty("准入时间-截止")
    private String enterEndTime;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String cardNo;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("所属公司")
    private String companyName;

    @ApiModelProperty("状态 10-驳回 20-审批中 25-超时未审批 30-审批通过")
    private String status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建日期")
    private String createTime;

    @ApiModelProperty("提交人手机号")
    private String submitPhone;

    @ApiModelProperty("全量标记-true表示不排除已删除的")
    private Boolean allFlag;

    private List<Integer> idList;

    private Integer id;
    private List<String> cardNoList;

    private List<String> buttJointCodeList;
    /**
     * 最小的结束时间
     */
    private String minEnterEndTime;

    /**
     * 最大的开始时间
     */
    private String maxEnterStartTime;

    /**
     * 最大的结束时间
     */
    private String maxEnterEndTime;
    /**
     * 排除状态
     */
    private String statusNot;
    /**
     * 下发标记（0 已下发，1 未下发）
     */
    private String downMark;

    private Integer historyId;

    private Integer entryMethod;

    /**
     * 排除状态
     */
    private List<String> statusList;
}
