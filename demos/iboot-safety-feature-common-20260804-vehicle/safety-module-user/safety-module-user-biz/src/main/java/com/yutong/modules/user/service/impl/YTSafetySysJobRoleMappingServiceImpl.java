package com.yutong.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.convert.SysJobRoleMappingConvert;
import com.yutong.modules.user.dto.YTSafetySysJobRoleMappingDTO;
import com.yutong.modules.user.dto.YTSafetySysJobRoleMappingImportDTO;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.entity.YTSafetySysJobRoleMapping;
import com.yutong.modules.user.mapper.YTSafetySysJobRoleMappingMapper;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.ISafetySysRoleService;
import com.yutong.modules.user.api.IYTSafetySysJobRoleMappingService;
import com.yutong.modules.user.vo.JobOptionListVO;
import com.yutong.modules.user.vo.YTSafetySysJobRoleMappingVO;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 岗位角色映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Service
public class YTSafetySysJobRoleMappingServiceImpl extends ServiceImpl<YTSafetySysJobRoleMappingMapper, YTSafetySysJobRoleMapping> implements IYTSafetySysJobRoleMappingService {

    @Resource
    private ISafetySysRoleService safetySysRoleService;

    @Autowired
    private SysJobRoleMappingConvert sysJobRoleMappingConvert;
    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Override
    public IPage<YTSafetySysJobRoleMappingVO> queryPageList(Integer pageNo, Integer pageSize, String jobName, String roleName) {
        Page<YTSafetySysJobRoleMappingVO> page = new Page<>(pageNo, pageSize);
        return baseMapper.selectPageList(page, jobName, roleName);
    }

