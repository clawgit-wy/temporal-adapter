package com.yutong.safety.common.constants;

/**
 * 外部人员入场入场消息提醒
 */
public enum SafetyYxtMsgEnum {

    VISITOR_EHR_MSG("访客-EHR", "禁止入厂-核验失败", "{workEmpName}老师您好，人力黑名单人员 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《访客预约流程》拜访已拦截，请知悉。\n" +
            "注：人力黑名单内人员原则上禁止入厂，如有疑问请联系0371-85338000（内线8000）获取黑名单详细信息。"),

    VISITOR_COMPETITOR_MSG("访客-非友商", "禁止入厂-验证失败", "{workEmpName}老师您好，集团非友商 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《访客预约流程》拜访已拦截，如确因工作需要必须入厂，请提交《受限人员入厂申请流程》 {anEipTodoUrl} ，如有疑问请联系责任人{competitorInfo}，或拨打司法保卫室24小时服务热线0371-66718119。\n" +
            "注：①依据《集团非友商管理办法》，纳入非友商清单的单位/个人，原则上不允许入厂。如确因工作需要必须入厂时，需由责任部门审批后方可放行，必要情况下责任部门需陪同。\n" +
            "②《受限人员入厂申请流程》发布后请另行提交访客预约流程获取人员进厂权限。"),

    VISITOR_RESIGN_MSG("访客-离职人员", "禁止入厂-校验失败", "{workEmpName}老师您好，离职人员 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《访客预约流程》拜访已拦截，如确因工作需要必须入厂，请提交《受限人员入厂申请流程》 {anEipTodoUrl} ；如在供应商处任职，请提交《离职人员在供应商处任职审批流程》 {eipFlowLink} ，如有疑问可拨打司法保卫室24小时服务热线0371-66718119。\n" +
            "注：①《受限人员入厂申请流程》发布后请另行提交访客预约流程获取人员进厂权限。\n" +
            "②《离职人员在供应商处任职审批流程》发布后将取消人员进出限制。"),


    FILING_EHR_MSG("备案-EHR", "禁止入厂-核验失败", "{workEmpName}老师您好，人力黑名单人员 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《人员入厂备案申请》备案入厂已拦截{supplier}。请知悉。\n" +
            "注：人力黑名单内人员原则上禁止入厂，如有疑问请联系0371-85338000（内线8000）获取黑名单详细信息。"),

    FILING_COMPETITOR_MSG("备案-非友商", "禁止入厂-验证失败", "{workEmpName}老师您好，集团非友商 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《人员入厂备案申请》备案入厂已拦截{supplier}。如确因工作需要必须入厂，请提交《受限人员入厂申请流程》 {anEipTodoUrl} ，如有疑问请联系责任人{competitorInfo}，或拨打司法保卫室24小时服务热线0371-66718119。\n" +
            "注：依据《集团非友商管理办法》，纳入非友商清单的单位/个人，原则上不允许入厂。如确因工作需要必须入厂时，需由责任部门审批后方可放行，必要情况下责任部门需陪同。"),

    FILING_RESIGN_MSG("备案-离职人员", "禁止入厂-校验失败", "{workEmpName}老师您好，离职人员 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《人员入厂备案申请》备案入厂已拦截{supplier}。如确因工作需要临时入厂，请提交《受限人员入厂申请流程》 {anEipTodoUrl} ；如在供应商处任职，请提交《离职人员在供应商处任职审批流程》 {eipFlowLink} ，如有疑问可拨打司法保卫室24小时服务热线0371-66718119。\n" +
            "注：《离职人员在供应商处任职审批流程》发布后将取消人员进出限制。"),


    LOGISTICS_EHR_MSG("物流车-EHR", "禁止入厂-核验失败", "{workEmpName}老师您好，人力黑名单人员 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《物流车入厂申请》入厂已拦截，请知悉。\n" +
            "注：人力黑名单内人员原则上禁止入厂，如有疑问请联系0371-85338000（内线8000）获取黑名单详细信息。"),

    LOGISTICS_COMPETITOR_MSG("物流车-非友商", "禁止入厂-验证失败", "{workEmpName}老师您好，集团非友商 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《物流车入厂申请》入厂已拦截，如确因工作需要必须入厂，请提交《受限人员入厂申请流程》 {anEipTodoUrl} ，如有疑问请联系责任人{competitorInfo}，或拨打司法保卫室24小时服务热线0371-66718119。\n" +
            "注：①依据《集团非友商管理办法》，纳入非友商清单的单位/个人，原则上不允许入厂。如确因工作需要必须入厂时，需由责任部门审批后方可放行，必要情况下责任部门需陪同。\n" +
            "②《受限人员入厂申请流程》发布后请另行提交物流车入厂申请获取人员、车辆进厂权限。"),

    LOGISTICS_RESIGN_MSG("物流车-离职人员", "禁止入厂-校验失败", "{workEmpName}老师您好，离职人员 {blankEmpName} （身份证号：{cardNo}，电话：{phone}）于{currentDate}尝试通过《物流车入厂申请》入厂已拦截，如确因工作需要临时入厂，请提交《受限人员入厂申请流程》 {anEipTodoUrl} ；如在供应商处任职，请提交《离职人员在供应商处任职审批流程》 {eipFlowLink} ，如有疑问可拨打司法保卫室24小时服务热线0371-66718119。\n" +
            "注：①《受限人员入厂申请流程》发布后请另行提交物流车入厂申请获取人员、车辆进厂权限。\n" +
            "②《离职人员在供应商处任职审批流程》发布后将取消人员、车辆进出限制。"),

    NONE("无", "", "无"),
    ;

    private String describe;
    private String errorMessage;
    private String msg;

    SafetyYxtMsgEnum(String describe, String errorMessage, String msg) {
        this.describe = describe;
        this.errorMessage = errorMessage;
        this.msg = msg;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
