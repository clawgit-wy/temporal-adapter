package com.yutong.safety.web.controller.web.entry;

import com.yutong.safety.common.vo.*;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.core.db.entity.SafetyWebBaseDelete;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigExample;
import com.yutong.safety.core.db.entity.entry.param.*;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterAuthHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryConfigHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryExternalNetworkHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryImPortFailHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "外来人员入厂-WEB备案人员信息")
@RestController
@RequestMapping("/web/safety/entry/entryConfig")
public class SafetyEntryConfigController extends AbstractSafetyWebBaseController<SafetyEntryConfig, SafetyEntryConfigExample, SafetyEntryConfigQueryParams, SafetyEntryConfigAddParams> {

    @Resource
    private SafetyEntryConfigHandler safetyEntryConfigHandler;
    @Resource
    private SafetyEntryImPortFailHandler safetyEntryImPortFailHandler;
    @Resource
    private SafetyEntryExternalNetworkHandler safetyEntryExternalNetworkHandler;
    @Resource
    private SafetyEnterAuthHandler safetyEnterAuthHandler;
    @Override
    public AbstractSafetyWebBaseHandler<SafetyEntryConfig, SafetyEntryConfigExample, SafetyEntryConfigQueryParams, SafetyEntryConfigAddParams> getHandler() {
        return safetyEntryConfigHandler;
    }
    @ApiOperation(value = "获取字段外来人员入厂类型")
    @RequestMapping(value = "getDictEnterType", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getDictEnterType(){
        return safetyEntryConfigHandler.getDictEnterTypeList();
    }
    /**
     * 新增备案
     * @param params 入参
     * @param token 登录许可
     * @return 返回状态
     */
    @ApiOperation(value = "保存或更新备案")
    @RequestMapping(value = "save", method = {RequestMethod.GET,RequestMethod.POST})
    public Object save(SafetyEntryConfigAddParams params,
                       @RequestHeader("X-Access-Token") String token){
        //解析token
        safetyEntryConfigHandler.setCreateInfo(token,params);
        params.setStatus(Constants.FLOW_STATUS_SUCCESS);
        String result = safetyEntryConfigHandler.saveConfig(params, false);
        if (!"".equals(result)){
            throw new BizException(ResponseEnum.FAIL.getCode(),result);
        }
        return result;
    }

    @ApiOperation(value = "批量编辑")
    @RequestMapping(value = "batchUpdate", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchUpdate(SafetyEntryConfigBatchParams params,
                              @RequestHeader("X-Access-Token") String token) {

        return safetyEntryConfigHandler.batchUpdate(params,token);
    }

    @ApiOperation(value = "批量审核")
    @RequestMapping(value = "batchAudit", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchAudit(SafetyEntryConfigBatchParams params,
                             @RequestHeader("X-Access-Token") String token) {
        return safetyEntryConfigHandler.batchAudit(params,token);
    }

    @ApiOperation(value = "批量续期")
    @RequestMapping(value = "batchExtension", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchExtension(SafetyEntryConfigBatchParams params,
                            @RequestHeader("X-Access-Token") String token) {
        return safetyEntryConfigHandler.batchExtension(params,token);
    }


    @ApiOperation(value = "获取详情")
    @RequestMapping(value = "showInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object showInfo(Integer id,
                                 @RequestHeader("X-Access-Token") String token) {
        if(id == null){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"请指定要编辑的信息");
        }
        return safetyEntryConfigHandler.getInfoById(id,token);
    }

    /**
     * 失败信息删除
     */
    @ApiOperation(value = "失败信息删除")
    @RequestMapping(value = "/deleteFailList", method = {RequestMethod.POST})
    public Object deleteFailList(@RequestBody SafetyEntryImportFailQueryParams params) {
        return safetyEntryImPortFailHandler.deleteFailList(params);
    }

    /**
     * 失败信息删除
     */
    @ApiOperation(value = "失败信息导出")
    @RequestMapping(value = "/exportFailData", method = {RequestMethod.GET})
    public Object exportFailData(HttpServletResponse response,SafetyEntryImportFailQueryParams params) {
        try {
            safetyEntryImPortFailHandler.export(response, params, null);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出失败信息列表异常");
        }
        return null;
    }

    /**
     * 数据导入操作
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping(value = "/importData", method = {RequestMethod.POST})
    public VO importData(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryConfigHandler.importData(param, token);
    }

    /**
     * 数据导入操作
     */
    @ApiOperation(value = "获取二级黑名单清单")
    @RequestMapping(value = "/getBlankForVersionNo", method = {RequestMethod.POST})
    public Object getBlankForVersionNo(String versionNo,@RequestHeader("X-Access-Token") String token){
        return safetyEntryImPortFailHandler.getDtoListByVersionNo(versionNo);
    }

    /**
     * 注销权限
     */
    @ApiOperation(value = "注销权限")
    @RequestMapping(value = "/logoutPermission", method = {RequestMethod.GET,RequestMethod.POST})
    public Object logoutPermission(@RequestBody SafetyWebBaseDelete param, @RequestHeader("X-Access-Token")String token){
        Integer id = param.getId();
        String reason = param.getReason();
        return safetyEntryExternalNetworkHandler.logoutPermission(id,reason, token, true);
    }

    /**
     * 入厂方式配置
     */
    @ApiOperation(value = "入厂方式配置")
    @RequestMapping(value = "/updateEntryMethod", method = {RequestMethod.GET, RequestMethod.POST})
    public VO updateEntryMethod(@RequestBody SafetyEntryMethodParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryConfigHandler.updateEntryMethod(param, token);
    }

    /**
     * 入厂方式获取
     */
    @ApiOperation(value = "入厂方式获取")
    @RequestMapping(value = "/getEntryMethod", method = {RequestMethod.GET, RequestMethod.POST})
    public VO getEntryMethod(@RequestHeader("X-Access-Token") String token) {
        return safetyEntryConfigHandler.getEntryMethod(token);
    }
}
