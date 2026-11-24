package com.yutong.safety.common.param.app;

import com.yutong.safety.common.param.Check;
import lombok.Data;

import java.util.List;

/**
 * @author gwcuiyx
 * @date 2023-10-17 15:12
 */
@Data
public class VisitorHairpinListAddParam implements Check {
    /**
     * 被访人姓名
     */
    private String visitedName;
    /**
     * 被访人手机号
     */
    private String visitedPhone;
    /**
     * 访客单位
     */
    private String vendorUnit;
    /**
     * 访问事由
     */
    private String vendorMatter;
    /**
     * 车牌号
     */
    private String cardNo;
    /**
     * 入厂开始时间
     */
    private String startTime;
    /**
     * 入厂结束时间
     */
    private String endTime;
    /**
     * eip流程id
     */
    private String eipId;

    /**
     * 是否是小程序触发
     */
    private String wxFlag;

    /**
     * 微信登录的电话号码
     */
    private String wxPhone;

    /**
     * 被访人部门
     */
    private String visitedDept;

    /**
     * eip流程创建时间
     */
    private String eipCreateTime;

    /**
     * 访客数据列表
     */
    private List<VisitorParam> visitorList;

    @Override
    public void check() {

    }
}
