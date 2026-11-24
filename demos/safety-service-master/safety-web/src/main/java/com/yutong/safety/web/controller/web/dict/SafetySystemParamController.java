package com.yutong.safety.web.controller.web.dict;

import com.yutong.safety.common.param.web.sys.SystemParamAddParam;
import com.yutong.safety.common.param.web.sys.SystemParamListParam;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.handler.dict.SafetySystemParamHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "系统参数")
@RestController
@RequestMapping("/web/safety/dict/systemParam")
public class SafetySystemParamController {

    @Autowired
    private SafetySystemParamHandler safetySystemParamHandler;

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/listPage",method = {RequestMethod.GET,RequestMethod.POST})
    public Object listPage(SystemParamListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySystemParamHandler.pageList(param);
    }

    /**
     * 批量删除操作
     * @param idStr ID拼接串
     * @return
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping(value="/batchDelete",method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchDelete(String idStr, @RequestHeader("X-Access-Token") String token) {
        safetySystemParamHandler.deleteById(idStr, token);
        return null;
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value="/add",method = {RequestMethod.GET,RequestMethod.POST})
    public Object add(SystemParamAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySystemParamHandler.saveOrUpdate(param, token);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value="/update",method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(SystemParamAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySystemParamHandler.saveOrUpdate(param, token);
    }

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "不分页数据列表查询接口")
    @RequestMapping(value="/list",method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(SystemParamListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySystemParamHandler.getList(param);
    }

    @ApiOperation(value = "根据编码获取参数")
    @RequestMapping(value = "/getParamByCode",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getParamByCode(String paramCode, @RequestHeader("X-Access-Token") String token){
        if(StringUtils.isBlank(paramCode)){
            return null;
        }
        SystemParamListParam listParam = new SystemParamListParam();
        listParam.setParamCode(paramCode);
        List<SafetySystemParamDO> list = safetySystemParamHandler.getList(listParam);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }
}
