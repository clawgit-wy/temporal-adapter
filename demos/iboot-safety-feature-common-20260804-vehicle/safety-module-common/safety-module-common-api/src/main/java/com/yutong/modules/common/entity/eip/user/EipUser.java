package com.yutong.modules.common.entity.eip.user;

import lombok.Data;

import java.util.List;

/**
 * EIP 用户中心-人员信息
 */
@Data
public class EipUser {

    /**主键id*/
    private String id;
    /**工号*/
    private String workNo;
    /**原工号*/
    private String oldWorkNo;
    /**姓名*/
    private String name;
    /**性别*/
    private String gender;
    /**邮箱*/
    private String email;
    /**办公电话*/
    private String officePhone;
    /**办公短号*/
    private String shortOfficePhone;
    /**手机号*/
    private String phone;
    /**备用电话*/
    private String backupPhone;
    /**状态*/
    private String status;
    /**状态明细*/
    private String statusDetail;
    /**组织编码*/
    private String orgCode;
    /**组织名称*/
    private String orgName;
    /**组织编码路径*/
    private String orgCodePath;
    /**组织名称路径*/
    private String orgNamePath;
    /**上级组织编码*/
    private String parentCode;
    /**照片*/
    private String photo;
    /**生活照*/
    private String lifePhoto;
    /**生活照类型*/
    private String lifePhotoType;
    /**证件类型*/
    private String certificateType;
    /**身份证号*/
    private String idCard;
    /**出生日期*/
    private String birthDate;
    /**职位编码*/
    private String positionCode;
    /**职位名称*/
    private String positionName;
    /**职位类型*/
    private String positionType;
    /**职务（工种）名称*/
    private String jobTypeName;
    /**用户类型*/
    private String userType;
    /**人员范围*/
    private String personnelScope;
    /**供应商编码*/
    private String supplierCode;
    /**办公地址*/
    private String officeAddress;
    /**政治面貌*/
    private String politicsStatus;
    /**员工类型*/
    private String empType;
    /**员工类型编码*/
    private String empTypeCode;
    /**公司入职（司龄起算）日期*/
    private String compBeginDate;
    /**成本中心编码*/
    private String kostl;
    /**成本中心名称*/
    private String kostlName;
    /**兼职列表*/
    private List<Object> partTimeJobList;
    /**工资公司编码*/
    private String salaryCompanyCode;
    /**工资公司名称*/
    private String salaryCompanyName;
    /**体系编码*/
    private String txCode;
    /**体系名称*/
    private String txName;
    /**板块编码*/
    private String plateCode;
    /**板块名称*/
    private String plateName;
    /**公司编码*/
    private String companyCode;
    /**公司名称*/
    private String companyName;
    /**事业部编码*/
    private String divisionCode;
    /**事业部名称*/
    private String divisionName;
    /**部门（处）编码*/
    private String deptCode;
    /**部门（处）名称*/
    private String deptName;
    /**科室编码*/
    private String officeCode;
    /**科室名称*/
    private String officeName;
    /**室编码*/
    private String roomCode;
    /**室名称*/
    private String roomName;
    /**单元（科）编码*/
    private String unitCode;
    /**单元（科）名称*/
    private String unitName;
    /**段（模块）编码*/
    private String paragraphCode;
    /**段（模块）名称*/
    private String paragraphName;
    /**班组编码*/
    private String teamCode;
    /**员工组*/
    private String empGroup;
    /**员工子组*/
    private String empSubgroup;
    /**员工子组名称*/
    private String empSubgroupName;
    /**国家*/
    private String country;
    /**籍贯省*/
    private String nativePlaceProvince;
    /**籍贯市*/
    private String nativePlaceCity;
    /**EIP标志*/
    private String eipSign;
    /**EIP标志日期*/
    private String eipSignDate;
    /**自定义部门编码*/
    private String customDeptCode;
    /**自定义部门名称*/
    private String customDeptName;
    /**自定义体系编码*/
    private String customTxCode;
    /**自定义体系名称*/
    private String customTxName;
    /**合同主体*/
    private String contractSubject;
    /**合同主体名称*/
    private String contractSubjectName;
    /**参加工作日期*/
    private String joinWorkingDate;
    /**离职日期*/
    private String departDate;
    /**更新时间*/
    private String updateTime;
    /**EIP办公地址*/
    private String eipOfficeAddress;
    /**华为国籍*/
    private String hwNationality;
    /**华为国籍含义*/
    private String hwNationalityMeaning;
    /**华为国家含义*/
    private String hwCountryMeaning;
    /**华为员工类型*/
    private String hwEmpolyeeType;
    /**华为员工类型名称*/
    private String hwEmpolyeeTypeName;
    /**华为民族*/
    private String hwNationCom;
    /**华为民族名称*/
    private String hwNationComName;
    /**华为业务区域*/
    private String hwBusinessRegion;
    /**华为业务区域名称*/
    private String hwBusinessRegionName;
}
