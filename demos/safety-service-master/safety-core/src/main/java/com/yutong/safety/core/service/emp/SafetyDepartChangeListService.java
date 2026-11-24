package com.yutong.safety.core.service.emp;

import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDOExample;
import com.yutong.safety.core.db.entity.emp.param.DepartChangeListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.emp.vo.DepartChangeVO;
import com.yutong.safety.core.db.mapper.emp.SafetyDepartChangeListDOBizMapper;
import com.yutong.safety.core.db.mapper.emp.SafetyDepartChangeListDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


@Service
public class SafetyDepartChangeListService {
    @Autowired
    SafetyDepartChangeListDOMapper safetyDepartChangeListDOMapper;

    @Autowired
    SafetyDepartChangeListDOBizMapper safetyDepartChangeListDOBizMapper;

    public List<SafetyDepartChangeListDO> selectList(DepartChangeListParam param){
        param.setPageFlag(false);
        SafetyDepartChangeListDOExample example = formatExample(param);
        return safetyDepartChangeListDOMapper.selectByExample(example);
    }

    public List<SafetyDepartChangeListDO> selectPageList(DepartChangeListParam param){
        param.setPageFlag(true);
        SafetyDepartChangeListDOExample example = formatExample(param);
        return safetyDepartChangeListDOMapper.selectByExample(example);
    }

    private SafetyDepartChangeListDOExample formatExample(DepartChangeListParam param){
        SafetyDepartChangeListDOExample example = new SafetyDepartChangeListDOExample();
        SafetyDepartChangeListDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(param.getUpdateTimeBegin())){
            criteria.andCreateTimeGreaterThanOrEqualTo(param.getUpdateTimeBegin());
        }
        if(StringUtils.isNotBlank(param.getUpdateTimeEnd())){
            criteria.andUpdateTimeLessThanOrEqualTo(param.getUpdateTimeEnd());
        }
        if(StringUtils.isNotBlank(param.getChangeType())){
            criteria.andChangeTypeEqualTo(param.getChangeType());
        }
        if(StringUtils.isNotBlank(param.getEmployeeCode())){
            criteria.andEmployeeCodeEqualTo(param.getEmployeeCode());
        }
        if(param.getPageFlag()){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
            if(StringUtils.isNotBlank(param.getOrderByClause())){
                example.setOrderByClause(param.getOrderByClause());
            }
        }
        return example;
    }

    public void save(SafetyDepartChangeListDO en) {
        safetyDepartChangeListDOMapper.insertSelective(en);
    }

    public int saveOrUpdate(SafetyDepartChangeListDO en){
        List<SafetyDepartChangeListDO> list = new ArrayList<>();
        list.add(en);
        return safetyDepartChangeListDOBizMapper.saveOrUpdate(list);
    }

    public List<SafetyDepartChangeListDO> getList(SafetyDepartChangeListDO en) {
        SafetyDepartChangeListDOExample example = new SafetyDepartChangeListDOExample();
        SafetyDepartChangeListDOExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isBlank(en.getCreateTime())) {
            criteria.andCreateTimeGreaterThan(DateUtils.format(DateUtils.addDays(new Date(),-1), 1) + " 00:00:00");
            criteria.andCreateTimeLessThan(DateUtils.format(DateUtils.addDays(new Date(),-1), 1) + " 23:59:59");
        }

        if (!StringUtils.isBlank(en.getChangeType())) {
            criteria.andChangeTypeEqualTo(en.getChangeType());
        }
        example.setOrderByClause(" UPDATE_TIME desc");
        return safetyDepartChangeListDOMapper.selectByExample(example);
    }

    public List<String> getChangeEmpCodeList(SafetyDepartChangeListDO en){
        return safetyDepartChangeListDOBizMapper.getChangeEmpCodeList(en);
    }

    public List<SafetyDepartChangeListDO> selectChangList(DepartChangeListParam listParam){
        return safetyDepartChangeListDOBizMapper.selectChangList(listParam);
    }


    public List<DepartChangeVO> selectChangEmpList(DepartChangeListParam listParam){
        return safetyDepartChangeListDOBizMapper.selectChangEmpList(listParam);
    }

    public int updateByIdList(List<Integer> idList,SafetyDepartChangeListDO record){
        SafetyDepartChangeListDOExample example = new SafetyDepartChangeListDOExample();
        SafetyDepartChangeListDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(idList);
        return safetyDepartChangeListDOMapper.updateByExampleSelective(record,example);
    }

    public List<String> selectEmpNoList(String beginTime, String endTime){
        return safetyDepartChangeListDOBizMapper.selectEmpNoList(beginTime, endTime);
    }
}
