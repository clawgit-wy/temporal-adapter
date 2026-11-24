package com.yutong.safety.core.service;

import cn.hutool.core.util.ObjectUtil;
import com.yutong.safety.common.constants.DeleteEnum;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.dict.SafetyDictItemDO;
import com.yutong.safety.core.db.entity.dict.SafetyDictItemDOExample;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.db.mapper.SafetyDictItemMapper;
import com.yutong.safety.core.handler.SafetyDictItemHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SafetyDictItemService implements SafetyDictItemHandler {
    @Autowired
    private SafetyDictItemMapper dictItemMapper;

    @Override
    public List<DictItemRt> selectDictItemListBySupperCode(String supperCode) {
        List<DictItemRt> list = dictItemMapper.selectDictItemListBySupperCode(supperCode);
        return list;
    }

    @Override
    public List<DictItemRt> selectDictItemListBySupperId(Integer supperId) {
        List<DictItemRt> list = dictItemMapper.selectDictItemListBySupperId(supperId);
        return list;
    }

    @Override
    public Map selectDictItemMapBySupperCode(String supperCode) {
        Map map = new HashMap();
        List<DictItemRt> list = dictItemMapper.selectDictItemListBySupperCode(supperCode);
        for(int i=0;i<list.size();i++){
            map.put(list.get(i).getItemCode(), list.get(i).getItemName());
        }
        return map;
    }

    @Override
    public Map selectDictItemMapBySupperId(Integer supperId) {
        Map map = new HashMap();
        List<DictItemRt> list = dictItemMapper.selectDictItemListBySupperId(supperId);
        for(int i=0;i<list.size();i++){
            map.put(list.get(i).getItemCode(), list.get(i).getItemName());
        }
        return map;
    }

    @Override
    public void saveOrUpdateSupper(SafetyDictItemDO en, String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        en.setUpdateUser(tokenUser.get("realName"));
        en.setUpdateTime(new Date());

        if(ObjectUtil.isNotEmpty(en.getId())){
            dictItemMapper.updateByPrimaryKeySelective(en);
        }else {
            dictItemMapper.insert(en);
        }
    }

    @Override
    public void deleteSupper(Integer id, String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        String realName = tokenUser.get("realName");

        dictItemMapper.deleteSupper(id,realName);
    }

    @Override
    public PageVO getList(SafetyDictItemDO en) {
        PageVO<SafetyDictItemDO> pageVO = new PageVO<>();
        SafetyDictItemDOExample example = new SafetyDictItemDOExample();
        SafetyDictItemDOExample.Criteria criteria = example.createCriteria();

        criteria.andIsUseEqualTo(DeleteEnum.USE_STATUS.getCode());

        if(ObjectUtil.isNotEmpty(en.getSupperCode())){
            criteria.andSupperCodeEqualTo(en.getSupperCode());
        }

        if(ObjectUtil.isNotEmpty(en.getItemName())){
            criteria.andItemNameLike(en.getItemName());
        }

        pageVO.setTotal(dictItemMapper.countByExample(example));

        example.setOrderByClause(" item_order asc ");
        int pageNum = en.getPageNum();
        int pageSize = en.getPageSize();
        example.setLimit(pageSize);
        example.setOffset((pageNum-1)*pageSize);

        pageVO.setDataList(dictItemMapper.selectByExample(example));

        return pageVO;
    }

    /**
     * 获取指定supperCode值的数据字典信息
     * @return 返回列表
     */
    public List<SafetyDictItemDO> selectList(String supperCode, String showFlag) {
        SafetyDictItemDOExample example = new SafetyDictItemDOExample();
        SafetyDictItemDOExample.Criteria criteria = example.createCriteria();
        criteria.andSupperCodeEqualTo(supperCode);
        criteria.andIsUseEqualTo(DeleteEnum.USE_STATUS.getCode());
        if (StringUtils.isNotBlank(showFlag)) {
            criteria.andShowFlagEqualTo(showFlag);
        }
        return dictItemMapper.selectByExample(example);
    }

    /**
     * 获取指定supperCode值的数据字典信息
     * @return 返回MAP
     */
    public Map<String, String> selectListToMap(String supperCode, String showFlag) {
        List<SafetyDictItemDO> list = this.selectList(supperCode, showFlag);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().filter(li -> StringUtils.isNotBlank(li.getItemCode()) && StringUtils.isNotBlank(li.getItemName()))
                    .collect(Collectors.toMap(SafetyDictItemDO::getItemCode, SafetyDictItemDO::getItemName));
        }
        return new HashMap<>();
    }

    public void batchSave(List<SafetyDictItemDO> dictItems) {
        dictItemMapper.batchSave(dictItems);
    }
}
