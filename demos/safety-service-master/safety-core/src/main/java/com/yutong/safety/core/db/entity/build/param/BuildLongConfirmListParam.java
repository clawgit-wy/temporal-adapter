package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildLongConfirmListParam extends WebPageParam {

    @ApiModelProperty("门禁进出时间-起始")
    private String operTimeBegin;

    @ApiModelProperty("门禁进出时间-截止")
    private String operTimeEnd;

    @ApiModelProperty("员工号")
    private String employeeCode;

    @ApiModelProperty("姓名")
    private String employeeName;

    @ApiModelProperty("门禁名称")
    private String deviceName;

    @ApiModelProperty("门禁编码")
    private String deviceCode;

    @ApiModelProperty("是否本部门 是/否")
    private String corresFlag;

    @ApiModelProperty("生效时间-开始")
    private String beginTime;

    @ApiModelProperty("生效时间-截止")
    private String endTime;

    private List<Integer> idList;

    @ApiModelProperty("确认状态 0-待确认 1-加入白名单 2-关闭 3-进出时间更新删除")
    private String confirmFlag;

    @ApiModelProperty("责任人姓名")
    private String dutyPerson;

    @ApiModelProperty("是否白名单 是/否")
    private String isWhite;

    @ApiModelProperty("未进出时长（天） 最小天")
    private Integer minDay;

    @ApiModelProperty("未进出时长（天） 最大天")
    private Integer maxDay;

    @ApiModelProperty("未进出时长（天） 相等")
    private List<Integer> diffDayList;

    @ApiModelProperty("后端参数-自定义参数")
    private String diyQueryStr;

    @ApiModelProperty("后端参数-分页标记")
    private Boolean pageFlag;

    @ApiModelProperty("后端参数-责任部门过滤")
    private String filterDutyDept;

    @ApiModelProperty("后端参数-当前登录员工号")
    private String curEmpNo;

    @ApiModelProperty("组织变更时间-止")
    private String changeTimeEnd;

    @ApiModelProperty("门禁编号集合")
    private List<String> deviceCodeList;

    private List<String> employeeCodeList;

    private String empCrateTimeBegin;

    @ApiModelProperty("后端参数-是否过滤有门禁权限")
    private Boolean isCheckBuild;
    /**
     * 小于修改时间的字段
     */
    private String ltUpdateTime;

    private String newDeptCode;

    private String ltEipTodoDate;
    private String gtEipTodoDate;
    private Boolean eipTodoDateNotNull;
    private Boolean eipTodoDateNull;

    @ApiModelProperty("后端参数-责任人过滤")
    private String filterDutyEmpCode;

    private String notConfirmFlag;
}
