package com.yutong.safety.web.controller.app.entry;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants;
import com.yutong.safety.core.db.entity.entry.param.EntryConfigHistoryQueryParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryConfigAddParams;
import com.yutong.safety.core.handler.entry.SafetyEntryConfigHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@Api(tags = "外来人员入厂备案-entry入厂备案管理")
@RestController
@RequestMapping("/app/entry/safety/config/")
public class SafetyEntryConfigAppController {

    @Resource
    private SafetyEntryConfigHandler safetyEntryConfigHandler;

    /**
     * 校验登录用户是否在后台黑名单
     * @return
     */
    @ApiOperation(value = "根据手机号获取生效的备案信息")
    @RequestMapping(value = "getEntryConfigByPhone", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEntryConfigByPhone(String phone){
        if(StringUtils.isBlank(phone)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"手机号不能为空");
        }
        return safetyEntryConfigHandler.getEntryConfigByPhone(phone);
    }

    /**
     * 校验登录用户是否在后台黑名单
     * @return
     */
    @ApiOperation(value = "根据手机号获取历史对接人")
    @RequestMapping(value = "getDockersByPhone", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getDockersByPhone(String phone){
        if(StringUtils.isBlank(phone)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"手机号不能为空");
        }
        String beginDate = DateUtil.format(DateUtils.addMonths(new Date(),-3),"yyyy-MM-dd");
        return safetyEntryConfigHandler.getDockersByPhone(phone,beginDate);
    }

    /**
     * 校验登录用户是否在后台黑名单
     * @return
     */
    @ApiOperation(value = "根据手机号获取历史备案记录")
    @RequestMapping(value = "getConfigHistoryByPhone", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getConfigHistoryByPhone(EntryConfigHistoryQueryParams params){
        String phone = params.getPhone();
        if(params==null || StringUtils.isBlank(phone)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"手机号不能为空");
        }
        String beginDate = DateUtil.format(DateUtils.addMonths(new Date(),-3),"yyyy-MM-dd");
        params.setCreateTime(beginDate);
        //只查询自己提交的历史备案记录
        params.setCreateByNo(phone);
        params.setPhone("");
        return safetyEntryConfigHandler.getConfigHistoryByPhone(params);
    }

    @ApiOperation(value = "新增备案")
    @RequestMapping(value = "saveConfig",method = {RequestMethod.POST})
    public Object saveConfig(@RequestBody SafetyEntryConfigAddParams enterFactoryConfig) {
        enterFactoryConfig.setDelFlag(0);
        enterFactoryConfig.setStatus(SafetyEntryConstants.ENTRY_CONFIG_STATUS_WAIT);
        enterFactoryConfig.setModifyFlag(true); //更新注册信息
        enterFactoryConfig.setSubmitPhone(enterFactoryConfig.getCreateBy());
        String result = safetyEntryConfigHandler.saveConfig(enterFactoryConfig, true);
        if (!"".equals(result)){
            throw new BizException(ResponseEnum.FAIL.getCode(),result);
        }
        return result;
    }
}
