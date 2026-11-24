package com.yutong.safety.core.service.entry;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDOExample;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryCompanyRegisterQueryParams;
import com.yutong.safety.core.db.entity.entry.vo.EntryOverdueTaskVO;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryCompanyRegisterDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS;


@Service
public class SafetyEntryCompanyRegisterService extends AbstractSafetyWebBaseService<SafetyEntryCompanyRegisterDO, SafetyEntryCompanyRegisterDOExample, SafetyEntryCompanyRegisterQueryParams> {
    @Resource
    private SafetyEntryCompanyRegisterDOMapper safetyEntryCompanyRegisterDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyEntryCompanyRegisterDO, SafetyEntryCompanyRegisterDOExample> getMapper() {
        return safetyEntryCompanyRegisterDOMapper;
    }

    @Override
    protected void formatExample(SafetyEntryCompanyRegisterDOExample example, SafetyEntryCompanyRegisterQueryParams param, boolean pageType) {
        example.setOrderByClause(" create_time DESC");
        SafetyEntryCompanyRegisterDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(param.getButtJoint())) {
            criteria.andButtJointCodeOrButtJointNameEqualTo(param.getButtJoint());
        }
        if (StringUtils.isNotBlank(param.getFdName())) {
            criteria.andFdNameEqualTo(param.getFdName());
        }
        if (StringUtils.isNotBlank(param.getFdPhone())) {
            criteria.andFdPhoneEqualTo(param.getFdPhone());
        }
        if (StringUtils.isNotBlank(param.getFdCompany())) {
            criteria.andFdCompanyEqualTo(param.getFdCompany());
        }
        if (StringUtils.isNotBlank(param.getStatus())) {
            criteria.andStatusEqualTo(param.getStatus());
        }
        if (pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        if(StringUtils.isNotBlank(param.getRoleWorkNo())){
            criteria.andButtJointCodeEqualTo(param.getRoleWorkNo());
        }
    }

    @Override
    protected void batchDelete(SafetyEntryCompanyRegisterDOExample example, List<String> idList) {

    }

    /**
     * 唯一性校验
     */
    @Override
    protected void isExist(SafetyEntryCompanyRegisterDO data, SafetyEntryCompanyRegisterDOExample example) {
        SafetyEntryCompanyRegisterDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(data.getFdPhone())) {
            criteria.andFdPhoneEqualTo(data.getFdPhone());
        }
        if (data.getId() != null) {
            criteria.andIdNotEqualTo(data.getId());
            criteria.andStatusEqualTo(COMPANY_REGISTER_STATUS_PASS);
        }
    }

    /**
     * 根据手机号获取供应商注册信息
     *
     * @param phone 手机号
     */
    public SafetyEntryCompanyRegisterDO selectInfo(String phone) {
        SafetyEntryCompanyRegisterDOExample example = new SafetyEntryCompanyRegisterDOExample();
        SafetyEntryCompanyRegisterDOExample.Criteria criteria = example.createCriteria();
        criteria.andFdPhoneEqualTo(phone);
        example.setOrderByClause(" update_time desc");
        List<SafetyEntryCompanyRegisterDO> list = safetyEntryCompanyRegisterDOMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数据新增
     *
     * @param data
     */
    public void save(SafetyEntryCompanyRegisterDO data) {
        safetyEntryCompanyRegisterDOMapper.insert(data);
    }

    /**
     * 数据删除
     * @param id 数据行ID
     */
    public int deleteById(Integer id) {
        return safetyEntryCompanyRegisterDOMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据对接人员工号删除备案负责人信息
     * @param empNo 对接人员工号
     */
    public void deleteByEmpNo(String empNo) {
        SafetyEntryCompanyRegisterDOExample example = new SafetyEntryCompanyRegisterDOExample();
        SafetyEntryCompanyRegisterDOExample.Criteria criteria = example.createCriteria();
        criteria.andButtJointCodeEqualTo(empNo);
        safetyEntryCompanyRegisterDOMapper.deleteByExample(example);
    }

    /**
     * 获取备案人员逾期提醒的数据
     * 获取符合提醒的数据,对接人中 1.备案过期提醒字段为是的，2.配置类型为人员的， 备案人员信息中：1.状态为审批通过的， 2.准入时间-截止时间为今天的
     * @return 数据集
     */
    public List<EntryOverdueTaskVO> selectEntryOverdueTask() {
        return safetyEntryCompanyRegisterDOMapper.selectEntryOverdueTask();
    }
}
