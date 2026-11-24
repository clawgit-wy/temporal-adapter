package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CarInFactoryListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.RetentionWhiteAddParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyRetentionWhiteHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwxiexj
 * @since 2024-04-11 17:16
 */
@Api(tags = "滞留车辆通知-白名单管理")
@RestController
@RequestMapping("/web/safety/retention/white")
public class SafetyRetentionWhiteController {

    @Autowired
    private SafetyRetentionWhiteHandler safetyRetentionWhiteHandler;


    /**
     * 数据添加接口
     * @param param
     * @return
     */
    @ApiOperation(value = "更新")
    @RequestMapping("/add")
    public Object add(RetentionWhiteAddParam param) {
        try{
            safetyRetentionWhiteHandler.add(param);
        }catch (BizException ex){
            throw new BizException(ex.getCode(), ex.getMsg());
        }catch (Exception e){
            LogUtils.error("更新通知",e);
            throw new BizException("999",e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "分页数据列表查询")
    @RequestMapping("/list")
    public Object list(CarInFactoryListParam param) {
        return safetyRetentionWhiteHandler.pageList(param);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteById")
    public Object deleteById(String id) {
        if(StringUtils.isEmpty(id)){
            throw new BizException("999", "规则ID不能为空");
        }
        try{
            safetyRetentionWhiteHandler.deleteById(id);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("移除配置异常",e);
            throw new BizException("999",e.getMessage());
        }
        return null;
    }

    /**
     * @param param
     * @return
     */
    @ApiOperation(value = "批量导入")
    @RequestMapping("/import")
    public Object importData(CommonImportParam param) {
        try {
            return safetyRetentionWhiteHandler.importData(param);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("滞留白名单导入异常",e);
            throw new BizException("999",e.getMessage());
        }
    }
}
