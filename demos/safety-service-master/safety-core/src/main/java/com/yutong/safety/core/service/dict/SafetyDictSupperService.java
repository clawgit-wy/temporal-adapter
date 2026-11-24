package com.yutong.safety.core.service.dict;

import cn.hutool.core.util.ObjectUtil;
import com.yutong.safety.common.constants.DeleteEnum;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.dict.SafetyDictSupperDO;
import com.yutong.safety.core.db.entity.dict.SafetyDictSupperDOExample;
import com.yutong.safety.core.db.mapper.dict.SafetyDictSupperDOMapper;
import com.yutong.safety.core.handler.dict.SafetyDictSupperHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-8-8 11:23
 * @description SafetyDictSupperService
 */
@Service
@Slf4j
public class SafetyDictSupperService implements SafetyDictSupperHandler {

    @Autowired
    private SafetyDictSupperDOMapper safetyDictSupperDOMapper;

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageVO getList(SafetyDictSupperDO en) {
        PageVO<SafetyDictSupperDO> pageVO = new PageVO<>();
        SafetyDictSupperDOExample example = new SafetyDictSupperDOExample();
        SafetyDictSupperDOExample.Criteria criteria = example.createCriteria();

        criteria.andDelEqualTo(DeleteEnum.UN_DELETE.getCode());

        if(ObjectUtil.isNotEmpty(en.getSupperCode())){
            criteria.andSupperCodeEqualTo(en.getSupperCode());
        }

        if(ObjectUtil.isNotEmpty(en.getSupperName())){
            criteria.andSupperNameLike(en.getSupperName());
        }

        pageVO.setTotal(safetyDictSupperDOMapper.countByExample(example));

        example.setOrderByClause(" supper_order asc ");

        int pageNum = en.getPageNum();
        int pageSize = en.getPageSize();
        example.setLimit(pageSize);
        example.setOffset((pageNum-1)*pageSize);

        pageVO.setDataList(safetyDictSupperDOMapper.selectByExample(example));

        return pageVO;
    }

    @Override
    public void saveOrUpdateSupper(SafetyDictSupperDO en,String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        en.setUpdateUser(tokenUser.get("realName"));
        en.setUpdateTime(new Date());

        if(ObjectUtil.isNotEmpty(en.getId())){
            safetyDictSupperDOMapper.updateByPrimaryKeySelective(en);
        }else {
            safetyDictSupperDOMapper.insert(en);
        }
    }

    @Override
    public void deleteSupper(Integer id, String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        String realName = tokenUser.get("realName");

        safetyDictSupperDOMapper.deleteSupper(id,realName);
    }

    public SafetyDictSupperDO selectInfo(SafetyDictSupperDO record) {
        SafetyDictSupperDOExample example = new SafetyDictSupperDOExample();
        SafetyDictSupperDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(record.getSupperCode())) {
            criteria.andSupperCodeEqualTo(record.getSupperCode());
        }
        criteria.andDelEqualTo(DeleteEnum.UN_DELETE.getCode());
        List<SafetyDictSupperDO> safetyDictSupperDOS = safetyDictSupperDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(safetyDictSupperDOS)) {
            record.setUpdateTime(new Date());
            record.setDel(DeleteEnum.UN_DELETE.getCode());
            safetyDictSupperDOMapper.insert(record);
            return record;
        }
        return safetyDictSupperDOS.get(0);
    }
}
