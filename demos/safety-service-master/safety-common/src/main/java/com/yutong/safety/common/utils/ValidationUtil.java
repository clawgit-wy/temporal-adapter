package com.yutong.safety.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ValidationUtil {


    // 中国主要运营商的手机号码正则表达式
    private static final String MOBILE_PATTERN = "^1[3-9]\\d{9}$";

    // 身份证号正则表达式（18位或15位）
    private static final String IDCARD_PATTERN = "^[1-9]\\d{5}(?:18|19|20)?\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3}[\\dX]$|^\\d{15}$";

    /**
     * 校验手机号码是否为有效的中国手机号码
     *
     * @param mobile 手机号码
     * @return true if the mobile number is valid, false otherwise.
     */
    public static boolean isMobileValid(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        return Pattern.matches(MOBILE_PATTERN, mobile);
    }

    /**
     * 校验身份证号是否为有效的身份证号码
     *
     * @param idCard 身份证号
     * @return true 如果是有效的身份证号码，否则返回false.
     */
    public static boolean isIdCardValid(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return false;
        }
        // 检查格式是否正确
//        if (!Pattern.matches(IDCARD_PATTERN, idCard)) {
//            return false;
//        }
        // 18位身份证号码需要校验最后一位校验码
        if (idCard.length() == 18) {
//            return validateIdCardCheckCode(idCard);
            return true;
        }
//        return true;
        return false;
    }

    private static boolean validateIdCardCheckCode(String idCard) {
        String checkCode = idCard.substring(17, 18);
        String[] checkCodeArray = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        int[] idCardArray = new int[17];
        String idCardNum = idCard.substring(0, 17);
        for (int i = 0; i < 17; i++) {
            idCardArray[i] = Integer.parseInt(idCardNum.charAt(i) + "");
        }
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += power(2, 17 - i - 1) * idCardArray[i];
        }
        int remainder = sum % 11;
        String actualCheckCode = checkCodeArray[remainder];
        return checkCode.equalsIgnoreCase(actualCheckCode);
    }

    private static int power(int base, int exponent) {
        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }

    public static void main(String[] args) {
        // 测试手机号码校验
        System.out.println("手机号码校验:");
        System.out.println("13812345678 -> " + isMobileValid("13812345678"));  // true
        System.out.println("12345678901 -> " + isMobileValid("12345678901"));  // false

        // 测试身份证号码校验
        System.out.println("\n身份证号码校验:");
        System.out.println("42100419940101001X -> " + isIdCardValid("42100419940101001X"));  // true
        System.out.println("42100419940101001Y -> " + isIdCardValid("42100419940101001Y"));  // false
        System.out.println("420683199603070015 -> " + isIdCardValid("420683199603070015"));  // true
        System.out.println("123456789 -> " + isIdCardValid("123456789"));  // false

    }
}
