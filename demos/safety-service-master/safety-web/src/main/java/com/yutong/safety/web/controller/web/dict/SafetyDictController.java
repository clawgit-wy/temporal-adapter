package com.yutong.safety.web.controller.web.dict;

import com.yutong.safety.core.db.entity.dict.SafetyDictItemDO;
import com.yutong.safety.core.db.entity.dict.SafetyDictSupperDO;
import com.yutong.safety.core.handler.SafetyDictItemHandler;
import com.yutong.safety.core.handler.dict.SafetyDictSupperHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-8-8 11:19
 * @description SafetyDictController
 */
@Api(tags = "字典")
@RestController
@RequestMapping("/web/dict/safetyDict")
public class SafetyDictController {

    @Autowired
    SafetyDictItemHandler safetyDictItemHandler;

    @Autowired
    SafetyDictSupperHandler safetyDictSupperHandler;

    //=====================================字典父项设置begin===================================
    @ApiOperation(value = "分页查询字典父项")
    @RequestMapping("/supper/getSupperList")
    public Object getSupperList(SafetyDictSupperDO en){
        return safetyDictSupperHandler.getList(en);
    }

    @ApiOperation(value = "保存或更新父编码")
    @RequestMapping("/supper/saveOrUpdateSupper")
    public Object saveOrUpdateSupper(SafetyDictSupperDO en, @RequestHeader("X-Access-Token") String tocken){
        safetyDictSupperHandler.saveOrUpdateSupper(en,tocken);
        return null;
    }

    @ApiOperation(value = "逻辑删除父编码")
    @RequestMapping("/supper/deleteSupper")
    public Object deleteSupper(Integer id, @RequestHeader("X-Access-Token") String tocken){
        safetyDictSupperHandler.deleteSupper(id,tocken);
        return null;
    }

    //=====================================字典父项设置end===================================

    //=====================================字典子项设置begin===================================
    @ApiOperation(value = "分页查询字典子项")
    @RequestMapping("/supper/getDictList")
    public Object getDictList(SafetyDictItemDO en){
        return safetyDictItemHandler.getList(en);
    }

    @ApiOperation(value = "保存或更新子编码")
    @RequestMapping("/supper/saveOrUpdateDict")
    public Object saveOrUpdateDict(SafetyDictItemDO en, @RequestHeader("X-Access-Token") String tocken){
        safetyDictItemHandler.saveOrUpdateSupper(en,tocken);
        return null;
    }

    @ApiOperation(value = "逻辑删除子编码")
    @RequestMapping("/supper/deleteDict")
    public Object deleteDict(Integer id, @RequestHeader("X-Access-Token") String tocken){
        safetyDictItemHandler.deleteSupper(id,tocken);
        return null;
    }
    //=====================================字典子项设置end===================================

}
