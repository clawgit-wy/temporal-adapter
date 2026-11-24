package com.yutong.safety.web.controller.app.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.EnterFactoryRecord;
import com.yutong.safety.core.db.entity.enter.EnterFactoryRecordDTO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.handler.enter.SafetyEnterRegisterHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 已登记人员信息
 * */
@Api(tags = "安保入厂-车辆备案APP端")
@RestController
@RequestMapping("/app/enter/register/")
public class SafetyEnterRegisterAppController {
    @Autowired
    private SafetyEnterRegisterHandler enterRegisterHandler;

    /**
     * 入厂人员信息登记
     * @param list 登记记录
     * @param sessionKey 前端秘钥
     * @return 返回状态
     */
    @RequestMapping(value = "insertEnterRegister", method = {RequestMethod.GET,RequestMethod.POST})
    public VO insertEnterRegister(@RequestBody EnterFactoryRecordDTO list,
                                  @RequestHeader("sessionKey") String sessionKey) {
        List<EnterFactoryRecord> rlist = list.getList();
        enterRegisterHandler.insertEnterRegister(rlist,sessionKey);
        return new VO(ResponseEnum.SUCCESS.getCode(), "添加成功");
    }

    @ApiOperation(value = "根据手机号验证备案人")
    @RequestMapping(value = "checkEnterRegister", method = {RequestMethod.GET,RequestMethod.POST})
    public VO checkEnterRegister(String phone, String fdName) {
        SafetyEnterRegisterRt rt = enterRegisterHandler.selectEnterRegisterByPhone(phone,fdName);
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", rt);
    }

    /*根据手机号获取人员注册信息*/
    @ApiOperation(value = "根据手机号获取人员注册信息")
    @RequestMapping(value = "selectEnterRegisterByPhone", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterRegisterByPhone(String phone){
        return enterRegisterHandler.selectEnterRegisterByPhone(phone);
    }
}
