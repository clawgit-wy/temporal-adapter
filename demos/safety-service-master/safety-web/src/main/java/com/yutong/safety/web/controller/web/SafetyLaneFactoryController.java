package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LaneFactoryListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.handler.SafetyLaneFactoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwxiexj
 * @since 2024-04-11 14:36
 */
@Api(tags = "厂区道路与厂区关系")
@RestController
@RequestMapping("/web/safety/retention/laneFactory")
public class SafetyLaneFactoryController {
    @Autowired
    private SafetyLaneFactoryHandler safetyLaneFactoryHandler;

    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/pageList")
    public Object pageList(LaneFactoryListParam param) {
        PageVO pageVO = new PageVO();
        try {
            return safetyLaneFactoryHandler.getPageList(param);
        }catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("厂区道路与厂区关系查询列表异常", e);
            throw new BizException("999",e.getMessage());
        }
    }

    /**
     *
     * @param param
     * @return
     */
    @ApiOperation(value = "数据列表查询接口")
    @RequestMapping("/list")
    public Object list(LaneFactoryListParam param) {
        PageVO pageVO = new PageVO();
        try {
            return safetyLaneFactoryHandler.getList(param);
        }catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("厂区道路与厂区关系异常", e);
            throw new BizException("999",e.getMessage());
        }
    }

    /**
     *
     * @param param
     * @return
     */
    @ApiOperation(value = "数据列表查询接口")
    @RequestMapping("/getFactoryList")
    public Object getFactoryList(LaneFactoryListParam param) {
        try {
            return safetyLaneFactoryHandler.getFactoryList(param);
        }catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("获取工厂列表", e);
            throw new BizException("999",e.getMessage());
        }
    }
}
