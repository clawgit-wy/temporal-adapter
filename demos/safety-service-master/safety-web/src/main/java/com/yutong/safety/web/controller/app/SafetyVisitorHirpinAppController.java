package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.AppletVisitorParam;
import com.yutong.safety.common.param.app.VisitorAppListParam;
import com.yutong.safety.common.param.app.VisitorHairpinListAddParam;
import com.yutong.safety.common.param.web.VisitorHairpinAddParam;
import com.yutong.safety.common.param.web.VisitorHairpinParam;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationApprovalParam;
import com.yutong.safety.core.db.entity.visitor.vo.SafetyVisitorIdCardVO;
import com.yutong.safety.core.db.entity.visitor.vo.SafetyVisitorIntervieweeVO;
import com.yutong.safety.core.handler.SafetyVisitorHairpinHandler;
import com.yutong.safety.core.handler.visitor.SafetyVisitorAuthorizationApprovalHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "访客")
@RestController
@RequestMapping("/app/safety/visitor")
public class SafetyVisitorHirpinAppController {
    @Autowired
    private SafetyVisitorHairpinHandler safetyVisitorHairpinHandler;
    @Resource
    private SafetyVisitorAuthorizationApprovalHandler safetyVisitorAuthorizationApprovalHandler;
    /**
     * 新建访客添加功能
     * @param param
     * @return
     */
    @RequestMapping("/add")
    public Object add(VisitorHairpinAddParam param) {
        return safetyVisitorHairpinHandler.add(param, "");
    }

    /**
     * 新建访客添加多人方法功能
     * @return
     */
    @RequestMapping("/addList")
    public Object addList(@RequestBody VisitorHairpinListAddParam param) {
        return safetyVisitorHairpinHandler.addList(param);
    }

    /**
     * 根据身份证号获取数据列表
     * @param param
     * @return
     */
    @RequestMapping("/list")
    public Object list(VisitorAppListParam param) {

        return safetyVisitorHairpinHandler.listByWxPhone(param);
    }

    @RequestMapping("/updateByEipId")
    public Object updateEipStatusById(VisitorHairpinParam param) {
        return safetyVisitorHairpinHandler.updateEipStatusById(param);
    }

    /**
     * 根据id获取详情数据以及二维码数据
     * @param param
     * @return
     */
    @RequestMapping("/detail")
    public Object detailList(AppletVisitorParam param) {
        return safetyVisitorHairpinHandler.detailById(param);
    }

    @RequestMapping("/updateStates")
    public Object updateStates() {
        return safetyVisitorHairpinHandler.updateStates();
    }

    /**
     * 初始化加载数据接口
     * @param wxPhone
     * @return
     */
    @RequestMapping("/init")
    public Object initVisitor(String wxPhone) {
        return safetyVisitorHairpinHandler.initVisitor(wxPhone);
    }

    /**
     * 根据eip的流程id检查人员是否到访
     * @param param
     * @return
     */
    @RequestMapping("/checkStateByEipId")
    public Object checkStateByEipId(VisitorHairpinParam param) {
        return safetyVisitorHairpinHandler.checkStateByEipId(param);
    }

    @RequestMapping("/getHistoryByIdCard")
    @ApiOperation("根据身份证号查询历史记录")
    public Object getHistoryByIdCard(String phoneNum,Integer pageSize, Integer pageNum) {
        return safetyVisitorHairpinHandler.getHistoryByIdCard(phoneNum,pageSize,pageNum);
    }

    @RequestMapping("/getVisitorAuthorizationForEip")
    @ApiOperation("根据授权人信息获取被授权人信息")
    public VO getVisitorAuthorizationForEip(@RequestBody SafetyVisitorIntervieweeVO vo) {
        return safetyVisitorAuthorizationApprovalHandler.getVisitorAuthorizationForEip(vo.getWorkNo());
    }

    @ApiOperation(value = "审批流程")
    @RequestMapping(value = "/approvalProcess", method = {RequestMethod.POST})
    public VO approvalProcess(@RequestBody SafetyVisitorAuthorizationApprovalParam info) {
        try {
            return safetyVisitorAuthorizationApprovalHandler.approvalProcess(info);
        } catch (BizException e) {
            return VoUtil.fail(e.getCode(), e.getMsg());
        }
    }

    @ApiOperation(value = "流程详情页")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.GET})
    public VO getDetail(Integer processId) {
        return safetyVisitorAuthorizationApprovalHandler.getDetail(processId);
    }

    @ApiOperation(value = "校验被访人是否为主管及上级-eip")
    @RequestMapping(value = "/checkInterviewee", method = {RequestMethod.GET, RequestMethod.POST})
    public VO checkInterviewee(@RequestBody SafetyVisitorIntervieweeVO vo) {
        return safetyVisitorAuthorizationApprovalHandler.checkInterviewee(vo.getWorkNo());
    }

    @ApiOperation(value = "校验被访人是否为主管及上级-app")
    @RequestMapping(value = "/checkIntervieweeByNo", method = {RequestMethod.GET, RequestMethod.POST})
    public VO checkIntervieweeByNo(String workNo) {
        return safetyVisitorAuthorizationApprovalHandler.checkInterviewee(workNo);
    }

    @ApiOperation(value = "身份证照片-校验")
    @RequestMapping(value = "/checkIdCardPhoto", method = {RequestMethod.POST})
    public VO checkIdCardPhoto(SafetyVisitorIdCardVO visitorIdCardVO) {
        return safetyVisitorAuthorizationApprovalHandler.checkIdCardPhoto(visitorIdCardVO);
    }
}
