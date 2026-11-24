package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.risk.vo.YtsafetyTaskUserVO;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import com.yutong.modules.user.api.IYtsafetySysResponsibleMappingService;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.entity.YtsafetySysResponsibleMapping;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Description: 安全责任人名称批量解析器
 * 用于替代逐条执行 FIND_IN_SET 递归 SQL 的 N+1 查询方式：
 * 一次批量查询组织路径，一次批量查询责任人映射，然后在内存中沿组织路径
 * 从最深层向上查找，取最近一级有责任人的部门的人员名称
 */
@Component
public class SafetyResponsibleNameResolver {

    /** IN 条件分批大小，避免 IN 列表过大 */
    private static final int CHUNK_SIZE = 1000;

    @Autowired
    private ISafetyGroupInfoService safetyGroupInfoService;

    @Autowired
    private IYtsafetySysResponsibleMappingService responsibleMappingService;

    /**
     * 批量解析安全责任人名称：当前组织查不到责任人时向上级组织递归查找，
     * 取组织路径上最近一级有责任人的部门，多人用 "," 隔开，无匹配时为空串
     *
     * @param orgRolePairs 组织编码+角色编码组合，每个元素为 [orgCode, roleCode]
     * @return key = orgCode_roleCode，value = 责任人名称（逗号分隔）
     */
    public Map<String, String> resolveNames(Collection<String[]> orgRolePairs) {
        Map<String, String> result = new HashMap<>();
        if (CollectionUtils.isEmpty(orgRolePairs)) {
            return result;
        }
        // 去重收集组织编码、角色编码及组合
        Set<String> orgCodes = new HashSet<>();
        Set<String> roleCodes = new HashSet<>();
        List<String[]> distinctPairs = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String[] pair : orgRolePairs) {
            String orgCode = pair[0];
            String roleCode = pair[1];
            if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
                continue;
            }
            if (seen.add(orgCode + "_" + roleCode)) {
                distinctPairs.add(pair);
                orgCodes.add(orgCode);
                roleCodes.add(roleCode);
            }
        }
        if (distinctPairs.isEmpty()) {
            return result;
        }

        // 1. 批量查询组织路径：code -> org_code_path
        Map<String, String> orgPathMap = queryOrgCodePaths(orgCodes);

        // 2. 收集组织路径上出现的所有部门编码
        Set<String> deptCodes = new HashSet<>();
        for (String path : orgPathMap.values()) {
            if (StringUtils.isBlank(path)) {
                continue;
            }
            for (String code : path.split(",")) {
                if (StringUtils.isNotBlank(code)) {
                    deptCodes.add(code.trim());
                }
            }
        }

        // 3. 批量查询责任人映射：departCode_roleCode -> 人员名称集合
        Map<String, Set<String>> responsibleMap = queryResponsibleMap(deptCodes, roleCodes);

        // 4. 逐个组合沿组织路径从最深层向上查找责任人
        for (String[] pair : distinctPairs) {
            String names = resolveOne(orgPathMap.get(pair[0]), pair[1], responsibleMap);
            result.put(pair[0] + "_" + pair[1], names);
        }
        return result;
    }

    /**
     * 批量解析安全责任人，返回结构化用户信息（userId, empName, empNo）
     *
     * @param orgRolePairs 组织编码+角色编码组合，每个元素为 [orgCode, roleCode]
     * @return key = orgCode_roleCode，value = 用户列表
     */
    public Map<String, List<YtsafetyTaskUserVO>> resolveNamesToUserVO(Collection<String[]> orgRolePairs) {
        Map<String, List<YtsafetyTaskUserVO>> result = new HashMap<>();
        if (CollectionUtils.isEmpty(orgRolePairs)) {
            return result;
        }
        Set<String> orgCodes = new HashSet<>();
        Set<String> roleCodes = new HashSet<>();
        List<String[]> distinctPairs = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String[] pair : orgRolePairs) {
            String orgCode = pair[0];
            String roleCode = pair[1];
            if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
                continue;
            }
            if (seen.add(orgCode + "_" + roleCode)) {
                distinctPairs.add(pair);
                orgCodes.add(orgCode);
                roleCodes.add(roleCode);
            }
        }
        if (distinctPairs.isEmpty()) {
            return result;
        }

        Map<String, String> orgPathMap = queryOrgCodePaths(orgCodes);

        Set<String> deptCodes = new HashSet<>();
        for (String path : orgPathMap.values()) {
            if (StringUtils.isBlank(path)) {
                continue;
            }
            for (String code : path.split(",")) {
                if (StringUtils.isNotBlank(code)) {
                    deptCodes.add(code.trim());
                }
            }
        }

        Map<String, Set<YtsafetyTaskUserVO>> responsibleMap = queryResponsibleUserVOMap(deptCodes, roleCodes);

        for (String[] pair : distinctPairs) {
            List<YtsafetyTaskUserVO> users = resolveOneUserVO(orgPathMap.get(pair[0]), pair[1], responsibleMap);
            result.put(pair[0] + "_" + pair[1], users);
        }
        return result;
    }


    /**
     * 单个组合解析：沿组织路径从最深层（FIND_IN_SET 位置最大）向上，
     * 取第一个有责任人的部门的人员名称
     */
    private String resolveOne(String orgCodePath, String roleCode, Map<String, Set<String>> responsibleMap) {
        if (StringUtils.isBlank(orgCodePath)) {
            return "";
        }
        String[] pathCodes = orgCodePath.split(",");
        for (int i = pathCodes.length - 1; i >= 0; i--) {
            String deptCode = StringUtils.trimToNull(pathCodes[i]);
            if (deptCode == null) {
                continue;
            }
            Set<String> names = responsibleMap.get(deptCode + "_" + roleCode);
            if (!CollectionUtils.isEmpty(names)) {
                return String.join(",", names);
            }
        }
        return "";
    }

    private List<YtsafetyTaskUserVO> resolveOneUserVO(String orgCodePath, String roleCode, Map<String, Set<YtsafetyTaskUserVO>> responsibleMap) {
        if (StringUtils.isBlank(orgCodePath)) {
            return Collections.emptyList();
        }
        String[] pathCodes = orgCodePath.split(",");
        for (int i = pathCodes.length - 1; i >= 0; i--) {
            String deptCode = StringUtils.trimToNull(pathCodes[i]);
            if (deptCode == null) {
                continue;
            }
            Set<YtsafetyTaskUserVO> users = responsibleMap.get(deptCode + "_" + roleCode);
            if (!CollectionUtils.isEmpty(users)) {
                return new ArrayList<>(users);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 批量查询组织路径
     */
    private Map<String, String> queryOrgCodePaths(Set<String> orgCodes) {
        Map<String, String> orgPathMap = new HashMap<>();
        List<String> codeList = new ArrayList<>(orgCodes);
        for (int i = 0; i < codeList.size(); i += CHUNK_SIZE) {
            List<String> chunk = codeList.subList(i, Math.min(i + CHUNK_SIZE, codeList.size()));
            List<SafetyGroupInfo> groups = safetyGroupInfoService.list(
                    new LambdaQueryWrapper<SafetyGroupInfo>()
                            .select(SafetyGroupInfo::getCode, SafetyGroupInfo::getOrgCodePath)
                            .eq(SafetyGroupInfo::getStatus, "Y")
                            .in(SafetyGroupInfo::getCode, chunk));
            for (SafetyGroupInfo group : groups) {
                orgPathMap.putIfAbsent(group.getCode(), group.getOrgCodePath());
            }
        }
        return orgPathMap;
    }

    /**
     * 批量查询责任人映射，按 departCode_roleCode 分组人员名称（去重）
     */
    private Map<String, Set<String>> queryResponsibleMap(Set<String> deptCodes, Set<String> roleCodes) {
        Map<String, Set<String>> responsibleMap = new HashMap<>();
        if (deptCodes.isEmpty() || roleCodes.isEmpty()) {
            return responsibleMap;
        }
        List<String> codeList = new ArrayList<>(deptCodes);
        for (int i = 0; i < codeList.size(); i += CHUNK_SIZE) {
            List<String> chunk = codeList.subList(i, Math.min(i + CHUNK_SIZE, codeList.size()));
            List<YtsafetySysResponsibleMapping> mappings = responsibleMappingService.list(
                    new LambdaQueryWrapper<YtsafetySysResponsibleMapping>()
                            .select(YtsafetySysResponsibleMapping::getDepartCode,
                                    YtsafetySysResponsibleMapping::getRoleCode,
                                    YtsafetySysResponsibleMapping::getEmpName)
                            .in(YtsafetySysResponsibleMapping::getRoleCode, roleCodes)
                            .in(YtsafetySysResponsibleMapping::getDepartCode, chunk));
            for (YtsafetySysResponsibleMapping mapping : mappings) {
                if (StringUtils.isBlank(mapping.getEmpName())) {
                    continue;
                }
                responsibleMap.computeIfAbsent(mapping.getDepartCode() + "_" + mapping.getRoleCode(),
                        k -> new LinkedHashSet<>()).add(mapping.getEmpName());
            }
        }
        return responsibleMap;
    }

    private Map<String, Set<YtsafetyTaskUserVO>> queryResponsibleUserVOMap(Set<String> deptCodes, Set<String> roleCodes) {
        Map<String, Set<YtsafetyTaskUserVO>> responsibleMap = new HashMap<>();
        if (deptCodes.isEmpty() || roleCodes.isEmpty()) {
            return responsibleMap;
        }
        List<String> codeList = new ArrayList<>(deptCodes);
        for (int i = 0; i < codeList.size(); i += CHUNK_SIZE) {
            List<String> chunk = codeList.subList(i, Math.min(i + CHUNK_SIZE, codeList.size()));
            List<YtsafetySysResponsibleMapping> mappings = responsibleMappingService.list(
                    new LambdaQueryWrapper<YtsafetySysResponsibleMapping>()
                            .select(YtsafetySysResponsibleMapping::getDepartCode,
                                    YtsafetySysResponsibleMapping::getRoleCode,
                                    YtsafetySysResponsibleMapping::getUserId,
                                    YtsafetySysResponsibleMapping::getEmpName,
                                    YtsafetySysResponsibleMapping::getEmpNo)
                            .in(YtsafetySysResponsibleMapping::getRoleCode, roleCodes)
                            .in(YtsafetySysResponsibleMapping::getDepartCode, chunk));
            for (YtsafetySysResponsibleMapping mapping : mappings) {
                if (StringUtils.isBlank(mapping.getEmpName())) {
                    continue;
                }
                YtsafetyTaskUserVO vo = new YtsafetyTaskUserVO();
                vo.setUserId(mapping.getUserId());
                vo.setEmpName(mapping.getEmpName());
                vo.setEmpNo(mapping.getEmpNo());
                responsibleMap.computeIfAbsent(mapping.getDepartCode() + "_" + mapping.getRoleCode(),
                        k -> new LinkedHashSet<>()).add(vo);
            }
        }
        return responsibleMap;
    }


}
