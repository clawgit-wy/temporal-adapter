package org.jeecg.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by jackliu on 17-3-14.
 */
public class NewJsonUtils {
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>(){} ;

    /**
     * 转map
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(String json) {
        return JSON.parseObject(json, MAP_TYPE_REFERENCE) ;
    }

    /**
     * 转List
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseList(String str, Class<T> clazz) {
        return JSON.parseArray(str, clazz) ;
    }

    /**
     * 转String
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if(obj == null) {
            return "" ;
        }
        return JSON.toJSONString(obj) ;
    }

    /**
     * json转Object
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parse(String str, Class<T> clazz) {
        T result = null ;
        if(StringUtils.isNotBlank(str)) {
            result = JSON.parseObject(str, clazz) ;
        }
        return result ;
    }

    /**
     * 将字符串解析为Json
     * @param str
     * @return
     */
    public static Object parse(String str) {
        try {
            return JSON.parse(str) ;
        } catch (Exception e) {
            return str ;
        }
    }

}
