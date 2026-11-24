package com.yutong.safety.web.controller.app.enter;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDockDTO;
import com.yutong.safety.core.handler.SafetyCarInFactoryHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterAuthHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterCheckHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "安保入厂-车辆备案数据校验")
@RestController
@RequestMapping("/app/enter/check/")
public class SafetyEnterCheckAppController {
    @Autowired
    private SafetyEnterCheckHandler enterCheckHandler;
    @Autowired
    private SafetyEnterAuthHandler enterAuthHandler;
    @Autowired
    private SafetyCarInFactoryHandler safetyCarInFactoryHandler;

    /**
     * 校验登录用户是否在后台黑名单
     * @return
     */
    @ApiOperation(value = "校验登录用户是否在后台黑名单")
    @RequestMapping(value = "checkUserInBlack", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkUserInBlack(String dockCode,String phoneNo) {
        return enterCheckHandler.checkUserInBlack(dockCode, phoneNo);
    }
    /**
     * 校验接口人信息
     * @return
     */
    @ApiOperation(value = "校验接口人信息")
    @RequestMapping(value = "checkDockInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkDockInfo(@RequestBody SafetyEnterFilingDockDTO dockDTO) {
        List<SafetyEnterFilingDock> dockList = dockDTO.getList();
        enterCheckHandler.checkDockInfo(dockList);
        return new VO(ResponseEnum.SUCCESS.getCode(), "校验通过", dockList);
    }
    /**
     * 校验车牌号是否存在，用于再次备案
     * @return
     */
    @ApiOperation(value = "校验车牌号是否存在，用于再次备案")
    @RequestMapping(value = "checkCarNo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkCarNo(String carNo) {
        int has = enterCheckHandler.checkCarNo(carNo);
        return new VO(ResponseEnum.SUCCESS.getCode(), "校验通过", has);
    }
    /**
     * 校验是否备案
     * */
    @ApiOperation(value = "校验是否有备案通行信息")
    @RequestMapping(value = "checkRecord", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkRecord(String phone){
        return enterCheckHandler.checkRecord(phone);
    }

    /**
     * 获取车辆入厂信息
     * @param plateNo 车牌号
     * @return 入参记录
     */
    @ApiOperation(value = "判断车辆是否进厂")
    @RequestMapping(value = "getCarInFactory", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getCarInFactory(String plateNo){
        return safetyCarInFactoryHandler.getInvehicleInfo(plateNo);
    }

    @ApiOperation(value = "查询可选择的备案厂区")
    @RequestMapping(value = "selectAuthFactory", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectAuthFactory(String empNo, String filingId) {
        List rt = enterAuthHandler.selectAuthFactory(null, empNo, filingId);
        return rt;
    }

    @ApiOperation(value = "展示登记详情")
    @RequestMapping(value = "showRegisterInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object showRegisterInfo(String phone) {
        if(StringUtils.isBlank(phone)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"入参手机号不能为空");
        }
        return enterCheckHandler.showRegisterInfo(phone);
    }

    /**
     * 校验接口人信息
     * @return
     */
    @ApiOperation(value = "校验接口人信息-外来人员入厂")
    @RequestMapping(value = "checkDockInfoForEntry", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkDockInfoForEntry(@RequestBody SafetyEnterFilingDockDTO dockDTO) {
        List<SafetyEnterFilingDock> list = dockDTO.getList();
        enterCheckHandler.checkDockInfoForNew(list,true);
        return new VO(ResponseEnum.SUCCESS.getCode(), "校验通过", list);
    }
}
