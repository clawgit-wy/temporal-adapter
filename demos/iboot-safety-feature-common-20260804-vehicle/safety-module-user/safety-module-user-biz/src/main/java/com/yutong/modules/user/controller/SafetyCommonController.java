package com.yutong.modules.user.controller;

import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Slf4j
@Api(tags="公共接口")
@RestController
@RequestMapping("/common")
public class SafetyCommonController  {

    @Autowired
    private ISafetyCommonService commonService;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    /**
     * 员工号和姓名模糊查询
     * @param userInfo
     * @return
     */
    @ApiOperation(value="公共接口-员工号和姓名模糊查询", notes="公共接口-员工号和姓名模糊查询")
    @GetMapping(value = "/queryEmpAccount")
    public Result<?> queryEmpAccount(@RequestParam(required=true) String userInfo){
        List<SafetyEmployeeMessageVO> pageList = commonService.queryEmployFuzzySearch(userInfo);
        return Result.OK(pageList);
    }

    @ApiOperation(value="公共接口-员工号和姓名模糊查询", notes="公共接口-员工号和姓名模糊查询")
    @GetMapping(value = "/queryEmpAccountApp")
    public Result<?> queryEmpAccountApp(@RequestParam(required=true) String userInfoApp){
        List<SafetyEmployeeMessageVO> pageList = commonService.queryEmployFuzzySearch(userInfoApp);
        return Result.OK(pageList);
    }


    /**
     * 依据组织架构查询用户
     * @param orgCode
     * @return
     */
    @ApiOperation(value="公共接口-依据组织架构查询用户", notes="公共接口-依据组织架构查询用户")
    @GetMapping(value = "/queryUserInfo")
    public Result<?> queryUserInfo(@RequestParam(required=true) String orgCode){
        List<SafetyEmployeeMessageVO> pageList = commonService.queryEmployByOrgCode(orgCode);
        return Result.OK(pageList);
    }

    @ApiOperation(value="公共接口-厂区下拉列表", notes="公共接口-厂区下拉列表")
    @GetMapping("/queryFactory")
    public Result<?> queryFactory(){
        List<SafetyFieldManageVO> safetyFieldManageVOList = commonService.queryFactoryDrop();
        return Result.OK(safetyFieldManageVOList);
    }

    @ApiOperation(value="公共接口-厂区下拉列表(带过滤)", notes="公共接口-厂区下拉列表(带过滤)")
    @GetMapping("/queryFactoryFilter")
    public Result<?> queryFactoryFilter(){
        List<SafetyFieldManageVO> safetyFieldManageVOList = commonService.queryFactoryFilterDrop();
        return Result.OK(safetyFieldManageVOList);
    }

    @ApiOperation(value="公共接口-隐患区域下拉列表", notes="公共接口-隐患区域下拉列表")
    @GetMapping("/queryRiskRegion")
    public Result<?> queryRiskRegion(@RequestParam(required=false) String parentIds){
        List<SafetyFieldManageVO> safetyFieldManageVOList = commonService.queryRiskRegion(parentIds);
        return Result.OK(safetyFieldManageVOList);
    }
    @ApiOperation(value="公共接口-位置下拉列表", notes="公共接口-位置下拉列表")
    @GetMapping("/queryLocationInfo")
    public Result<?> queryLocationInfo(@RequestParam(value = "factoryId", required=false) String factoryId,
                                       @RequestParam(value = "areaId", required=false) String areaId,
                                       @RequestParam(value = "orgId",required=false) String orgId,
                                       @RequestParam(value = "orgCode",required=false) String orgCode){
        List<SafetyLocationInfoVO> safetyLocationInfoVOList = commonService.queryLocationInfo(factoryId,areaId,orgId,orgCode);
        return Result.OK(safetyLocationInfoVOList);
    }

    /**
     * 获取角色下拉框列表
     *
     * @return 角色列表
     */
    @ApiOperation(value="公共接口-获取角色下拉框列表", notes="公共接口-获取角色下拉框列表")
    @GetMapping(value = "/optionList")
    public Result<?> optionlist(@RequestParam(required = false) Integer[] userType) {
        List<SafetySysRoleOptionListVO> list = commonService.optionlist(userType);
        return Result.OK(list);
    }

    /**
     * 获取组织机构树
     *
     * @return 角色列表
     */
    @ApiOperation(value="公共接口-获取组织机构树", notes="公共接口-获取组织机构树")
    @GetMapping(value = "/queryGroupInfoTree")
    public Result<?> queryGroupInfoTree() {
        List<SafetyGroupInfoTreeVO> treeVOs = commonService.queryGroupInfoTree();
        return Result.OK(treeVOs);
    }


