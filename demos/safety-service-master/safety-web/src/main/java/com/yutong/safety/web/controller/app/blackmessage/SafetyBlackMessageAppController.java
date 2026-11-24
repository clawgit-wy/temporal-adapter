package com.yutong.safety.web.controller.app.blackmessage;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.BlackEipListParam;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.blackmessage.SafetyBlackMessageForEipParam;
import com.yutong.safety.core.db.entity.system.param.BlackCheckParam;
import com.yutong.safety.core.handler.SafetyBlackMessageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "黑名单信息管理App")
@RestController
@RequestMapping("/app/safety/black")
public class SafetyBlackMessageAppController {
    @Autowired
    private SafetyBlackMessageHandler safetyBlackMessageHandler;


    @RequestMapping("/check")
    public Object checkMessageByIdCard(String idCardNo, String cardNo) {
        if (StringUtils.isEmpty(idCardNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"身份证号不能为空!");
        }
        return safetyBlackMessageHandler.check(idCardNo, cardNo);
    }

    @RequestMapping(value = "/checkForEnter",method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkForEnter(BlackCheckParam param) {
        String idCardNo = param.getIdCardNo();
        if (StringUtils.isEmpty(param.getPhoto())) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"照片不能为空!");
        }
        if (StringUtils.isEmpty(idCardNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"身份证号不能为空!");
        }
        if (StringUtils.isEmpty(param.getEnterBeginTime()) || StringUtils.isEmpty(param.getEtnerEndTime())) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"入厂起止时间不能为空!");
        }
        if (StringUtils.isEmpty(param.getDockNo())) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"对接人不能为空!");
        }
        return safetyBlackMessageHandler.checkForEnter(param);
    }

    /**
     * 定时任务执行增量或全量数据
     * @param isAll
     * @param dateTime
     * @return
     */
    @RequestMapping("/tasksMessage")
    public Object tasksMessage(Boolean isAll, String dateTime) {
        return safetyBlackMessageHandler.tasksMessage(isAll, dateTime);
    }

    /**
     * 根据身份证号，更新和名单的状态数据
     * @param idCardId
     * @return
     */
    @RequestMapping("/updateState")
    public Object updateStateByCardId(String idCardId) {
        return safetyBlackMessageHandler.updateStateByCardId(idCardId);
    }

    /**
     * 黑名单数据eip流程
     * @param param
     * @return
     */
    @RequestMapping("/departList")
    public Object departList(@RequestBody BlackEipListParam param) {
        return safetyBlackMessageHandler.addDepart(param);
    }

    @RequestMapping("/test")
    public Object test() {
        return safetyBlackMessageHandler.test();
    }

    @ApiOperation("批量查询车辆是否在黑名单中")
    @RequestMapping("/isBlackBatchQuery")
    public Object isBlackBatchQuery(String carNoList) {
        return new VO(ResponseEnum.SUCCESS.getCode(), "", safetyBlackMessageHandler.isBlackBatchQuery(carNoList));
    }

    @ApiOperation(value = "EIP回写特批授权截止日期")
    @RequestMapping(value = "/queryAuthorizationDateEip", method = {RequestMethod.POST})
    public VO queryAuthorizationDateEip(@RequestBody SafetyBlackMessageForEipParam param) {
        return safetyBlackMessageHandler.queryAuthorizationDateEip(param);
    }
}
