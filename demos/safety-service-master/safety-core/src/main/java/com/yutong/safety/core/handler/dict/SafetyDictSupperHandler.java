package com.yutong.safety.core.handler.dict;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.dict.SafetyDictSupperDO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-8-8 11:22
 * @description SafetyDictSupperHandler
 */
public interface SafetyDictSupperHandler {

    //分页查询
    PageVO getList(SafetyDictSupperDO en);

    //保存或更新父编码
    void saveOrUpdateSupper(SafetyDictSupperDO en,String tocken);

    //删除父编码
    void deleteSupper(Integer id,String tocken);
}
