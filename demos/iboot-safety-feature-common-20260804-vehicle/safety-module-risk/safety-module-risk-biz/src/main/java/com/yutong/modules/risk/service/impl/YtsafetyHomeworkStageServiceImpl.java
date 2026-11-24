package com.yutong.modules.risk.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStage;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcessDetails;
import com.yutong.modules.risk.mapper.YtsafetyHomeworkStageMapper;
import com.yutong.modules.risk.query.YtsafetyHomeworkStageQuery;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageProcessDetailsService;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageService;
import com.yutong.modules.risk.service.SafetyResponsibleNameResolver;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageExportVO;
import com.yutong.modules.risk.vo.YtsafetyTaskUserVO;
import lombok.SneakyThrows;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 作业环节表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Service
public class YtsafetyHomeworkStageServiceImpl extends ServiceImpl<YtsafetyHomeworkStageMapper, YtsafetyHomeworkStage> implements IYtsafetyHomeworkStageService {

    @Autowired
    private YtsafetyHomeworkStageMapper homeworkStageMapper;

    @Autowired
    private IYtsafetyHomeworkStageProcessDetailsService homeworkStageProcessDetailsService;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    @Autowired
    private SafetyResponsibleNameResolver responsibleNameResolver;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;

    @Override
    public IPage<YtsafetyHomeworkStage> queryPageList(Page<YtsafetyHomeworkStage> page, YtsafetyHomeworkStageQuery query) {
        IPage<YtsafetyHomeworkStage> stagePage = homeworkStageMapper.queryPageList(page, query);
        fillSafetyStaffNames(stagePage.getRecords());
        return stagePage;
    }

    @Override
    public List<YtsafetyHomeworkStage> queryDropdownList(String name) {
        LambdaQueryWrapper<YtsafetyHomeworkStage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YtsafetyHomeworkStage::getDelFlag, 0);
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(YtsafetyHomeworkStage::getLinkName, name);
        }
        wrapper.orderByDesc(YtsafetyHomeworkStage::getUpdateTime);
        return list(wrapper);
    }

    @Override
    public List<YtsafetyHomeworkStageExportVO> queryList(YtsafetyHomeworkStageQuery query) {
        List<YtsafetyHomeworkStageExportVO> list = homeworkStageMapper.queryStageList(query);
        if (CollectionUtil.isNotEmpty(list)) {
            fillExportSafetyStaffNames(list);
            list.forEach(stageExportVO -> {
                stageExportVO.setStageInfo(exportUrl + "safety/risk/workPhaseApprove?mode=detail&id=" + stageExportVO.getId());
            });
        }
        return list;
    }

    /**
     * 批量回填安全员名称和工号
     */
    private void fillSafetyStaffNames(List<YtsafetyHomeworkStage> records) {
        if (org.springframework.util.CollectionUtils.isEmpty(records)) {
            return;
        }
        List<String[]> pairs = new ArrayList<>();
        for (YtsafetyHomeworkStage record : records) {
            pairs.add(new String[]{record.getOrgCode(), record.getSafetyRoleCode()});
        }
        Map<String, List<YtsafetyTaskUserVO>> userMap = responsibleNameResolver.resolveNamesToUserVO(pairs);
        for (YtsafetyHomeworkStage record : records) {
            String orgCode = record.getOrgCode();
            String roleCode = record.getSafetyRoleCode();
            if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
                record.setSafetyStaff("");
                record.setSafetyStaffId("");
                continue;
            }
            List<YtsafetyTaskUserVO> users = userMap.get(orgCode + "_" + roleCode);
            if (CollectionUtils.isEmpty(users)) {
                record.setSafetyStaff("");
                record.setSafetyStaffId("");
            } else {
                record.setSafetyStaff(users.stream().map(YtsafetyTaskUserVO::getEmpName).collect(Collectors.joining(",")));
                record.setSafetyStaffId(users.stream().map(YtsafetyTaskUserVO::getEmpNo).collect(Collectors.joining(",")));
            }
        }
    }

    /**
     * 批量回填导出VO的安全员名称
     */
    private void fillExportSafetyStaffNames(List<YtsafetyHomeworkStageExportVO> records) {
        if (org.springframework.util.CollectionUtils.isEmpty(records)) {
            return;
        }
        List<String[]> pairs = new ArrayList<>();
        for (YtsafetyHomeworkStageExportVO record : records) {
            pairs.add(new String[]{record.getOrgCode(), record.getSafetyRoleCode()});
        }
        Map<String, String> nameMap = responsibleNameResolver.resolveNames(pairs);
        for (YtsafetyHomeworkStageExportVO record : records) {
            String orgCode = record.getOrgCode();
            String roleCode = record.getSafetyRoleCode();
            if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
                record.setSafetyStaff("");
                continue;
            }
            record.setSafetyStaff(nameMap.getOrDefault(orgCode + "_" + roleCode, ""));
        }
    }

    /**
     * 将审批通过的流程明细复制到正式作业环节表
     * @param processId 流程主键
     */
    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void copyProcessDetailsToStage(Long processId) {
        List<YtsafetyHomeworkStageProcessDetails> detailsList = homeworkStageProcessDetailsService.list(
                new LambdaQueryWrapper<YtsafetyHomeworkStageProcessDetails>()
                        .eq(YtsafetyHomeworkStageProcessDetails::getProcessId, processId)
                        .eq(YtsafetyHomeworkStageProcessDetails::getDelFlag, 0)
        );
        if (CollectionUtils.isEmpty(detailsList)) {
            return;
        }
        List<YtsafetyHomeworkStage> stageList = new ArrayList<>();
        for (YtsafetyHomeworkStageProcessDetails detail : detailsList) {
            YtsafetyHomeworkStage stage = new YtsafetyHomeworkStage();
            BeanUtils.copyProperties(detail, stage);
            if(ObjectUtil.isNotNull(detail.getChangeId())){
                stage.setId(detail.getChangeId());
            }
            stageList.add(stage);
        }
        saveOrUpdateBatch(stageList);
    }

    @Override
    public YtsafetyHomeworkStage getByLinkCode(String linkCode) {
        LambdaQueryWrapper<YtsafetyHomeworkStage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YtsafetyHomeworkStage::getLinkCode, linkCode);
        wrapper.eq(YtsafetyHomeworkStage::getDelFlag, 0);
        List<YtsafetyHomeworkStage> stageList = list(wrapper);
        if (CollectionUtil.isNotEmpty(stageList)){
            return stageList.get(0);
        }else {
            return null;
        }

    }

    /**
     * 封装修改字段
     * @param stage
     * @param loginUser
     */
    private void setUpdateData(YtsafetyHomeworkStage stage, LoginUser loginUser) {
        stage.setUpdateUser(loginUser.getEmpNo());
        stage.setUpdateTime(new Date());
        stage.setUpdateName(loginUser.getRealname());
    }
    /**
     * 封装新增字段
     * @param stage
     * @param loginUser
     */
    private void setAddData(YtsafetyHomeworkStage stage, LoginUser loginUser) {
        stage.setCreateUser(loginUser.getEmpNo());
        stage.setCreateTime(new Date());
        stage.setCreateName(loginUser.getRealname());
    }
}
