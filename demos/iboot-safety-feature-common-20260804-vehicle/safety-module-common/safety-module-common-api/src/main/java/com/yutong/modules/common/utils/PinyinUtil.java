package com.yutong.modules.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        FORMAT.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /**
     * 获取汉字首字母大写
     * @param chinese 汉字字符串
     * @return 首字母串
     */
    public static String getFirstLetterAndSign(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.trim().toCharArray();
        try {
            for (char c : chars) {
                if (c > 128) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        sb.append(pinyinArray[0].charAt(0));
                    }
                } else {
                    sb.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.trim().toCharArray();
        try {
            for (char c : chars) {
                if (c > 128) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        sb.append(pinyinArray[0].charAt(0));
                    }
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getFirstLetterAndInt(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.trim().toCharArray();
        try {
            for (char c : chars) {
                if (Character.isDigit(c)) {
                    sb.append(c);
                } else if (c > 128) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        sb.append(pinyinArray[0].charAt(0));
                    }
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    // 获取单个汉字首字母
    public static String getSingleFirstLetter(char c) {
        return getFirstLetter(String.valueOf(c));
    }

}