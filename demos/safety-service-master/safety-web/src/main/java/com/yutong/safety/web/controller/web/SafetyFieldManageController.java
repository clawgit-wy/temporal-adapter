package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.FieldManageAddParam;
import com.yutong.safety.common.param.web.FieldManageListParam;
import com.yutong.safety.common.param.web.FieldManageMoveParam;
import com.yutong.safety.common.param.web.FieldManageUpdateParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyFieldManageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 *  create gwcuiyx 2023.07.11
 *  字段管理--controller
 */
@Api(tags = "字段管理")
@RestController
@RequestMapping("/web/safety/field")
public class SafetyFieldManageController {

    @Autowired
    private SafetyFieldManageHandler safetyFieldManageHandler;

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据添加接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET,RequestMethod.POST})
    public Object add(FieldManageAddParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyFieldManageHandler.add(param, tocken);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "数据删除接口")
    @RequestMapping(value = "/delete", method = {RequestMethod.GET,RequestMethod.POST})
    public Object delete(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        return safetyFieldManageHandler.delete(id, tocken);
    }

    /**
     * 数据列表操作
     * @return
     */
    @ApiOperation(value = "数据列表接口")
    @RequestMapping(value = "/list", method = {RequestMethod.GET,RequestMethod.POST})
    public Object fieldManageList() {
        return safetyFieldManageHandler.fieldManageList();
    }

    /**
     * 数据移动操作方法
     * @param param
     * @return
     */
    @ApiOperation(value = "数据移动接口")
    @RequestMapping(value = "/move", method = {RequestMethod.GET,RequestMethod.POST})
    public Object moveFieldManage(FieldManageMoveParam param) {
        return safetyFieldManageHandler.moveFieldManage(param);
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新接口")
    @RequestMapping(value = "/update", method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(FieldManageUpdateParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyFieldManageHandler.update(param, tocken);
    }

    /**
     * 获取联动数据
     * @return
     */
    @ApiOperation(value = "获取联动数据列表")
    @RequestMapping("/selectLinkageList")
    public Object selectLinkageList() {
        return safetyFieldManageHandler.selectLinkageList();
    }

    @ApiOperation(value = "获取联动数据列表-带中心站")
    @RequestMapping("/selectLinkageListNew")
    public Object selectLinkageListNew() {
        return safetyFieldManageHandler.selectLinkageListNew();
    }

    @ApiOperation(value = "获取联动数据列表-带中心站—只有工厂名")
    @RequestMapping("/selectLinkageListNewName")
    public Object selectLinkageListNewName() {
        return safetyFieldManageHandler.selectLinkageListNewName();
    }

    /**
     * 异步-获取分类树
     * @param param
     * @return
     */
    @ApiOperation(value = "分类树列表")
    @RequestMapping(value = "/fieldManageTree", method = {RequestMethod.GET,RequestMethod.POST})
    public Object fieldManageTree(FieldManageListParam param){
        try{
            return safetyFieldManageHandler.fieldManageTree(param);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("获取字段管理列表异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取字段管理列表异常"+e.getMessage());
        }
    }

    /**
     * 异步-获取字段树
     * @param param
     * @return
     */
    @ApiOperation(value = "字段树")
    @RequestMapping(value = "/fieldChildTree", method = {RequestMethod.GET,RequestMethod.POST})
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
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取字段管理列表异常"+e.getMessage());
        }
    }

    @ApiOperation(value = "查询子字段")
    @RequestMapping(value = "/fieldChildByFieldType", method = {RequestMethod.GET,RequestMethod.POST})
    public Object fieldChildByFieldType(String fieldType){
        try{
            return safetyFieldManageHandler.fieldChildByFieldType(fieldType);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("获取字段管理列表异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取字段管理列表异常"+e.getMessage());
        }
    }

    @ApiOperation(value = "查询用户的发卡厂区权限")
    @RequestMapping(value = "/getCardFactoryList", method = {RequestMethod.GET,RequestMethod.POST})
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
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询用户的发卡厂区权限异常"+e.getMessage());
        }
    }

    @ApiOperation(value = "根据工厂id查询楼栋")
    @PostMapping(value = "/findBuildList")
    public Object findBuildList(String factoryId){
        return safetyFieldManageHandler.findBuildList(factoryId);
    }

}
