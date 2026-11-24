package com.yutong.safety.core.db.entity.system.param;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

@Data
public class BlackCheckParam extends WebPageParam {
    private String idCardNo; //身份证号
    private String name; //名称
    private String phone; //手机号
    private String cardNo;  //员工卡号
    private String enterBeginTime; //入厂开始时间
    private String etnerEndTime;   //入厂截止时间(前端已对接)
    private String dockNo; //对接人员工号
    private String openId;
    private String photo;
}
