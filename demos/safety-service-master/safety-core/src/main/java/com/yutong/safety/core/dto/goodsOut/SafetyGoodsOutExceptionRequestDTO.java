package com.yutong.safety.core.dto.goodsOut;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SafetyGoodsOutExceptionRequestDTO  extends WebPageParam  {
    @ApiModelProperty(value = "申请人", example = "张三")
    private String applicant;

    @ApiModelProperty(value = "出门人", example = "李四")
    private String personLeaving;

    @ApiModelProperty(value = "审核人", example = "王五")
    private String reviewer;

    @ApiModelProperty(value = "状态", example = "已通过")
    private String status;

    @ApiModelProperty(value = "出门类型", example = "生产出门")
    private String outboundType;

    @ApiModelProperty(value = "出厂厂区", example = "十八里河厂区")
    private String factoryLocation;

    @ApiModelProperty(value = "提交时间启")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date submissionStartTime;

    @ApiModelProperty(value = "提交时间止")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date submissionEndTime;

    @ExcelIgnore
    private String bpmUrlPre;//流程链接前缀

    @ApiModelProperty(value = "出厂厂区-权限限制")
    private String outFactoryIds;

    @ApiModelProperty(value = "当前登录人")
    private String curPersonLogin;
}
