package com.yutong.safety.core.handler;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.dict.SafetyDictItemDO;
import com.yutong.safety.core.db.entity.result.DictItemRt;

import java.util.List;
import java.util.Map;

public interface SafetyDictItemHandler {

    List<DictItemRt> selectDictItemListBySupperCode(String supperCode);
    List<DictItemRt> selectDictItemListBySupperId(Integer supperId);
    Map selectDictItemMapBySupperCode(String supperCode);
    Map selectDictItemMapBySupperId(Integer supperId);

    //保存或更新
    void saveOrUpdateSupper(SafetyDictItemDO en,String tocken);

    //删除
    void deleteSupper(Integer id,String tocken);

    //分页查询
    PageVO getList(SafetyDictItemDO en);

}
