package com.yutong.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.dto.EmployeeMessageListParam;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.mapper.SafetyEmployeeMessageMapper;
import com.yutong.modules.user.vo.JobOptionListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: EHR员工信息
 * @Author: jeecg-boot
 * @Date: 2026-05-22
 * @Version: V1.0
 */
@Service
public class SafetyEmployeeMessageServiceImpl extends ServiceImpl<SafetyEmployeeMessageMapper, SafetyEmployeeMessage> implements ISafetyEmployeeMessageService {
    @Autowired
    private SafetyEmployeeMessageMapper safetyEmployeeMessageMapper;

    @Override
    public List<JobOptionListVO> jobOptionList() {
        List<SafetyEmployeeMessage> list = safetyEmployeeMessageMapper.selectDistinctPosition();
        return list.stream()
                .map(emp -> new JobOptionListVO()
                        .setJobCode(emp.getPositionCode())
                        .setJobName(emp.getPositionName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getLoginNameByNo(String empNo) {
        if (StringUtils.isBlank(empNo)) {
            return "";
        }
        SafetyEmployeeMessage employee = lambdaQuery()
                .eq(SafetyEmployeeMessage::getEmpNo, empNo)
                .eq(SafetyEmployeeMessage::getStatus, "NORMAL")
                .one();
        if (employee != null && StringUtils.isNotBlank(employee.getEmail())) {
            String email = employee.getEmail();
            int endIdx = email.lastIndexOf("@");
            if (endIdx > 0) {
                return email.substring(0, endIdx);
            }
        }
        return "";
    }

    @Override
    public SafetyEmployeeMessage getByNo(String empNo) {
        if (StringUtils.isBlank(empNo)) {
            return null;
        }
        return lambdaQuery()
                .eq(SafetyEmployeeMessage::getEmpNo, empNo)
                .eq(SafetyEmployeeMessage::getStatus, "NORMAL")
                .one();
    }

    @Override
    public List<SafetyEmployeeMessage> pageList(EmployeeMessageListParam param) {
        // 构建查询条件
        LambdaQueryWrapper<SafetyEmployeeMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SafetyEmployeeMessage::getStatus, "NORMAL");

        // 员工号精确匹配
        if (StringUtils.isNotBlank(param.getFullEmpNo())) {
            wrapper.eq(SafetyEmployeeMessage::getEmpNo, param.getFullEmpNo());
        }
        // 员工号模糊匹配
        if (StringUtils.isNotBlank(param.getEmpNo())) {
            wrapper.like(SafetyEmployeeMessage::getEmpNo, param.getEmpNo());
        }
        // 姓名模糊匹配（左匹配）
        if (StringUtils.isNotBlank(param.getEmpName())) {
            wrapper.likeRight(SafetyEmployeeMessage::getEmpName, param.getEmpName());
        }
        // 手机号模糊匹配
        if (StringUtils.isNotBlank(param.getPhoneNo())) {
            wrapper.like(SafetyEmployeeMessage::getPhoneNo, param.getPhoneNo());
        }
        // 备注模糊匹配
        if (StringUtils.isNotBlank(param.getRemark())) {
            wrapper.like(SafetyEmployeeMessage::getRemark, param.getRemark());
        }
        // 人员类型精确匹配
        if (StringUtils.isNotBlank(param.getUserType())) {
            wrapper.eq(SafetyEmployeeMessage::getUserType, param.getUserType());
        }
        // 员工号列表
        if (param.getEmpNoList() != null && !param.getEmpNoList().isEmpty()) {
            wrapper.in(SafetyEmployeeMessage::getEmpNo, param.getEmpNoList());
        }
        // 类别
        if (StringUtils.isNotBlank(param.getPositionType())) {
            if ("空".equals(param.getPositionType())) {
                wrapper.isNull(SafetyEmployeeMessage::getPositionType);
            } else {
                wrapper.eq(SafetyEmployeeMessage::getPositionType, param.getPositionType());
            }
        }
        // 岗位名称
        if (StringUtils.isNotBlank(param.getPositionName())) {
            if ("空".equals(param.getPositionName())) {
                wrapper.isNull(SafetyEmployeeMessage::getPositionName);
            } else {
                wrapper.like(SafetyEmployeeMessage::getPositionName, param.getPositionName());
            }
        }
        // 组织编码（模糊匹配orgCodePath）
        if (StringUtils.isNotBlank(param.getOrgCode())) {
            wrapper.like(SafetyEmployeeMessage::getOrgCodePath, param.getOrgCode());
        }
        // 邮箱（左匹配）
        if (StringUtils.isNotBlank(param.getEmail())) {
            wrapper.likeRight(SafetyEmployeeMessage::getEmail, param.getEmail());
        }
        // 创建时间-起
        if (StringUtils.isNotBlank(param.getCreateBegin())) {
            wrapper.ge(SafetyEmployeeMessage::getCreateTime, param.getCreateBegin() + " 00:00:00");
        }
        // 创建时间-止
        if (StringUtils.isNotBlank(param.getCreateEnd())) {
            wrapper.lt(SafetyEmployeeMessage::getCreateTime, param.getCreateEnd() + " 23:59:59");
        }
        // 组织路径不包含关键字
        if (StringUtils.isNotBlank(param.getNoOrgPatchName())) {
            wrapper.notLike(SafetyEmployeeMessage::getOrgNamePath, param.getNoOrgPatchName());
        }
        // 组织路径为空
        if (StringUtils.isNotBlank(param.getNullOrgPathName())) {
            wrapper.isNull(SafetyEmployeeMessage::getOrgNamePath);
        }
        // 人脸下发标记
        if (param.getFaceIssueFlag() != null) {
            wrapper.eq(SafetyEmployeeMessage::getFaceIssueFlag, param.getFaceIssueFlag());
        }
        // 卡下发标记
        if (param.getCardIssueFlag() != null) {
            wrapper.eq(SafetyEmployeeMessage::getCardIssueFlag, param.getCardIssueFlag());
        }
        // 排除人员类型
        if (StringUtils.isNotBlank(param.getNoUserType())) {
            wrapper.ne(SafetyEmployeeMessage::getUserType, param.getNoUserType());
        }

        // 排序：按姓名升序或创建时间降序
        if (StringUtils.isNotBlank(param.getEmpName())) {
            wrapper.orderByAsc(SafetyEmployeeMessage::getEmpName);
        } else {
            wrapper.orderByDesc(SafetyEmployeeMessage::getCreateTime);
        }
        Page<SafetyEmployeeMessage> page = baseMapper.selectPage(new Page<>(1, 15), wrapper);
        return page.getRecords();
    }

    @Override
    public SafetyEmployeeMessage getEmployeeByWorkNo(String workNo, String phone) {
        LambdaQueryWrapper<SafetyEmployeeMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SafetyEmployeeMessage::getStatus, "NORMAL");

        boolean hasWorkNo = StringUtils.isNotBlank(workNo);
        boolean hasPhone = StringUtils.isNotBlank(phone);

        if (hasWorkNo) {
            wrapper.eq(SafetyEmployeeMessage::getEmpNo, workNo);
        }
        if (hasPhone) {
            wrapper.eq(SafetyEmployeeMessage::getPhoneNo, phone);
        }

        List<SafetyEmployeeMessage> list = baseMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new org.jeecg.common.exception.JeecgBootException("根据入参获取到多条数据，请核查");
        }
        return list.get(0);
    }

    @Override
    public int updateByEmpNoOfUnqualifiedRecord(List<String> ids) {
        return this.baseMapper.updateByEmpNoOfUnqualifiedRecord(ids);
    }

    @Override
    public List<SafetyEmployeeMessage> selectListForUnqualified(String dateStr) {
        return this.baseMapper.selectListForUnqualified(dateStr);
    }

    @Override
    public List<Integer> selectListByLowQuality(String dateStr) {
        return this.baseMapper.selectListByLowQuality(dateStr);
    }

    @Override
    public List<SafetyEmployeeMessage> selectListByLowQualityOfId(String dateStr, List<Integer> dataIdList) {
        return this.baseMapper.selectListByLowQualityOfId(dateStr, dataIdList);
    }
}
