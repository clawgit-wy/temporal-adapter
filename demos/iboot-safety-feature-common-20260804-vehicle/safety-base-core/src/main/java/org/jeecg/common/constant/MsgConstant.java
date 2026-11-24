package org.jeecg.common.constant;

/**
 * @description:
 * @author: liuky
 * @createDate: 2023/7/8
 * @version: 1.0
 */
public class MsgConstant {

    public static String ENCRYPT_FIXED_STRING = "00000000";


    /**
     * 发送kafka消息-宇信通发送消息队列
     */
    public static final String MSG_YXT_SEND_TOPIC = "sendMsgYxt-out-0";

    /**
     * 发送rocket消息-宇信通发送消息队列
     */
    public static final String MSG_YXT_ROCKET_SEND_TOPIC = "sendMsgYxtRocket-out-0";

    /**
     * 发送rocket消息-宇信通发送消息队列
     */
    public static final String MSG_YXT_LOG_SEND_TOPIC = "sendMsgYxtLog-out-0";

    /**
     * 发送kafka消息-email发送消息队列
     */
    public static final String MSG_EMAIL_SEND_TOPIC = "sendMsgEmail-out-0";
    /**
     * 发送kafka消息-email发送消息队列
     */
    public static final String MSG_EMAIL_LOG_SEND_TOPIC = "sendMsgEmailLog-out-0";

    /**
     * 发送kafka消息-sms短信发送消息队列
     */
    public static final String MSG_SMS_SEND_TOPIC = "sendMsgSms-out-0";

    /**
     * 发送kafka消息-sms短信发送记录保存
     */
    public static final String MSG_SMS_LOG_TOPIC = "sendMsgSmsLog-out-0";

    /**
     * 发送kafka消息-sms短信错误状态通知
     */
    public static final String MSG_SMS_ERROR_NOTITY = "sendMsgSmsErrorNotity-out-0";



}
