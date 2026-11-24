package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.FieldManageListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyFieldManageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Api(tags = "字段管理-app")
@RestController
@RequestMapping("/app/safety/field")
public class SafetyFieldManageAppController {
    @Autowired
    private SafetyFieldManageHandler safetyFieldManageHandler;

    @RequestMapping("/visitList")
    public Object getVisitList() {
        return safetyFieldManageHandler.getVisitList();
    }

    /**
     * 异步-获取字段树
     * @param param
     * @return
     */
    @ApiOperation(value = "字段树")
    @RequestMapping(value="/fieldChildTree",method ={RequestMethod.GET,RequestMethod.POST})
    public Object fieldChildTree(FieldManageListParam param){
        try{
            String filedType = param.getFieldType();
            Integer parentId = param.getParentId();
            if(StringUtils.isEmpty(filedType) && parentId == null){
                return null;
            }
            return safetyFieldManageHandler.getSubTree(param);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("获取字段管理列表异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取字段管理列表异常");
        }
    }

    /**
     * 获取联动数据
     * @return
     */
    @ApiOperation(value = "获取联动数据列表")
    @RequestMapping(value="/selectLinkageList",method ={RequestMethod.GET,RequestMethod.POST})
    public Object selectLinkageList() {
        return safetyFieldManageHandler.selectLinkageList();
    }

    /**
     * 获取联动数据
     * @return
     */
    @ApiOperation(value = "获取用户工厂权限")
    @RequestMapping(value="/selectPowerFactoryList",method ={RequestMethod.GET,RequestMethod.POST})
    public Object selectPowerFactoryList(String workNo){
        try{
            if(StringUtils.isEmpty(workNo)){
                return null;
            }
            return safetyFieldManageHandler.selectPowerFactoryList(workNo);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("获取字段管理列表异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取字段管理列表异常");
        }
    }


    @ApiOperation(value = "查询用户的发卡厂区权限")
    @GetMapping("/getCardFactoryList")
    public Object getCardFactoryList(String workNo){
        try{
            if(StringUtils.isEmpty(workNo)){
                return null;
            }
            return safetyFieldManageHandler.selectCardFactoryList(workNo);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("查询用户的发卡厂区权限异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询用户的发卡厂区权限异常");
        }
    }
}
