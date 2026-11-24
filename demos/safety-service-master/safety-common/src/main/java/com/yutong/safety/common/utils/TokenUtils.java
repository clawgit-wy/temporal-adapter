package com.yutong.safety.common.utils;

import com.yutong.gw.utils.StringUtils;
import io.jsonwebtoken.*;
import sun.misc.BASE64Decoder;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * saffety的tocken生成器
 */
public class TokenUtils {
    //设置过期时间
    public static final long EXPIRE_DATE = 1000 * 60 * 60 * 24; // 一天
    //token秘钥
    private static final String TOKEN_SECRET = "SAFFETY";

    /**
     * 生成token
     * @param workNo
     * @param realName
     * @param phone
     * @param userName
     * @param email
     * @return
     */
    public static String createToken(String workNo, String realName, String phone, String userName, String email) {

        // 过期时间
        Date date = new Date(System.currentTimeMillis() + EXPIRE_DATE);
        // 获得JWT构造器
        JwtBuilder builder = Jwts.builder();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("workNo", workNo);
        hashMap.put("realName" , realName);
        hashMap.put("phone", phone);
        hashMap.put("userName" , userName);
        hashMap.put("email", email);


        return builder.setSubject("hello")
                .setClaims(hashMap)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
                .compact();
    }

    /**
     * 解析token
     * @param token token
     * @param key   key
     * @return 结果
     */
    public static String getString(String token, String key) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            Jws<Claims> claimsJws
                    = Jwts.parser() // 构造器
                    .setSigningKey(TOKEN_SECRET) // 加盐
                    .parseClaimsJws(token);
            // 获取token里的信息
            Claims claims = claimsJws.getBody();
            return (String) claims.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    // 获取token的workNo
    public static String getWorkNo(String token) {
        return getString(token, "workNo");
    }

    // 获取realName
    public static String getRealName(String token) {
        return getString(token, "realName");
    }

    // 获取phone
    public static  String getPhone (String token) {
        return getString(token, "phone");
    }

    // 获取userName
    public static  String getUserName (String token) {
        return getString(token, "userName")!=null?getString(token, "userName").toLowerCase():"";
    }

    // 获取email
    public static  String getEmail (String token) {
        return getString(token, "email");
    }



    //获取tocken信息
    public static Map<String, String> getTockenUser(String token) {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("workNo", getString(token, "workNo"));
        userInfo.put("realName", getString(token, "realName"));
        userInfo.put("phone", getString(token, "phone"));
        userInfo.put("userName", getString(token, "userName")!=null?getString(token, "userName").toLowerCase():"");
        userInfo.put("email", getString(token, "email")!=null?getString(token, "email").toLowerCase():"");
        return userInfo;
    }

    public static String generateNetworkToken(String phone, String userName, String buttJointCode, String key) {
        // 过期时间
        Date date = new Date(System.currentTimeMillis() + EXPIRE_DATE);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("workNo", phone);
        hashMap.put("realName" , userName);
        hashMap.put("phone", phone);
        hashMap.put("buttJointCode", buttJointCode);
        hashMap.put("redisKey" , key);

        return Jwts.builder().setSubject(phone)
                .setClaims(hashMap)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }




    public static void main(String[] args) {
//        String tocken = "eyJhbGciOiJIUzI1NiJ9.eyJ3b3JrTm8iOiIwMDAwMDAwMiIsInJlYWxOYW1lIjoi5a6J6Ziy5oyH5oyl5Lit5b-D6YOo6Zeo6YKu566xIiwicGhvbmUiOiIiLCJ1c2VyTmFtZSI6InljamtzIiwiZXhwIjoxNzAzMTI0MjUzLCJlbWFpbCI6InljamtzQHl1dG9uZy5jb20ifQ.pZA3zglc0TkpPZfjID3Ya4cugTJJ9PFj92EMiaejQVc";
//        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
//        System.out.println(tockenUser.get("workNo"));
//        System.out.println(tockenUser.get("workNo")==null);
//        String k = "18ba7c94bbec6b5d64ce8f44b83ab746";
//        System.out.println(k.length());
//        String aa = "15:30:00";
//        System.out.println(aa.substring(0, 5));
//        String bbbb = "http%3A%2F%2Fmailqas.yutong.com%3A443%2Fcoremail%2Fcmcu_addon%2FreadAttachment.jsp%3Ftoken%3DBAZMEZiiSKQIhuXPJqNuYuIgjphuVPZL%253At-1699576525177-35eT4OAlDNva7EZ9PL8N5J%26mid%3D1%3A1tbiAQABEmVArl0AVgBfsN%26partId%3D3";
//        try {
//            BASE64Decoder str =new BASE64Decoder();
//            String base64json=new String(str.decodeBuffer(bbbb),"UTF-8");
//            System.out.println(URLDecoder.decode(bbbb, "UTF-8"));
//        } catch (Exception e) {
//
//        }
        String token = createToken("52007541", "wxfyy","15993016992", "冯洋洋","");
        System.out.println(token);
    }
}