    @Override
    public boolean add(YTSafetySysJobRoleMappingDTO dto) {
        // 校验各位名称是否已存在
        LambdaQueryWrapper<YTSafetySysJobRoleMapping> checkJobNameWrapper = new LambdaQueryWrapper<>();
        checkJobNameWrapper.eq(YTSafetySysJobRoleMapping::getJobCode, dto.getJobCode());
        checkJobNameWrapper.eq(YTSafetySysJobRoleMapping::getRoleId, dto.getRoleId());
        checkJobNameWrapper.eq(YTSafetySysJobRoleMapping::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (this.count(checkJobNameWrapper) > 0) {
            throw new RuntimeException("已存在相同岗位与角色映射关系，无法新增成功");
        }
        SafetySysRole safetySysRole = safetySysRoleService.getById(dto.getRoleId());
        if (ObjectUtil.isEmpty(safetySysRole)){
            throw new RuntimeException("角色信息不存在，无法新增成功");
        }
        List<JobOptionListVO> jobOptionListVOS = safetyCommonService.jobOptionList();
        JobOptionListVO jobOption = jobOptionListVOS.stream()
                .filter(j -> j.getJobCode().equals(dto.getJobCode()))
                .findFirst()
                .orElse(null);
        if (jobOption == null) {
            throw new RuntimeException("岗位编码不存在，无法新增成功");
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        YTSafetySysJobRoleMapping ytSafetySysJobRoleMapping = new YTSafetySysJobRoleMapping();
        ytSafetySysJobRoleMapping.setId(IdWorker.getId());
        ytSafetySysJobRoleMapping.setRoleId(dto.getRoleId());
        ytSafetySysJobRoleMapping.setRoleName(safetySysRole != null ? safetySysRole.getRoleName() : null);
        ytSafetySysJobRoleMapping.setJobCode(jobOption.getJobCode());
        ytSafetySysJobRoleMapping.setJobName(jobOption.getJobName());
        ytSafetySysJobRoleMapping.setCreateTime(new Date());
        ytSafetySysJobRoleMapping.setUpdateTime(new Date());
        ytSafetySysJobRoleMapping.setDelFlag(CommonConstant.DEL_FLAG_0);
        ytSafetySysJobRoleMapping.setCreateUser(loginUser != null ? loginUser.getRealname() : null);
        ytSafetySysJobRoleMapping.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
        ytSafetySysJobRoleMapping.setVersion(1);
        return this.save(ytSafetySysJobRoleMapping);
    }

    @Override
    public boolean update(YTSafetySysJobRoleMappingDTO dto) {
        if (dto.getId() == null){
            throw new IllegalArgumentException("主键未传，修改失败！");
        }
        YTSafetySysJobRoleMapping ytSafetySysJobRoleMapping = this.getById(dto.getId());
        if (ObjectUtil.isEmpty(ytSafetySysJobRoleMapping)){
            throw new IllegalArgumentException("未找到数据，修改失败！");
        }
        // 校验各位名称是否已存在
        LambdaQueryWrapper<YTSafetySysJobRoleMapping> checkJobNameWrapper = new LambdaQueryWrapper<>();
        checkJobNameWrapper.eq(YTSafetySysJobRoleMapping::getJobCode, dto.getJobCode())
                .eq(YTSafetySysJobRoleMapping::getRoleId,dto.getRoleId())
                .eq(YTSafetySysJobRoleMapping::getDelFlag, CommonConstant.DEL_FLAG_0)
                .ne(YTSafetySysJobRoleMapping::getId, dto.getId());
        if (this.count(checkJobNameWrapper) > 0) {
            throw new IllegalArgumentException("已存在相同岗位与角色映射关系，修改失败");
        }
        SafetySysRole safetySysRole = safetySysRoleService.getById(dto.getRoleId());
        if (ObjectUtil.isEmpty(safetySysRole)){
            throw new IllegalArgumentException("未查到角色数据，修改失败");
        }
        List<JobOptionListVO> jobOptionListVOS = safetyCommonService.jobOptionList();
        JobOptionListVO jobOption = jobOptionListVOS.stream()
                .filter(j -> j.getJobCode().equals(dto.getJobCode()))
                .findFirst()
                .orElse(null);
        if (jobOption == null) {
            throw new IllegalArgumentException("岗位编码不存在，修改失败");
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        BeanUtil.copyProperties(dto,ytSafetySysJobRoleMapping);
        ytSafetySysJobRoleMapping.setRoleId(dto.getRoleId());
        ytSafetySysJobRoleMapping.setRoleName(safetySysRole != null ? safetySysRole.getRoleName() : null);
        ytSafetySysJobRoleMapping.setJobCode(jobOption.getJobCode());
        ytSafetySysJobRoleMapping.setJobName(jobOption.getJobName());
        ytSafetySysJobRoleMapping.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
        ytSafetySysJobRoleMapping.setUpdateTime(new Date());
        return this.updateById(ytSafetySysJobRoleMapping);
    }

    @Override
    public boolean delete(Long id) {
        YTSafetySysJobRoleMapping ytSafetySysJobRoleMapping = this.getById(id);
        if (BeanUtil.isEmpty(ytSafetySysJobRoleMapping)){
            return false;
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ytSafetySysJobRoleMapping.setUpdateTime(new Date());
        ytSafetySysJobRoleMapping.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
        this.updateById(ytSafetySysJobRoleMapping);
        return this.removeById(id);
    }

    @Override
    public YTSafetySysJobRoleMappingVO getDetails(Long id) {
        YTSafetySysJobRoleMappingVO jobRoleMappingVO = new YTSafetySysJobRoleMappingVO();
        if (id == null){
            return jobRoleMappingVO;
        }
        YTSafetySysJobRoleMapping ytSafetySysJobRoleMapping = this.getById(id);
        if (ytSafetySysJobRoleMapping != null){
            jobRoleMappingVO = sysJobRoleMappingConvert.entityToVo(ytSafetySysJobRoleMapping);
        }
        return jobRoleMappingVO;
    }

    @Override
    public List<YTSafetySysJobRoleMappingVO> queryAllList(String jobName, String roleName) {
        LambdaQueryWrapper<YTSafetySysJobRoleMapping> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(YTSafetySysJobRoleMapping::getDelFlag,CommonConstant.DEL_FLAG_0)
                .like(ObjectUtil.isNotNull(jobName),YTSafetySysJobRoleMapping::getJobName,jobName)
                .like(ObjectUtil.isNotNull(roleName),YTSafetySysJobRoleMapping::getRoleName,roleName)
                .orderByDesc(YTSafetySysJobRoleMapping::getUpdateTime);

        List<YTSafetySysJobRoleMapping> list = this.list(lambdaQueryWrapper);

        return convertToVoList(list);
    }

    /**
     * 将 Entity 转换为 VO 并统计用户数量
     */
    private List<YTSafetySysJobRoleMappingVO> convertToVoList(List<YTSafetySysJobRoleMapping> entities) {
        return sysJobRoleMappingConvert.entityListToVoList(entities);
    }

    @Override
    public List<YTSafetySysJobRoleMappingImportDTO> importSysData(List<YTSafetySysJobRoleMappingImportDTO> importList) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<YTSafetySysJobRoleMappingImportDTO> msgVo = new ArrayList<YTSafetySysJobRoleMappingImportDTO>();
        List<YTSafetySysJobRoleMapping> saveList = new ArrayList<>();

        // 提取岗位编码，批量查询岗位下拉框
        List<String> jobCodes = importList.stream()
                .filter(item -> item.getJobCode() != null && !item.getJobCode().trim().isEmpty())
                .map(YTSafetySysJobRoleMappingImportDTO::getJobCode)
                .distinct()
                .collect(Collectors.toList());

        Map<String, JobOptionListVO> jobCodeMap = new HashMap<>();
        if (!jobCodes.isEmpty()) {
            List<JobOptionListVO> jobList = safetyCommonService.jobOptionList();
            jobCodeMap = jobList.stream()
                    .filter(j -> jobCodes.contains(j.getJobCode()))
                    .collect(Collectors.toMap(JobOptionListVO::getJobCode, j -> j, (a, b) -> a));
        }

        // 提取角色编码，批量查询角色
        List<String> roleNames = importList.stream()
                .filter(item -> item.getRoleName() != null && !item.getRoleName().trim().isEmpty())
                .map(YTSafetySysJobRoleMappingImportDTO::getRoleName)
                .distinct()
                .collect(Collectors.toList());

        Map<String, SafetySysRole> roleNameMap = new HashMap<>();
        if (!roleNames.isEmpty()) {
            LambdaQueryWrapper<SafetySysRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(SafetySysRole::getRoleName, roleNames)
                    .eq(SafetySysRole::getDeleteFalg, CommonConstant.DEL_FLAG_0.toString());
            List<SafetySysRole> roleList = safetySysRoleService.list(roleWrapper);
            roleNameMap = roleList.stream()
                    .collect(Collectors.toMap(SafetySysRole::getRoleName, r -> r, (a, b) -> a));
        }

        // 查询数据库中已存在的映射：根据岗位编码+角色名称组合判断
        List<YTSafetySysJobRoleMapping> existingList = this.list(
                new LambdaQueryWrapper<YTSafetySysJobRoleMapping>()
                        .eq(YTSafetySysJobRoleMapping::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .in(YTSafetySysJobRoleMapping::getJobCode, jobCodes));

        // 统计导入文件中各岗位+角色映射组合出现次数
        Map<String, Long> mappingCountMap = importList.stream()
                .filter(item -> item.getJobCode() != null && !item.getJobCode().trim().isEmpty()
                        && item.getRoleName() != null && !item.getRoleName().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        item -> item.getJobCode() + "|" + item.getRoleName(),
                        Collectors.counting()));

        for (YTSafetySysJobRoleMappingImportDTO item : importList) {
            if (BeanUtil.isEmpty(item)){
                continue;
            }

            JobOptionListVO jobOption = jobCodeMap.get(item.getJobCode());
            if (jobOption == null) {
                item.setErrorMsg("未找到匹配的岗位信息");
                msgVo.add(item);
                continue;
            }

            SafetySysRole safetySysRole = roleNameMap.get(item.getRoleName());
            if (safetySysRole == null) {
                item.setErrorMsg("未找到匹配的角色信息");
                msgVo.add(item);
                continue;
            }

            // 校验导入文件内的重复映射组合：出现大于1次的都不导入
            String mappingKey = item.getJobCode() + "|" + item.getRoleName();
            long mappingCount = mappingCountMap.getOrDefault(mappingKey, 0L);
            if (mappingCount > 1) {
                item.setErrorMsg("岗位与角色映射重复");
                msgVo.add(item);
                continue;
            }

            // 判断该岗位角色绑定关系是否已存在
            if (existingList.stream().anyMatch(e ->
                    e.getJobCode().equals(item.getJobCode()) && e.getRoleName().equals(item.getRoleName()))) {
                item.setErrorMsg("岗位与角色映射已存在");
                msgVo.add(item);
                continue;
            }

            YTSafetySysJobRoleMapping entity = new YTSafetySysJobRoleMapping();
            entity.setJobCode(jobOption.getJobCode());
            entity.setJobName(jobOption.getJobName());
            entity.setRoleId(safetySysRole.getId());
            entity.setRoleName(safetySysRole.getRoleName());
            entity.setCreateTime(new Date());
            entity.setCreateUser(loginUser != null ? loginUser.getRealname() : null);
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
            entity.setDelFlag(0);
            entity.setVersion(1);
            saveList.add(entity);
        }

        if (!saveList.isEmpty()) {
            this.saveBatch(saveList,1000);
        }
        return msgVo;
    }
}
