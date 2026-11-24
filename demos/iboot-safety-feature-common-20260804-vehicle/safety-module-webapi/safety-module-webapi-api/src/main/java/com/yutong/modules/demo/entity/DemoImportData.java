package com.yutong.modules.demo.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: demo导入示例数据
 * @Author: jeecg-boot
 * @Date:   2026-06-04
 * @Version: V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoImportData {

    /** 行号（解析时记录） */
    @ExcelIgnore
    private Integer rowIndex;

    /** 员工工号 */
//    @ExcelProperty("员工工号")
    @Excel(name="员工工号*",width=15)
    @NotBlank(message = "员工工号不能为空")
    private String empNo;

    /** 姓名 */
//    @ExcelProperty("姓名")
    @Excel(name="姓名",width=15)
    @NotBlank(message = "姓名不能为空")
    private String empName;

    /** 邮箱 */
//    @ExcelProperty("邮箱")
    @Excel(name="邮箱",width=15)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    /** 手机号 */
//    @ExcelProperty("手机号")
    @Excel(name="手机号",width=15)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 部门 */
//    @ExcelProperty("部门")
    @Excel(name="部门",width=15)
    @NotBlank(message = "部门不能为空")
    private String department;

    @Excel(name="异常信息",width=15)
    @ApiModelProperty(value = "异常信息")
    private String errorMsg;
}