    @ApiOperation(value="公共接口-获取组织机构的安全员", notes="公共接口-获取组织机构的安全员")
    @GetMapping(value = "/queryGroupInfoSafetyOfficer")
    public Result<?> queryGroupInfoSafetyOfficer(@RequestParam(value = "code",required = false) String code) {
        if (StringUtils.isEmpty(code)){
            return Result.OK("参数为空",new ArrayList<SafetyOfficerVO>());
        }
        List<SafetyOfficerVO> list = commonService.queryGroupInfoSafetyOfficer(code);
        return Result.OK(list);
    }

    // 代理平台接口  不让用 code  之类的关键字  新增接口 叫 orgCode
    @ApiOperation(value="公共接口-获取组织机构的安全员", notes="公共接口-获取组织机构的安全员")
    @GetMapping(value = "/queryGroupInfoSafetyOfficerNew")
    public Result<?> queryGroupInfoSafetyOfficerNew(@RequestParam(value = "orgCode",required = false) String orgCode) {
        if (StringUtils.isEmpty(orgCode)){
            return Result.OK("参数为空",new ArrayList<SafetyOfficerVO>());
        }
        List<SafetyOfficerVO> list = commonService.queryGroupInfoSafetyOfficer(orgCode);
        return Result.OK(list);
    }

    @ApiOperation(value="公共接口-获取组织机构的角色安全人", notes="公共接口-获取组织机构的角色安全人")
    @GetMapping(value = "/queryGroupInfoRoles")
    public Result<?> queryGroupInfoRoles(@RequestParam("orgCode") String orgCode,@RequestParam("roleCode")  String roleCode) {
        String roleRelCode = "";
        switch (roleCode){
            case "SafetyOfficer":
                roleRelCode = roleCodeConstant.getSafetyOfficer();
                break;
            case "SafetySupervisor":
                roleRelCode = roleCodeConstant.getSafetySupervisor();
                break;
            default:
                break;
        }
        if(StringUtils.isEmpty(roleRelCode)) return Result.OK("角色不存在");
        List<SafetyOfficerVO> list = commonService.queryGroupInfoRoles(orgCode,roleRelCode);
        return Result.OK(list);
    }

    /**
     * 刷新组织机构缓存
     *
     * @return
     */
    @ApiOperation(value="公共接口-刷新组织机构缓存", notes="公共接口-刷新组织机构缓存")
    @GetMapping(value = "/refreshGroupInfoCache")
    public Result<?> refreshGroupInfoCache() {
        commonService.refreshGroupInfoCache();
        return Result.OK();
    }

    /**
     * 获取岗位下拉框列表
     *
     * @return 岗位列表
     */
    @ApiOperation(value="岗位 - 获取岗位下拉框列表", notes="岗位 - 获取岗位下拉框列表")
    @GetMapping(value = "/jobOptionList")
    public Result<?> jobOptionList() {
        List<JobOptionListVO> listVOS = commonService.jobOptionList();
        return Result.OK(listVOS);
    }

    /**
     * 查询变更安全责任人
     *
     * @return
     */
    @ApiOperation(value="安全责任人流程信息表-查询登录人信息", notes="安全责任人流程信息表-查询登录人信息")
    @GetMapping(value = "/queryLoginUser")
    public Result<?> queryLoginUser() {
        LoginUser loginUser =  BusUtits.getLoginUser();
        if (loginUser != null && loginUser.getIsDefend() == null){
            Long num = commonService.checkDefend(loginUser.getEmpNo());
            if (num > 0) {
                loginUser.setIsDefend(1);
            } else {
                loginUser.setIsDefend(0);
            }
            Boolean b = commonService.checkIsResponsible(loginUser.getEmpNo(),Arrays.asList(roleCodeConstant.getSecurityAdministrator().split(",")));
            if (b){
                loginUser.setIsSecurityManager(1);
            }else {
                loginUser.setIsSecurityManager(0);
            }
        }
        return Result.OK(loginUser);
    }

    /**
     * 根据组织code获取对应安全员
     *
     * @return
     */
    @ApiOperation(value="根据组织code获取对应安全员", notes="根据组织code获取对应安全员")
    @GetMapping(value = "/queryResponsibleByOrgCode")
    public Result<List<YtsafetySysResponsibleVO>> queryResponsibleByOrgCode(@RequestParam(required = false) String orgCode) {
        List<String> roleCodeList = Arrays.asList(roleCodeConstant.getSafetyOfficer().split(","));
        List<YtsafetySysResponsibleVO> responsibleVOList = commonService.queryResponsibleByOrgCode(orgCode,roleCodeList);
        if (responsibleVOList.isEmpty()){
            responsibleVOList = commonService.queryResponsibleByOrgCode(orgCode,Arrays.asList(roleCodeConstant.getSafetySupervisor().split(",")));
        }
        return Result.OK(responsibleVOList);
    }
}
