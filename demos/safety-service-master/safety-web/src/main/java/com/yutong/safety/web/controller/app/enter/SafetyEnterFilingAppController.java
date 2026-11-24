package com.yutong.safety.web.controller.app.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.enter.SafetyEnterApprove;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFiling;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.rbody.SafetyEnterFilingBody;
import com.yutong.safety.core.db.entity.enter.rbody.SafetyEnterFilingPerBody;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingInfoRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingPerAppRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingAppSh;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHistoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 车辆备案
 * */
@Api(tags = "安保入厂-车辆备案APP端")
@RestController
@RequestMapping("/app/enter/filing/")
public class SafetyEnterFilingAppController {
    @Autowired
    private SafetyEnterFilingHandler enterFilingHandler;
    @Autowired
    private SafetyEnterFilingHistoryHandler enterFilingHistoryHandler;

    @ApiOperation(value = "入厂填报")
    @RequestMapping(value = "insertEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public VO insertEnterFiling(@RequestBody SafetyEnterFilingBody filingBody) {
        enterFilingHandler.insertEnterFilingApp(filingBody);
        return new VO(ResponseEnum.SUCCESS.getCode(), "添加成功");
    }

    @ApiOperation(value = "查询备案列表")
    @RequestMapping(value = "selectEnterFilingList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterFilingList(@RequestBody SafetyEnterFilingAppSh appSh) {
        return enterFilingHandler.selectEnterFilingListApp(appSh);
    }

    @ApiOperation(value = "查询备案人员列表")
    @RequestMapping(value = "selectEnterFilingPer", method = {RequestMethod.GET,RequestMethod.POST})
    public VO selectEnterFilingPer(String id) {
        SafetyEnterFilingPerAppRt rt = enterFilingHandler.selectEnterFilingPerApp(id);
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", rt);
    }

    @ApiOperation(value = "更新备案人员信息")
    @RequestMapping(value = "updateEnterFilingPer", method = {RequestMethod.GET,RequestMethod.POST})
    public VO updateEnterFilingPer(@RequestBody SafetyEnterFilingPerBody perBody) {
        enterFilingHandler.updateEnterFilingPer(perBody);
        return new VO(ResponseEnum.SUCCESS.getCode(), "更新成功");
    }

    @ApiOperation(value = "查询备案车牌号信息")
    @RequestMapping(value = "selectEnterFilingCar", method = {RequestMethod.GET,RequestMethod.POST})
    public VO selectEnterFilingCar(String carNo) {
        List list = enterFilingHandler.selectEnterFilingCar(carNo);
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", list);
    }

    @ApiOperation(value = "查询备案信息")
    @RequestMapping(value = "selectEnterFilingInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public VO selectEnterFilingInfo(String id) {
        SafetyEnterFilingInfoRt rt = enterFilingHandler.selectEnterFilingInfo(id);
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", JsonUtils.toString(rt));
    }

    @ApiOperation(value = "接口人历史记录")
    @RequestMapping(value = "selectEnterFilingDockHis", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterFilingDockHis(String phone) {
        List<SafetyEnterFilingDock> list = enterFilingHistoryHandler.selectEnterFilingDockHis(phone);
        return VoUtil.successData(list);
    }

    @ApiOperation(value = "更新备案车牌的货物信息")
    @RequestMapping(value = "updateFilingGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateFilingGoods(SafetyEnterFiling enterFiling) {
        enterFilingHandler.updateFilingGoods(enterFiling);
        return new VO(ResponseEnum.SUCCESS.getCode(), "更新成功");
    }

    @ApiOperation(value = "审批")
    @RequestMapping(value = "approveEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public Object approveEnterFiling(@RequestBody SafetyEnterApprove approve) {
        String rt = enterFilingHandler.approveEnterFiling(approve, null);
        if(StringUtils.isNotBlank(rt) && !rt.startsWith("T")){
            return new VO(ResponseEnum.FAIL.getCode(), "审批异常", rt);
        }
        return new VO(ResponseEnum.SUCCESS.getCode(), "操作成功");
    }

    @ApiOperation(value = "根据图片解析驾驶证数据")
    @RequestMapping(value = "drivingLicense", method = {RequestMethod.GET,RequestMethod.POST})
    public Object drivingLicense(String url) {
        Map map = enterFilingHandler.drivingLicense(url);
        return new VO(ResponseEnum.SUCCESS.getCode(), "操作成功", map);
    }

    @ApiOperation(value = "根据身份证号校验手机号是否一致")
    @RequestMapping(value = "validTelNumberByIdCard", method = {RequestMethod.GET,RequestMethod.POST})
    public Object validTelNumberByIdCard(String phone,String idCard){
        return enterFilingHandler.validTelNumberByIdCard(phone,idCard);
    }

    //详见需求：21518
    @ApiOperation(value = "入厂申请填报前信息校验")
    @RequestMapping(value = "verifyBeforeInsert", method = {RequestMethod.GET,RequestMethod.POST})
    public VO verifyBeforeInsert(@RequestBody SafetyEnterFilingBody filingBody) {
        boolean b = enterFilingHandler.verifyBeforeInsert(filingBody);
        return new VO(ResponseEnum.SUCCESS.getCode(), b + "");
    }

    @ApiOperation(value = "入厂申请审批时信息校验")
    @RequestMapping(value = "verifyBeforeApprove", method = {RequestMethod.GET,RequestMethod.POST})
    public VO verifyBeforeApprove(@RequestBody SafetyEnterApprove approve) {
        Map<String, Object> map = enterFilingHandler.verifyBeforeApprove(approve);
        return new VO(ResponseEnum.SUCCESS.getCode(), "", map);
    }

    @ApiOperation(value = "根据手机号获取弹框提示信息")
    @RequestMapping(value = "getAlertMes", method = {RequestMethod.GET,RequestMethod.POST})
    public VO getAlertMes(String phoneNo) {
        Map<String, Object> map = enterFilingHandler.getAlertMes(phoneNo);
        return new VO(ResponseEnum.SUCCESS.getCode(), "", map);
    }

    @ApiOperation(value = "根据手机号设置弹框提示信息不再展示")
    @RequestMapping(value = "setAlertMesHidden", method = {RequestMethod.GET,RequestMethod.POST})
    public VO setAlertMesHidden(String phoneNo, String mesId) {
        enterFilingHandler.setAlertMesHidden(phoneNo,mesId);
        return new VO(ResponseEnum.SUCCESS.getCode(), "设置成功");
    }

    @ApiOperation(value = "获取登记人信息并校验")
    @RequestMapping(value = "getRegisterInfo", method = {RequestMethod.GET})
    public VO getRegisterInfo(String phone) {
        return enterFilingHandler.getRegisterInfo(phone);
    }
}
