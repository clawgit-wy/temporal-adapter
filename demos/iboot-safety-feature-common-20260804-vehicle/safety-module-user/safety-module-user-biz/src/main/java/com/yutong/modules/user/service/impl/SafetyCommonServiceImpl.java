package com.yutong.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.constant.CustomConstantConfig;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.entity.SafetySysUser;
import com.yutong.modules.user.mapper.SafetyCommonMapper;
import com.yutong.modules.user.mapper.SafetyGroupInfoMapper;
import com.yutong.modules.user.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 公共接口
 */
@Service
public class SafetyCommonServiceImpl  implements ISafetyCommonService {

    @Autowired
    private SafetyCommonMapper commonMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CustomConstantConfig customConstantConfig;

    @Resource
    private SafetyGroupInfoMapper safetyGroupInfoMapper;

    @Value("${ytsafety.custom.constant.safetyOfficer}")
    private String safetyOfficer;
    @Value("${ytsafety.custom.constant.topParentCode}")
    private String topParentCode;

    @Override
    public List<SafetyEmployeeMessageVO> queryEmployFuzzySearch(String userInfo) {
        if(StringUtils.isEmpty(userInfo)) return new ArrayList<SafetyEmployeeMessageVO>();
        List<SafetyEmployeeMessageVO> vos = commonMapper.queryEmployFuzzySearch(userInfo);
        if(vos==null||vos.isEmpty()) return new ArrayList<SafetyEmployeeMessageVO>();
        for (SafetyEmployeeMessageVO vo:vos){
            String orgName = vo.getOrgName();
            if (StringUtils.isEmpty(orgName)) continue;
            orgName = BusUtits.reverseCommaStr(orgName);
            vo.setOrgName(orgName);
        }
        return vos;
    }

    @Override
    public List<SafetyEmployeeMessageVO> queryEmployByOrgCode(String orgCode) {
        if(StringUtils.isEmpty(orgCode)) return new ArrayList<SafetyEmployeeMessageVO>();
        List<SafetyEmployeeMessageVO> vos = commonMapper.queryEmployByOrgCode(orgCode);
        if(vos==null||vos.isEmpty()) return new ArrayList<SafetyEmployeeMessageVO>();
        for (SafetyEmployeeMessageVO vo:vos){
            String orgName = vo.getOrgName();
            if (StringUtils.isEmpty(orgName)) continue;
            orgName = BusUtits.reverseCommaStr(orgName);
            vo.setOrgName(orgName);
        }
        return vos;
    }

    @Override
    public List<SafetyFieldManageVO> queryFactoryDrop() {
        return commonMapper.queryFactoryDrop();
    }

    @Override
    public List<SafetyFieldManageVO> queryFactoryFilterDrop() {
        return commonMapper.queryFactoryFilterDrop();
    }

    @Override
    public List<SafetyFieldManageVO> queryRegionDrop() {
        return commonMapper.queryRegionDrop();
    }

    @Override
    public List<SafetyFieldManageVO> queryRiskRegion(String parentIds) {
        List<SafetyFieldManageVO> List = new ArrayList<>();
        if(StringUtils.isEmpty(parentIds)){
            return List;
        }else{
            String [] parentIdArray = parentIds.split(",");
            List<String> parentIdList = Arrays.stream(parentIdArray).collect(Collectors.toList());
            List = commonMapper.queryRiskRegion(parentIdList);
            return List;
        }
    }

    @Override
    public List<SafetyFieldManageVO> queryAllRiskRegion() {
        return commonMapper.queryAllRiskRegion();
    }

    @Override
    public List<SafetyLocationInfoVO> queryLocationInfo(String factoryId, String areaId, String orgId, String orgCode) {
        return commonMapper.queryLocationInfo(factoryId,areaId,orgId,orgCode);
    }

    @Override
    public List<SafetySysRoleOptionListVO> optionlist(Integer[] userTypes) {

        LambdaQueryWrapper<SafetySysRole> queryWrapper = new LambdaQueryWrapper<SafetySysRole>();
        queryWrapper.eq(SafetySysRole::getDeleteFalg,"0");
        if(userTypes!=null&&userTypes.length>0){
            queryWrapper.in(SafetySysRole::getRoleType, Arrays.asList(userTypes));
        }
        return commonMapper.optionlist(queryWrapper);
    }

