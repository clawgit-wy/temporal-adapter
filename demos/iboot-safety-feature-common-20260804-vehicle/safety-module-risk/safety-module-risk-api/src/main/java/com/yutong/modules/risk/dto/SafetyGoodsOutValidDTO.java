package com.yutong.modules.risk.dto;

import com.yutong.modules.risk.web.WebPageParam;
import lombok.Data;

@Data
public class SafetyGoodsOutValidDTO extends WebPageParam {
    private String businessType;//验证类型
    private String subject;//主题
    private String createTime;//时间
    private String location;//位置
    private String carNumber;//车牌号
    private String gooutEmpName;//出门人
    private String gooutPhone;//联系电话
    private String gooutEmpDept;//单位
    private String outFactoryName;//出厂厂区
    private String outFactoryId;//出厂厂区
    private String targetFactory;//入厂厂区
    private String targetFactoryId;//入厂厂区
    private String targetFactoryDoor;//入厂厂区大门
    private String goodsType;//物资类别
    private String reasonType;//原因类别
    private String reasonDesc;//原因说明
    private String factory;//厂区
    private String factoryId;//厂区
    private String goodsDetail;//物资明细
    private String validAccess;//是否通过验证
    private String exceptionType;//异常类型
    private String remark;//备注
    private String weightListWeight;//磅单重量
    private String recheckWeight;//复磅重量
    private String emissionStandard;//排放标准
    private String blacklist;//是否加入黑名单（0否1是）
    private String checkPerson;//抽查人姓名
    private String checkPersonNo;//抽查人员工号
    private String checkTimeBegin;//抽查人时间开始
    private String checkTimeEnd;//抽查人时间结束
    private Integer id;//业务验证id
    private String goodsOutId;//出门证id
    private String goodsOutBpmUrlPre;//出门证流程链接前缀
    private String goodsOutFlowUrl;//出门证流程链接

    private String outFactoryIds;

    private String curPersonLogin;
}
