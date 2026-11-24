package com.yutong.modules.risk.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.domain.BaseDTO;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyLocationInfoDTO对象", description="位置信息表DTO")
public class YtsafetyLocationInfoDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.Long id;
    /**申请员工号*/
    @ApiModelProperty(value = "申请员工号")
    private java.lang.String applyNo;
    /**原因说明*/
    @ApiModelProperty(value = "原因说明")
    private java.lang.String applyReason;
    /**申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送*/
    @ApiModelProperty(value = "申请类型 0 新增 1 变更新增 2 批量新增 3变更修改 4批量推送")
    private java.lang.Integer changeType;
    /**操作类型 0 保存草稿 1 提交*/
    @ApiModelProperty(value = "操作类型 0 保存草稿 1 提交")
    private java.lang.Integer opType;
    /**详情明细*/
    @ApiModelProperty(value = "详情明细")
    private List<YtsafetyLocationInfoProcessDetailsDTO> details;
}
