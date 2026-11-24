package com.yutong.safety.core.service.entry;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDOExample;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailQueryParams;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryBlankProcessDetailDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SafetyEntryBlankProcessDetailService extends AbstractSafetyWebBaseService<SafetyEntryBlankProcessDetailDO, SafetyEntryBlankProcessDetailDOExample, SafetyEntryBlankProcessDetailQueryParams> {

    @Resource
    private SafetyEntryBlankProcessDetailDOMapper safetyEntryBlankProcessDetailDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyEntryBlankProcessDetailDO, SafetyEntryBlankProcessDetailDOExample> getMapper() {
        return safetyEntryBlankProcessDetailDOMapper;
    }

    @Override
    protected void formatExample(SafetyEntryBlankProcessDetailDOExample example, SafetyEntryBlankProcessDetailQueryParams param, boolean pageType) {
        example.setOrderByClause(" id desc");
        SafetyEntryBlankProcessDetailDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(param.getCompanyName())) {
            criteria.andCompanyNameEqualTo(param.getCompanyName());
        }
        if (StringUtils.isNotBlank(param.getCardNo())) {
            criteria.andCardNoEqualTo(param.getCardNo());
        }
        if (StringUtils.isNotBlank(param.getPhone())) {
            criteria.andPhoneEqualTo(param.getPhone());
        }
        if (StringUtils.isNotBlank(param.getCreateTime())) {
            Date date = DateUtils.parse(param.getCreateTime(), 2);
            criteria.andCreateTimeGreaterThanOrEqualTo(date);
        }
        if (param.getProcessId() != null) {
            criteria.andProcessIdEqualTo(param.getProcessId());
        }
        if (pageType) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        if (StringUtils.isNotBlank(param.getApplyStatus())) {
            criteria.andApplyStatusEqualTo(param.getApplyStatus());
        }
        if (StringUtils.isNotBlank(param.getNotApplyStatus())) {
            criteria.andApplyStatusNotEqualTo(param.getNotApplyStatus());
        }
        if(StringUtils.isNotBlank(param.getContainBeginTime())){
            criteria.andEnterStartTimeLessThanOrEqualTo(param.getContainBeginTime());
        }
        if(StringUtils.isNotBlank(param.getContainEndTime())){
            criteria.andEnterEndTimeGreaterThanOrEqualTo(param.getContainEndTime());
        }
        //过滤掉已失效的备案信息
        if(param.getCheckNow() != null && param.getCheckNow()){
            criteria.andEnterEndTimeGreaterThan(DateUtils.format(new Date(),2));
        }
        //过滤掉已失效的备案信息
        if(param.getNullRecordType() != null){
            if(param.getNullRecordType()){
                criteria.andRecordTypeCodeIsNull();
            }else{
                criteria.andRecordTypeCodeIsNotNull();
            }
        }
        if (StringUtils.isNotBlank(param.getCompetitorDirector())) {
            criteria.andCompetitorDirectorEqualTo(param.getCompetitorDirector());
        }
    }

    @Override
    protected void batchDelete(SafetyEntryBlankProcessDetailDOExample example, List<String> idList) {

    }

    @Override
    protected void isExist(SafetyEntryBlankProcessDetailDO data, SafetyEntryBlankProcessDetailDOExample example) {

    }

    /**
     * 分页和导出数据
     *
     * @param param 入参
     * @return 结果集
     */
    public List<SafetyEntryBlankProcessDetailDO> selectProcessAndDetail(SafetyEntryBlankProcessDetailQueryParams param) {
        return safetyEntryBlankProcessDetailDOMapper.selectProcessAndDetail(param);
    }
    /**
     * 分页和导出数据的数量
     *
     * @param param 入参
     * @return 总数
     */
    public long selectProcessAndDetailCount(SafetyEntryBlankProcessDetailQueryParams param) {
        return safetyEntryBlankProcessDetailDOMapper.selectProcessAndDetailCount(param);
    }

    /**
     * 批量入库
     * @param detailList 数据明细
     */
    public void batchSave(List<SafetyEntryBlankProcessDetailDO> detailList) {
        safetyEntryBlankProcessDetailDOMapper.batchSave(detailList);
    }

    /**
     * 批量修改
     * @param detailList 数据明细
     */
    public void batchUpdateStatus(List<SafetyEntryBlankProcessDetailDO> detailList) {
        safetyEntryBlankProcessDetailDOMapper.batchUpdate(detailList);
    }
}
