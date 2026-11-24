package com.yutong.safety.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-5-11 9:29
 */
public class SensitiveUtils {
    /**
     * 脱敏姓名
     * @param name
     * @return
     */
    public static String maskName(String name) {
        if (StringUtils.isEmpty(name) || name.length() < 2) {
            return name;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        if (name.length() > 2) {
            for (int i = 1; i < name.length() - 1; i++) {
                sb.append("*");
            }
            sb.append(name.charAt(name.length() - 1));
        } else {
            sb.append("*");
        }

        return sb.toString();
    }

    /**
     * 脱敏手机号
     * @param phoneNumber
     * @return
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return phoneNumber;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(phoneNumber.substring(0, 3)).append("****").append(phoneNumber.substring(7));
        return sb.toString();
    }

    /**
     * 脱敏身份证
     * @param cardId
     * @return
     */
    public static String maskCardId(String cardId) {
        if (StringUtils.isEmpty(cardId) || cardId.length() < 18) {
            return cardId;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(cardId.substring(0, 6)).append("********").append(cardId.substring(14));
        return sb.toString();
    }

    /**
     * 日志数据脱敏
     * @param log
     * @return
     */
    public static String maskLogData(String log) {
        if (StringUtils.isEmpty(log)) {
            return log;
        }
        // 此处只处理访客下发类型，其他的暂不处理
        List<String> list = Arrays.asList(log.split("]:"));
        String msg = log;
        String prefix = "";
        if (list.size() == 2) {
            prefix = list.get(0);
            msg = list.get(1);
        }

        String[] logs = msg.split(";");
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            sb.append(prefix).append("]:");
        }
        for (String s : logs) {
            if (s.length() == 11) {
                sb.append(maskPhoneNumber(s)).append(";");
            } else if (s.length() == 18) {
                sb.append(maskCardId(s)).append(";");
            } else {
                sb.append(maskName(s)).append(";");
            }
        }
        return sb.toString().substring(0, sb.toString().length()-1);
    }

}
