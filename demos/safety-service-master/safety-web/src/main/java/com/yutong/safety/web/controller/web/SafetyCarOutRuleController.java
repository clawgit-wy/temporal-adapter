package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CarOutRuleAddParam;
import com.yutong.safety.common.param.web.CarOutRuleListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyCarOutRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwxiexj
 * @since 2024-04-02 09:51
 */
@Api(tags = "私家车出厂检查规则管理")
@RestController
@RequestMapping("/web/safety/carOutRule")
public class SafetyCarOutRuleController {

    @Autowired
    private SafetyCarOutRuleHandler safetyCarOutRuleHandler;


    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(CarOutRuleListParam param) {
        return safetyCarOutRuleHandler.pageList(param);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新接口")
    @RequestMapping("/add")
    public Object add(CarOutRuleAddParam param) {
        try{
            safetyCarOutRuleHandler.add(param);
        }catch (BizException ex){
            throw new BizException(ex.getCode(), ex.getMsg());
        }catch (Exception e){
            LogUtils.error("",e);
        }
        return null;
    }

    /**
     * 数据添加接口
     * @param ruleId
     * @return
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping("/deleteById")
    public Object deleteById(String ruleId) {
        if(StringUtils.isEmpty(ruleId)){
            throw new BizException("999", "规则ID不能为空");
        }
        try{
            safetyCarOutRuleHandler.deleteById(ruleId);
        }catch (BizException e){
            throw new BizException(e.getCode(), e.getMsg());
        }catch (Exception e){
            LogUtils.error("移除配置异常",e);
        }
        return null;
    }

    @ApiOperation("全量下发个人以及岗位的车辆规则")
    @RequestMapping("/downAll")
    public Object downAll() {
        return new VO(ResponseEnum.SUCCESS.getCode(), "执行成功", safetyCarOutRuleHandler.downAll());
    }
}
