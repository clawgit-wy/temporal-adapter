package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LaneFactoryListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyCarInFactoryHandler;
import com.yutong.safety.core.handler.SafetyLaneFactoryHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 滞留车辆通知管理定时任务
 *
 * @author gwxiexj
 * @since 2024-04-12 08:54
 */
@RestController
@RequestMapping("/app/safety/retention")
public class SafetyRetentionTaskController {
    @Autowired
    private SafetyCarInFactoryHandler safetyCarInFactoryHandler;

    @Autowired
    private SafetyLaneFactoryHandler safetyLaneFactoryHandler;

    @ApiOperation(value = "滞留车辆管理-同步在厂车辆")
    @RequestMapping("/taskCarInFactory")
    public Object taskCarInFactory() {
        try {
            return safetyCarInFactoryHandler.taskCarInFactory();
        }catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("同步在厂车辆异常", e);
            throw new BizException("999",e.getMessage());
        }
    }

    @ApiOperation(value = "滞留车辆管理-获取厂区道路与厂区关系列表")
    @RequestMapping("/getLaneFactoryList")
    public Object getLaneFactoryList(){
        try {
            return safetyLaneFactoryHandler.getList(new LaneFactoryListParam());
        }catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("厂区道路与厂区关系异常", e);
            throw new BizException("999",e.getMessage());
        }
    }

    @ApiOperation(value = "滞留车辆管理-滞留车消息推送")
    @RequestMapping("/taskSendRetentionNotice")
    public Object taskSendRetentionNotice(){
        try {
            safetyCarInFactoryHandler.dealRetentionCar();
            safetyCarInFactoryHandler.sendRetentionNoticeTask();
            return null;
        }catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("滞留逻辑处理异常", e);
            throw new BizException("999",e.getMessage());
        }
    }
}
