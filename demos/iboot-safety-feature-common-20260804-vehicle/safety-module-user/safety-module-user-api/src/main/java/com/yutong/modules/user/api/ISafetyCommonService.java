package com.yutong.modules.user.api;

import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.entity.SafetySysUser;
import com.yutong.modules.user.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 公共接口
 */
public interface ISafetyCommonService {

    /**
     * 用户信息模糊匹配
     * @param userInfo
     * @return
     */
    public List<SafetyEmployeeMessageVO> queryEmployFuzzySearch(String userInfo);

    /**
     * 获取用户信息
     * @param orgCode
     * @return
     */
    public List<SafetyEmployeeMessageVO> queryEmployByOrgCode(String orgCode);

    /**
     * 厂区下拉
     * @return
     */
    public List<SafetyFieldManageVO> queryFactoryDrop();

    /**
     * 厂区下拉(带remark过滤)
     * @return
     */
    public List<SafetyFieldManageVO> queryFactoryFilterDrop();

    /**
     * 区域下拉
     * @return
     */
    public List<SafetyFieldManageVO> queryRegionDrop();

    /**
     * 隐患区域下拉
     * @param parentIds
     * @return
     */
    public List<SafetyFieldManageVO> queryRiskRegion(String parentIds);

    /**
     * 所有隐患区域下拉
     * @param parentIds
     * @return
     */
    public List<SafetyFieldManageVO> queryAllRiskRegion();

    /**
     * 位置下拉
     * @param
     * @return
     */
    public List<SafetyLocationInfoVO> queryLocationInfo(String factoryId,String areaId,String orgId,String orgCode);

    /**
     * 角色下拉
     * @param userTypes
     * @return
     */
    public List<SafetySysRoleOptionListVO> optionlist(Integer[] userTypes);

    /**
     * 部门组织树
     * @return
     */
    public List<SafetyGroupInfoTreeVO> queryGroupInfoTree();

    /**
     * 刷新部门组织缓存
     * @return
     */
    void refreshGroupInfoCache();

    /**
     * 岗位
     * @return
     */
    List<JobOptionListVO> jobOptionList();

    /**
     * 部门组织树
     * @return
     */
    public Map<String, Long> queryGroupInfoMap();

    public Map<String, SafetyGroupInfoTreeVO> queryGroupInfoFactoryMap();

    /**
     * 获取安全员信息
     * @param code
     * @return
     */
    List<SafetyOfficerVO> queryGroupInfoSafetyOfficer(String code);

    /**
     * 获取安全责任人中的匹配角色人员信息
     * @param orgCode
     * @param roleCodes
     * @return
     */
    List<SafetyOfficerVO> queryGroupInfoRoles(String orgCode,String roleCodes);

    /**
     * 校验是否为保卫人员
     * @param positionCode
     * @return
     */
    Long checkDefend(String positionCode);

    /**
     * 根据 组织编码获取安全员
     * @param orgCode
     * @return
     */
    List<YtsafetySysResponsibleVO> queryResponsibleByOrgCode(String orgCode,List<String> roleCodeList);

    /**
     * 根据 厂区和角色获取对应安全责任人
     * @param plantId
     * @param roleCodeList
     * @return
     */
    List<YtsafetySysResponsibleVO> queryResponsibleByFactory(Long plantId, List<String> roleCodeList);

    /**
     * 根据 厂区和角色获取对应安全责任人
     * @param plantId
     * @param roleCodeList
     * @return
     */
    List<YtsafetySysResponsibleVO> queryResponsibleByFactoryAndDepart(String orgCode,Long plantId, List<String> roleCodeList);

    /**
     * 校验员工是否为安全责任人
     */
    Boolean checkIsResponsible(String empNo,List<String> roleCodeList);

    /**
     * 获取用户授权厂区ID列表（安全责任人+指定角色过滤）
     * @param empNo
     * @return
     */
    List<Long> getAuthorizedFactoryIds(String empNo);


    List<SafetySysUser> getAproveUser(String applyNo, String code);

    /**
     * 获取厂区下的指定角色user
     * @param factoryCode
     * @param roles
     * @return
     */
    List<SafetySysUser> selectFactoryRole(String factoryCode, List<String> roles);

    List<YtsafetySysResponsibleVO> queryResponsibleByOrgCodeHierarchy(String code, List<String> roleCodeList);

    SafetyEmployeeMessage querySafetyEmployeeMessageByEmpLogin(String empLogin);

    /**
     * 根据组织ID列表查询组织信息，返回ID→名称映射，底层走缓存性能高
     * @param orgIds 组织ID列表
     * @return id→name 映射
     */
    Map<Long, String> queryGroupInfoByIds(List<Long> orgIds);

    String getOrgPathName(Long orgId);
}