    @Override
    @Lock4j(name = "ytsafety:group:tree:cache")
    public List<SafetyGroupInfoTreeVO> queryGroupInfoTree() {
        List<SafetyGroupInfoTreeVO> groupInfoTree = (List<SafetyGroupInfoTreeVO>) redisUtil.get(CommonConstant.GROUP_INFO_TREE);
        if (groupInfoTree == null){
            groupInfoTree = queryGroupInfoTreePackaging();
            redisUtil.set(CommonConstant.GROUP_INFO_TREE,groupInfoTree);
        }
        for (SafetyGroupInfoTreeVO vo: groupInfoTree){
            renameOrgNamePath(vo.getChildren());
        }
        return groupInfoTree;
    }

    private List<SafetyGroupInfoTreeVO> queryGroupInfoTreePackaging(){
        List<SafetyGroupInfoTreeVO> roots = commonMapper.queryGroupInfoRoot(customConstantConfig.getTopParentCode());
        if(CollectionUtils.isEmpty(roots)){
            return roots;
        }
        for (SafetyGroupInfoTreeVO vo: roots){
            List<SafetyGroupInfoTreeVO> subtree = commonMapper.queryGroupInfoChild(vo.getCode());
            Map<String,List<SafetyGroupInfoTreeVO>> groupInfoMap = subtree.stream()
                    .collect(Collectors.groupingBy(SafetyGroupInfoTreeVO::getParentCode));
            buildGroupInfoTree(vo,groupInfoMap);
        }
        return roots;
    }

    @Override
    @Lock4j(name = "ytsafety:group:cache")
    public void refreshGroupInfoCache() {
        refreshGroupInfoTree();
        refreshGroupInfoTreeMap();
        refreshGroupInfoFactoryMap();
    }

    private void refreshGroupInfoFactoryMap() {
        List<SafetyGroupInfoTreeVO> groupInfos = commonMapper.queryGroupInfoRoot(customConstantConfig.getTopParentCode());
        if(!CollectionUtils.isEmpty(groupInfos)){
            List<SafetyGroupInfoTreeVO> results = new ArrayList<>(groupInfos);
            for (SafetyGroupInfoTreeVO vo: groupInfos){
                List<SafetyGroupInfoTreeVO> subtree = commonMapper.queryGroupInfoChild(vo.getCode());
                results.addAll(subtree);
            }
            Map<String, SafetyGroupInfoTreeVO> voMap = results.stream()
                    .collect(Collectors.toMap(SafetyGroupInfoTreeVO::getOrgNamePath, f -> f, (a, b) -> a));
            redisUtil.set(CommonConstant.GROUP_INFO_TREE_FACTORY_MAP,voMap);
        }

    }

    private void refreshGroupInfoTree(){
        List<SafetyGroupInfoTreeVO> groupInfoTree = queryGroupInfoTreePackaging();
        redisUtil.set(CommonConstant.GROUP_INFO_TREE,groupInfoTree);
    }

    private void refreshGroupInfoTreeMap(){
        Map<String, Long> groupInfoMap = groupInfoMapPackaging();
        redisUtil.set(CommonConstant.GROUP_INFO_TREE_MAP,groupInfoMap);
    }

    private void buildGroupInfoTree(SafetyGroupInfoTreeVO root,Map<String,List<SafetyGroupInfoTreeVO>> groupInfoMap){
        List<SafetyGroupInfoTreeVO> subTree = groupInfoMap.get(root.getCode());
        if(subTree==null||subTree.isEmpty()){
            return;
        }
        root.setChildren(subTree);
        for (SafetyGroupInfoTreeVO vo: subTree){
            buildGroupInfoTree(vo,groupInfoMap);
        }
    }

    private void renameOrgNamePath(List<SafetyGroupInfoTreeVO> children){
        if (CollectionUtil.isEmpty(children)) {
            return;
        }
        for (SafetyGroupInfoTreeVO vo: children){
            // 子节点全路径逆序处理
            vo.setOrgNamePath(CollectionUtil.join(CollectionUtil.reverse(Arrays.asList(vo.getOrgNamePath().split(","))), ","));
            renameOrgNamePath(vo.getChildren());
        }
    }


    @Override
    public List<JobOptionListVO> jobOptionList() {
        return  commonMapper.jobOptionList();
    }

    @Override
    @Lock4j(name = "ytsafety:group:map:cache")
    public Map<String, Long> queryGroupInfoMap() {
        Map<String, Long> groupInfoMap = (Map<String, Long>) redisUtil.get(CommonConstant.GROUP_INFO_TREE_MAP);
        if (groupInfoMap == null){
            groupInfoMap = groupInfoMapPackaging();
            redisUtil.set(CommonConstant.GROUP_INFO_TREE_MAP,groupInfoMap);
        }
        return groupInfoMap;
    }

