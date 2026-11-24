package com.yutong.safety.common.utils;

import com.alibaba.excel.util.StringUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author gwcuiyx
 * @date 2024-5-6 16:53
 */
public class PinYinUtils {
    /**
     * 提取每个字符串的首字母（大写）
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {
        if (StringUtils.isEmpty(str) && "".equals(str.trim())) {
            return "";
        }
        String convert = "";
        for (int i = 0; i < str.length(); i++) {
            char word = str.charAt(i);
            //提取字符串首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert.toUpperCase();
    }

    /**
     * 获取字符串指定字符的最后一个截取串
     * @param str
     * @return
     */
    public static String getAfterLastComma(String str,String var) {
        int lastCommaIndex = str.lastIndexOf(var);
        if (lastCommaIndex == -1) {
            return "";
        }
        return str.substring(lastCommaIndex + 1);
    }


    /**
     * 字符串去重拼接
     */
    public static String stringFormat(String value1, String value2) {
        if (StringUtils.isBlank(value1)) {
            return value2;
        }
        if (StringUtils.isBlank(value2)) {
            return value1;
        }
        List<String> list1 = Arrays.asList(value1.split(","));
        List<String> list2 = Arrays.asList(value2.split(","));
        Set<String> result = new HashSet<>();
        result.addAll(list1);
        result.addAll(list2);
        return String.join(",", result);
    }

    /**
     * 转二进制字符
     * @param binaryStr
     * @return
     */
    public static Set<String> formatBinaryStr(String binaryStr){
        Set<String> positions = new HashSet<>();
        // 验证输入合法性
        if (!binaryStr.matches("[01]+")) {
            LogUtils.info("输入必须为二进制字符串");
        }
        if(StringUtils.isNotBlank(binaryStr)){
            for(int i=0;i<binaryStr.length();i++){
                if (binaryStr.charAt(i) == '1') {
                    int lat = i+1;
                    positions.add(String.valueOf(lat));
                }
            }
        }
        return positions;
    }

    /**
     * 把字符串放在List中，并去重
     * @param value 字符串
     * @param list List数组
     */
    public static void checkStringDistinct(String value, List<String> list) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(value) && !list.contains(value)) {
            list.add(value);
        }
    }

    /**
     * List转字符串， 并限制只需要8个值
     * @param list List数组
     * @return 返回字符串值
     */
    public static String listByEightToString(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        if (list.size() <= 8) {
            return String.join(",", list);
        }
        return String.join(",", list.subList(0, 7));
    }

    /**
     * 升序排列字符串
     * @param input
     * @return
     */
    public static String sortNumberString(String input) {
        if (input == null || input.isEmpty())  {
            return input;
        }

        // 分割原始字符串
        String[] parts = input.split(",");

        // 自定义排序：按数值升序排列（保留原始字符串格式）
        Arrays.sort(parts,  new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    // 按数值比较（处理前导零和负数）
                    return Integer.compare(Integer.parseInt(s1),  Integer.parseInt(s2));
                } catch (NumberFormatException e) {
                    // 非数字内容按字符串原始顺序
                    return s1.compareTo(s2);
                }
            }
        });

        // 重新拼接字符串
        return String.join(",",  parts);
    }

    public static void main(String[] args) {

        String str = "154,121,12,125,1257";
        int length = 5; // 需要补位到的长度
        //String formattedStr = PinYinUtils.getPinYinHeadChar(str);
        System.out.println(sortNumberString(str));  // 输出：orange
        /*
        System.out.println(getAfterLastComma("test",","));  // 输出：空字符串
        System.out.println(getAfterLastComma("a,b,c,d",","));  // 输出：d
        System.out.println(getAfterLastComma("end,",","));  // 输出：空字符串
         */
    }
}
