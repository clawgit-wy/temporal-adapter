package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.build.param.BuildConfirmAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildConfirmBatchParam;
import com.yutong.safety.core.db.entity.build.param.BuildLongConfirmListParam;
import com.yutong.safety.core.handler.build.SafetyBuildDepartConfirmHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "楼宇门禁-组织变更门禁确认")
@RestController
@RequestMapping("/web/build/safety/departConfirm")
public class SafetyBuildDepartConfirmController {

    @Resource
    private SafetyBuildDepartConfirmHandler safetyBuildDepartConfirmHandler;

    @ApiOperation("部门变动列表-分页")
    @RequestMapping(value = "/getDepartPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getDepartPageList(BuildLongConfirmListParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        try {
            return safetyBuildDepartConfirmHandler.selectDepartPageList(param, token);
        }catch (Exception e){
            LogUtils.error("分页查询异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"分页查询异常");
        }
    }

    @ApiOperation("一键处理门禁授权-部门")
    @RequestMapping(value = "/batchDealBuild", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchDealBuild(@RequestBody BuildConfirmBatchParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(param.getDealType()) || CollectionUtils.isEmpty(param.getConfirmList())){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请确认入参是否正确");
        }
        try {
            return safetyBuildDepartConfirmHandler.batchDealBuild(param.getDealType(),param.getConfirmList(), token);
        }catch (Exception e){
            LogUtils.error("一键处理门禁授权异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"一键处理门禁授权异常");
        }
    }

    @ApiOperation("一键处理门禁授权-部门")
    @RequestMapping(value = "/dealBuild", method = {RequestMethod.GET,RequestMethod.POST})
    public Object dealBuild(BuildConfirmAddParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(param.getChangeId()) || (StringUtils.isBlank(param.getCloseIdStr()) && StringUtils.isBlank(param.getKeepIdStr()))){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请确认入参是否正确");
        }
        try {
            return safetyBuildDepartConfirmHandler.dealBuild(param, token);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            LogUtils.error("一键处理门禁授权异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"一键处理门禁授权异常");
        }
    }

    @ApiOperation("一键处理白名单-部门")
    @RequestMapping(value = "/batchDealWhite", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchDealWhite(String dealType,String idStr, String reason,@RequestHeader("X-Access-Token") String token){
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isBlank(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(dealType) && StringUtils.isBlank(idStr)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请选择要添加的数据！");
        }
        try {
            return safetyBuildDepartConfirmHandler.batchDealWhite(dealType,idStr,reason,token);
        }catch (Exception e){
            LogUtils.error("批量处理门禁异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"批量处理门禁异常");
        }
    }

    @ApiOperation("跨部门保密负责人确认-确认")
    @RequestMapping(value = "/batchAcrossConfirm", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchCrocessConfirm(String dealType,String idStr, @RequestHeader("X-Access-Token") String token){
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isBlank(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        if(StringUtils.isBlank(dealType) && StringUtils.isBlank(idStr)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"请选择要确认的数据！");
        }
        try {
            return safetyBuildDepartConfirmHandler.batchCrocessConfirm(dealType,idStr,token);
        }catch (Exception e){
            LogUtils.error("批量确认门禁权限异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"批量确认门禁权限异常");
        }
    }

    @ApiOperation("跨部门保密负责人确认列表-分页")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(BuildLongConfirmListParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        try {
            return safetyBuildDepartConfirmHandler.selectPageList(param, token);
        }catch (Exception e){
            LogUtils.error("分页查询异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"分页查询异常");
        }
    }

}