    private Map<String, Long> groupInfoMapPackaging(){
        List<SafetyGroupInfoTreeVO> groupInfos = commonMapper.queryGroupInfoRoot(customConstantConfig.getTopParentCode());
        if(CollectionUtils.isEmpty(groupInfos)){
            return new HashMap<>();
        }
        List<SafetyGroupInfoTreeVO> results = new ArrayList<>(groupInfos);
        for (SafetyGroupInfoTreeVO vo: groupInfos){
            List<SafetyGroupInfoTreeVO> subtree = commonMapper.queryGroupInfoChild(vo.getCode());
            results.addAll(subtree);
        }
        return results.stream()
                .collect(Collectors.toMap(x -> x.getOrgNamePath().replace(",", "_"), // key：id字段
                        SafetyGroupInfoTreeVO::getId,                            // value：vo对象
                        (oldVal, newVal) -> oldVal         // 重复id时保留旧值
                ));
    }

    @Override
    @Lock4j(name = "ytsafety:group:factory:map:cache")
    public Map<String, SafetyGroupInfoTreeVO> queryGroupInfoFactoryMap(){
        Map<String, SafetyGroupInfoTreeVO> groupInfosMap = (Map<String, SafetyGroupInfoTreeVO>) redisUtil.get(CommonConstant.GROUP_INFO_TREE_FACTORY_MAP);
        if (groupInfosMap == null){
            List<SafetyGroupInfoTreeVO> groupInfos = commonMapper.queryGroupInfoRoot(customConstantConfig.getTopParentCode());
            if(CollectionUtils.isEmpty(groupInfos)){
                return new HashMap<>();
            }
            List<SafetyGroupInfoTreeVO> results = new ArrayList<>(groupInfos);
            for (SafetyGroupInfoTreeVO vo: groupInfos){
                List<SafetyGroupInfoTreeVO> subtree = commonMapper.queryGroupInfoChild(vo.getCode());
                results.addAll(subtree);
            }
            groupInfosMap = results.stream()
                    .collect(Collectors.toMap(SafetyGroupInfoTreeVO::getOrgNamePath, f -> f, (a, b) -> a));
            redisUtil.set(CommonConstant.GROUP_INFO_TREE_FACTORY_MAP,groupInfosMap);
        }
        return groupInfosMap;
    }

