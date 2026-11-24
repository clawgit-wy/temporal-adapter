package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.build.param.BuildLongConfirmListParam;
import com.yutong.safety.core.handler.build.SafetyBuildLongConfirmHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "楼宇门禁-长期未进出门禁确认")
@RestController
@RequestMapping("/web/build/safety/longConfirm")
public class SafetyBuildLongConfirmController {

    @Resource
    private SafetyBuildLongConfirmHandler safetyBuildLongConfirmHandler;

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(BuildLongConfirmListParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        try {
            return safetyBuildLongConfirmHandler.selectPageList(param, token);
        }catch (Exception e){
            LogUtils.error("分页查询异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"分页查询异常");
        }
    }

    @ApiOperation("分页查询-个人")
    @RequestMapping(value = "/getOwnerList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getOwnerList(BuildLongConfirmListParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        try {
            return safetyBuildLongConfirmHandler.selectOwnerList(token, param);
        }catch (Exception e){
            LogUtils.error("分页查询异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"分页查询异常");
        }
    }

    @ApiOperation("一键添加白名单")
    @RequestMapping(value = "/batchSaveWhite", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchSaveWhite(String idStr,String reason, @RequestHeader("X-Access-Token") String token){
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isBlank(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(idStr)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请选择要添加的数据！");
        }
        String msg;
        try {
            msg = safetyBuildLongConfirmHandler.batchSaveWhite(idStr,reason, token);
        }catch (Exception e){
            LogUtils.error("一键添加白名单异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"一键添加白名单异常");
        }
        if(!"操作成功".equals(msg)){
            throw new BizException(ResponseEnum.FAIL.getCode(),msg);
        }else{
            return msg;
        }
    }

    @ApiOperation("一键取消门禁")
    @RequestMapping(value = "/batchCloseBuild", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchCloseBuild(String idStr, @RequestHeader("X-Access-Token") String token){
        if(StringUtils.isBlank(idStr)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请指定数据");
        }
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isBlank(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(idStr)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请选择要添加的数据！");
        }
        String msg;
        try {
            msg = safetyBuildLongConfirmHandler.batchDealBuild(idStr,0, token);
        }catch (Exception e){
            LogUtils.error("一键取消门禁异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"一键取消门禁异常");
        }
        if(!"操作成功".equals(msg)){
            throw new BizException(ResponseEnum.FAIL.getCode(),msg);
        }else{
            return msg;
        }
    }

    @ApiOperation("个人界面批量处理门禁")
    @RequestMapping(value = "/batchDealBuild", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchDealBuild(String keepIdStr,String closeIdStr, @RequestHeader("X-Access-Token") String token){
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isBlank(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(keepIdStr) && StringUtils.isBlank(closeIdStr)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请选择要添加的数据！");
        }
        String msg = "";
        try {
            if(StringUtils.isNotBlank(keepIdStr)){
                msg = safetyBuildLongConfirmHandler.batchDealBuild(keepIdStr,1, token);
            }
            if(StringUtils.isNotBlank(closeIdStr)){
                msg = safetyBuildLongConfirmHandler.batchDealBuild(closeIdStr, 0,token);
            }
            // 消除代办
            safetyBuildLongConfirmHandler.delTodo(token);
        }catch (Exception e){
            LogUtils.error("批量处理门禁异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"批量处理门禁异常");
        }
        if(!"操作成功".equals(msg)){
            throw new BizException(ResponseEnum.FAIL.getCode(),msg);
        }else{
            return msg;
        }
    }



}
