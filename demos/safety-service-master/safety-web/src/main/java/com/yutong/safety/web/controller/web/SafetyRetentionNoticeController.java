package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.RetentionNoticeListParam;
import com.yutong.safety.common.param.web.RetentionNoticeSaveParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyRetentionNoticeHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwxiexj
 * @since 2024-04-10 17:40
 */
@Api(tags = "滞留车辆管理-通知管理")
@RestController
@RequestMapping("/web/safety/retention/notice")
public class SafetyRetentionNoticeController {

    @Autowired
    private SafetyRetentionNoticeHandler safetyRetentionNoticeHandler;

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    @ApiOperation(value = "更新通知接口")
    @RequestMapping("/add")
    public Object add(RetentionNoticeSaveParam param) {
        try{
            safetyRetentionNoticeHandler.saveNotice(param);
        }catch (BizException ex){
            throw new BizException(ex.getCode(), ex.getMsg());
        }catch (Exception e){
            LogUtils.error("",e);
            throw new BizException("999",e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(RetentionNoticeListParam param) {
        return safetyRetentionNoticeHandler.getNoticeList(param);
    }

    /**
     * 数据添加接口
     * @param noticeId
     * @return
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping("/deleteById")
    public Object deleteById(Integer noticeId) {
        if(noticeId == null || noticeId<0){
            throw new BizException("999", "规则ID不能为空");
        }
        try{
            safetyRetentionNoticeHandler.deleteById(noticeId);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("移除配置异常",e);
            throw new BizException("999",e.getMessage());
        }
        return null;
    }
}