    @Override
    public Map<Long, String> queryGroupInfoByIds(List<Long> orgIds) {
        if (orgIds == null || orgIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> idSet = new LinkedHashSet<>(orgIds);
        return queryGroupInfoFactoryMap()
                .values().stream()
                .filter(vo -> idSet.contains(vo.getId()))
                .collect(Collectors.toMap(SafetyGroupInfoTreeVO::getId, SafetyGroupInfoTreeVO::getName));
    }

    @Override
    public List<SafetyOfficerVO> queryGroupInfoSafetyOfficer(String code) {
        List<SafetyOfficerVO> safetyOfficerVOS = commonMapper.queryGroupInfoSafetyOfficer(code, safetyOfficer);
        if (CollectionUtil.isEmpty(safetyOfficerVOS)) {
            // 如果安全责任人为空，查询上级组织安全责任人
            SafetyGroupInfo safetyGroupInfo = safetyGroupInfoMapper.selectOne(new LambdaQueryWrapper<SafetyGroupInfo>()
                    .eq(SafetyGroupInfo::getStatus, "Y")
                    .eq(SafetyGroupInfo::getCode, code));
            if(BeanUtil.isNotEmpty(safetyGroupInfo)){
                if (StrUtil.isBlank(safetyGroupInfo.getParentCode()) || Objects.equals(safetyGroupInfo.getParentCode(), topParentCode)) {
                    return safetyOfficerVOS.stream().filter(BeanUtil::isNotEmpty).collect(Collectors.toList());
                }
                return queryGroupInfoSafetyOfficer(safetyGroupInfo.getParentCode());
            }

        }
        return safetyOfficerVOS.stream().filter(BeanUtil::isNotEmpty).collect(Collectors.toList());
    }

    @Override
    public List<SafetyOfficerVO> queryGroupInfoRoles(String orgCode, String roleCodes) {
        List<SafetyOfficerVO> safetyOfficerVOS = commonMapper.queryGroupInfoSafetyOfficer(orgCode, roleCodes);
        if (CollectionUtil.isEmpty(safetyOfficerVOS)) {
            // 如果安全责任人为空，查询上级组织安全责任人
            SafetyGroupInfo safetyGroupInfo = safetyGroupInfoMapper.selectOne(new LambdaQueryWrapper<SafetyGroupInfo>()
                    .eq(SafetyGroupInfo::getStatus, "Y")
                    .eq(SafetyGroupInfo::getCode, orgCode));
            if(BeanUtil.isNotEmpty(safetyGroupInfo)){
                if (StrUtil.isBlank(safetyGroupInfo.getParentCode()) || Objects.equals(safetyGroupInfo.getParentCode(), topParentCode)) {
                    return safetyOfficerVOS.stream().filter(BeanUtil::isNotEmpty).collect(Collectors.toList());
                }
                return queryGroupInfoRoles(safetyGroupInfo.getParentCode(),roleCodes);
            }
        }
        return safetyOfficerVOS.stream().filter(BeanUtil::isNotEmpty).collect(Collectors.toList());
    }

    @Override
    public Long checkDefend(String empNo) {
        return commonMapper.checkDefend(empNo);
    }

    @Override
    public List<YtsafetySysResponsibleVO> queryResponsibleByOrgCode(String orgCode,List<String> roleCodeList) {
        return commonMapper.queryResponsibleByOrgCode(orgCode,roleCodeList);
    }

    @Override
    public List<YtsafetySysResponsibleVO> queryResponsibleByOrgCodeHierarchy(String orgCode, List<String> roleCodeList) {
        List<YtsafetySysResponsibleVO> result = commonMapper.queryResponsibleByOrgCode(orgCode, roleCodeList);
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        // 当前组织未找到，向上递归查询父级组织
        SafetyGroupInfo safetyGroupInfo = safetyGroupInfoMapper.selectOne(
                new LambdaQueryWrapper<SafetyGroupInfo>()
                        .eq(SafetyGroupInfo::getStatus, "Y")
                        .eq(SafetyGroupInfo::getCode, orgCode));
        if (safetyGroupInfo != null) {
            String parentCode = safetyGroupInfo.getParentCode();
            if (StringUtils.isNotBlank(parentCode) && !Objects.equals(parentCode, topParentCode)) {
                return queryResponsibleByOrgCodeHierarchy(parentCode, roleCodeList);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public SafetyEmployeeMessage querySafetyEmployeeMessageByEmpLogin(String empLogin) {
        return commonMapper.querySafetyEmployeeMessageByEmpLogin(empLogin);
    }

    @Override
    public List<YtsafetySysResponsibleVO> queryResponsibleByFactory(Long plantId, List<String> roleCodeList) {
        return commonMapper.queryResponsibleByFactory(plantId,roleCodeList);
    }

    @Override
    public List<YtsafetySysResponsibleVO> queryResponsibleByFactoryAndDepart(String orgCode, Long plantId, List<String> roleCodeList) {
        return commonMapper.queryResponsibleByFactoryAndDepart(orgCode,plantId,roleCodeList);
    }

    @Override
    public Boolean checkIsResponsible(String empNo,List<String> roleCodeList) {
        if (StringUtils.isEmpty(empNo) || CollectionUtils.isEmpty(roleCodeList)) return false;
        return commonMapper.checkIsResponsible(empNo, roleCodeList);
    }

    @Override
    public List<Long> getAuthorizedFactoryIds(String empNo) {
        return commonMapper.getAuthorizedFactoryIds(empNo);
    }

    //获取位置审核的消防员
    @Override
    public List<SafetySysUser> getAproveUser(String roleCode, String code) {
        return commonMapper.getAproveUser(roleCode, code);
    }

    @Override
    public List<SafetySysUser> selectFactoryRole(String factoryCode, List<String> roles) {
        return commonMapper.selectFactoryRole(factoryCode, roles);
    }


    /**
     * 获取组织路径
     */
    @Override
    public String getOrgPathName(Long orgId) {
        if (orgId == null) {
            return "";
        }
        List<SafetyGroupInfoTreeVO> groupInfoTree = queryGroupInfoTree();
        if (CollectionUtil.isEmpty(groupInfoTree)) {
            return "";
        }
        return findOrgPathById(groupInfoTree, orgId);
    }

    private String findOrgPathById(List<SafetyGroupInfoTreeVO> tree, Long targetId) {
        if (CollectionUtil.isEmpty(tree)) {
            return "";
        }
        for (SafetyGroupInfoTreeVO vo : tree) {
            if (vo.getId().equals(targetId)) {
                return vo.getOrgNamePath();
            }
            String result = findOrgPathById(vo.getChildren(), targetId);
            if (StringUtils.isNotBlank(result)) {
                return result;
            }
        }
        return "";
    }

}
