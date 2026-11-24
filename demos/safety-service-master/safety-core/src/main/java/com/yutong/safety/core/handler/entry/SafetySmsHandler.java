package com.yutong.safety.core.handler.entry;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.RedisUtil;
import com.yutong.safety.common.utils.ValidationUtil;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntrySendSmsParams;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.captcha.SafetyCaptchaHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

import static com.yutong.safety.common.constants.Constants.REDIS_ENTRY_PHONE_PRE;
import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.SMS_CODE_TYPE_LOGIN;

@Component
public class SafetySmsHandler {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;


    @Resource
    private SafetyCaptchaHandler safetyCaptchaHandler;

    @Value("${iam.sendMsgSms.smsTemplateCode.region}")
    private String sendTemplateCodeRegion;

    @Value("${iam.sendMsgSms.smsTemplateCode.login}")
    private String sendTemplateCodeLogin;

    private static final String SEND_LIMIT_PREFIX = "send_limit";
    // 默认在特定时间内最多发送5次验证码
    private static final int DEFAULT_MAX_SEND_TIMES = 5;
    // 过期时间
    private static final long EXPIR_TIME = 300;


    public boolean sendVerificationCode(SafetyEntrySendSmsParams params) {
        // 二次验证
        Map<String, Object> result = safetyCaptchaHandler.captchaSecondCheck(params.getToken());
        Boolean success = (Boolean) result.get("success");
        String message = (String) result.get("msg");
        if (Boolean.FALSE.equals(success)) {
            String errorMsg = message != null ? message : "未知错误";
            throw new BizException(ResponseEnum.FAIL.getCode(), "验证码二次检查失败: " + errorMsg);
        }

        String mobile = params.getPhone();
        String type = params.getType();
        // 检查手机号格式是否合法
        if (!ValidationUtil.isMobileValid(mobile)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "无效的手机号码格式");
        }

        // 检查发送频率限制
        String sendLimitKey = REDIS_ENTRY_PHONE_PRE + SEND_LIMIT_PREFIX + type + mobile;
        Object count = redisUtil.get(sendLimitKey);
        if (count == null) {
            count = "";
        }
        long currentCount = 0L;
        if (isNumeric(count.toString())) {
            currentCount = Long.parseLong(count.toString());
        }

        int maxSendTimes = getMaxSendTimes();
        if (currentCount >= maxSendTimes) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "短信发送频率过高，请稍后再试");
        }

        // 生成随机6位数字验证码
        String verificationCode = generateRandomVerificationCode();

        String templateCode = sendTemplateCodeRegion;
        if (SMS_CODE_TYPE_LOGIN.equals(type)) {
            templateCode = sendTemplateCodeLogin;
        }

        try {
            // 发送短信验证码
            safetyBaseHandler.sendCodeSms(mobile, verificationCode, templateCode);

            // 将验证码存储到Redis并设置过期时间
            String cacheKey = REDIS_ENTRY_PHONE_PRE + type + mobile;
            redisUtil.set(cacheKey, verificationCode, EXPIR_TIME);

            // 更新发送次数
            redisUtil.incr(sendLimitKey, 1);
            // 设置发送次数的过期时间（比如1分钟后重置）
            redisUtil.expire(sendLimitKey, 1800);

            return true;
        } catch (Exception e) {
            LogUtils.error("发送短信验证码失败，手机号码: " + mobile + ", 验证码: " + verificationCode, e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "短信发送失败，请稍后再试");
        }
    }


    public boolean verifyCode(String mobile, String verificationCode, String type) {
        String cacheKey = REDIS_ENTRY_PHONE_PRE + type + mobile;

        String storedVerificationCode = String.valueOf(redisUtil.get(cacheKey));
        // 检查验证码是否存在
        if (StringUtils.isBlank(storedVerificationCode)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "验证码已过期或未发送，请重新获取验证码");
        }

        // 比较验证码
        if (!verificationCode.equals(storedVerificationCode)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "验证码不正确，请检查后重新输入");
        }

        // 验证码正确，删除缓存中的验证码，防止重复使用
        redisUtil.del(cacheKey);

        return true;
    }

    private String generateRandomVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private int getMaxSendTimes() {
        // 从配置文件或数据库获取最大允许的发送次数
        // 这里简化为固定值，实际开发中应从配置中读取
        return DEFAULT_MAX_SEND_TIMES;
    }

    public static boolean isNumeric(String str) {
        try {
            NumberFormat formatter = NumberFormat.getInstance();
            // 解析字符串为数字，如果成功则不抛出异常
            formatter.parse(str);
            return true;
        } catch (ParseException e) {
            // 如果解析失败，则捕获异常并返回false
            return false;
        }
    }
}
