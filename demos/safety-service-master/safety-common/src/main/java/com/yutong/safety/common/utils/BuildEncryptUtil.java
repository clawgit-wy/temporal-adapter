package com.yutong.safety.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import org.apache.commons.collections4.ListUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 楼宇门禁接口调用加解密工具类
 * @author gwxiexj
 * @since 2025-03-24 17:40
 */
public class BuildEncryptUtil {

    //加密key
    //public static String BUILD_ENCRYP_KEY = "50155aa1f5e546318c2e48f3132a7283";
    //签名key
    //public static String SIGN_KEY = "0b29a923a06541d78037cbc8b5a4669d";
    public static String SIGN_ENCODE_TYPE = "ENCODE";
    public static String SIGN_DECODE_TYPE = "DECODE";
    public static String SIGN_FLAG = "sign";
    public static String SIGN_CHARSET = "UTF-8";

    /**
     * 对参数进行3DES加密处理
     * @param data   要加密的数据
     * @param encryKey 加密KYE
     * @return
     */
    public static String desEncrypt(String data,String encryKey) {
        if (data == null) {
            return null;
        }
        byte[] desKey = Base64.decode(encryKey);
        //加密
        byte[] text = data.getBytes(StandardCharsets.UTF_8);
        //进行加密操作
        byte[] cipherBytes = SecureUtil.desede(desKey).encrypt(text);
        //对密文进行BASE64编码
        return Base64.encode(cipherBytes);
    }

    /**
     * 对参数进行3DES解密
     *
     * @param data   要解密的数据
     * @param encryKey 加密KYE
     * @return
     */
    public static String desDecrypt(String data,String encryKey) {
        if (data == null) {
            return null;
        }
        byte[] desKey = Base64.decode(encryKey);
        //对数据和密钥进行BASE64解码
        byte[] text = Base64.decode(data);
        //进行解密操作
        byte[] cipherBytes = SecureUtil.desede(desKey).decrypt(text);
        return new String(cipherBytes, StandardCharsets.UTF_8);
    }

    /**
     * 对参数进行SHA256签名
     *
     * @param dataMap 要签名的参数
     * @param signKey    签名密钥
     * @return
     * @throws Exception
     */
    public static String sha256Sign(Map<String, String> dataMap, String signKey) throws Exception {
        //待签名数据
        String content = signContent(dataMap, SIGN_DECODE_TYPE, signKey);
        //进行签名
        String sign = SecureUtil.sha256(content);
        if (sign != null) {
            sign = sign.toUpperCase();
        }
        return sign;
    }

    /**
     * 对参数进行SHA256验签
     * @param dataMap 要验签的参数
     * @param sign       签名值
     * @param signKey    签名密钥
     * @return
     * @throws Exception
     */
    public static boolean sha256CheckSign(Map<String, String> dataMap, String sign, String signKey) throws Exception {
        //待验签数据
        String content = signContent(dataMap, SIGN_DECODE_TYPE, signKey);
        //进行签名
        String signNew = SecureUtil.sha256(content);
        if (signNew != null) {
            signNew = signNew.toUpperCase();
        }
        //验证签名是否一致
        return Objects.equals(sign, signNew);
    }

    /**
     * 获得待签名/验签的数据
     *
     * @param dataMap 参数数据
     * @param urlType url编解码类型
     * @return
     * @throws Exception
     */
    private static String signContent(Map<String, String> dataMap, String urlType) throws Exception {
        return signContent(dataMap, urlType, null);
    }

    /**
     * 获得待签名/验签的数据
     *
     * @param dataMap 参数数据
     * @param urlType url编解码类型
     * @param signKey 签名的密钥
     * @return
     * @throws Exception
     */
    private static String signContent(Map<String, String> dataMap, String urlType, String signKey) throws Exception {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = dataMap.keySet();
        List<String> keyList = new ArrayList<>(keys);
        //排序
        Collections.sort(keyList);
        for (String key : keyList) {
            if (!org.springframework.util.StringUtils.hasText(key)) {
                continue;
            }
            if (SIGN_FLAG.equals(key.toLowerCase())) {
                continue;
            }
            String val = dataMap.get(key);
            if (!org.springframework.util.StringUtils.hasText(val)) {
                continue;
            }
            if (urlType.equals(SIGN_ENCODE_TYPE)) {
                //对数据进行URL编码
                val = URLEncoder.encode(val, SIGN_CHARSET);
            } else if (urlType.equals(SIGN_DECODE_TYPE)) {
                //对数据进行URL解码
                val = URLDecoder.decode(val, SIGN_CHARSET);
            }
            sb.append(key).append("=").append(val).append("&");
        }
        String signContent;
        if (StringUtils.hasText(signKey)) {
            signContent = sb.append("key=").append(signKey).toString();
        } else {
            if (sb.length() > 1) {
                signContent = sb.substring(0, sb.length() - 1);
            } else {
                signContent = sb.toString();
            }
        }
        return signContent;
    }


    public static void main(String[] args) throws Exception {
        String grantBuildUrl = "http://10.66.32.110/IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/AddGrantByOutid";
        String buildEncryKey = "50155aa1f5e546318c2e48f3132a7283";
        String buildSignKey = "0b29a923a06541d78037cbc8b5a4669d";
        String buildAppID = "150000634047";
        String buildDpcode = "00000000";
        String stat = "2025-12-01 00:00:00";
        String end = "2099-12-31 23:59:59";
        String empCodeStr = "90012442,90063224,90141364,90164977,91064477,90001239,90006925,90012441,90004183,90000001,90004010";
        String[] empArr = empCodeStr.split(",");
        for(String emp:empArr){
            test4(emp,stat,end,grantBuildUrl,buildEncryKey,buildSignKey,buildAppID,buildDpcode);
        }
    }

    //授权
    public static void test4(String employeeCode,String start,String end,String grantBuildUrl,
                             String buildEncryKey,String buildSignKey,String buildAppID,String buildDpcode)
            throws Exception{
        String dealDeviceCode = "150364,150006,150478,150310,150270,150514,150511,150513,150515,150516,150328,150438,150467,150230,150359,150445,150302,150436,150528,150010,150308,150331,150239,150121,150205,150207,150122,150124,150123,150444,150108,150039,150042,150037,150040,150041,150038,150020,150351,150024,150025,150023,150026,150146,150432,150313,150342,150047,150315,150356,150453,150526,150149,150022,150541,150372,150337,150510,150530,150276,150456,150375,150450,150316,150477,150366,150148,150044,150439,150268,150021,150357,150079,150009,150394,150473,150055,150204,150279,150479,150151,150070,150335,150137,150362,150384,150190,150104,150059,150482,150018,150102,150330,150459,150340,150344,150216,150350,150203,150383,150461,150404,150141,150185,150031,150014,150412,150125,150472,150088,150186,150376,150532,150405,150538,150016,150067,150015,150410,150381,150363,150341,150080,150008,150309,150224,150434,150029,150507,150365,150105,150408,150284,150320,150027,150267,150049,150411,150463,150492,150480,150058,150073,150488,150150,150288,150215,150214,150046,150285,150326,150495,150493,150416,150035,150387,150050,150454,150338,150011,150127,150534,150013,150519,150028,150277,150251,150152,150076,150497,150096,150542,150388,150086,150371,150249,150430,150543,150494,150089,150045,150232,150329,150520,150081,150449,150131,150395,150304,150283,150399,150282,150452,150353,150223,150414,150048,150233,150311,150401,150504,150502,150392,150303,150327,150134,150130,150325,150476,150393,150455,150066,150030,150306,150486,150043,150469,150403,150433,150098,150019,150491,150508,150343,150107,150448,150053,150142,150355,150264,150386,150489,150540,150529,150525,150462,150140,150546,150176,150523,150145,150265,150447,150352,150144,150319,150178,150234,150409,150458,150174,150487,150503,150061,150490,150143,150522,150544,150052,150440,150250,150521,150129,150244,150139,150083,150339,150132,150097,150056,150377,150075,150474,150468,150084,150435,150382,150506,150275,150346,150012,150072,150099,150427,150539,150475,150085,150509,150062,150524,150175,150300,150415,150466,150231,150126,150278,150324,150103,150082,150531,150272,150074,150060,150402,150301,150360,150485,150334,150017,150100,150269,150004,150138,150465,150180,150147,150135,150235,150133,150051,150057,150533,150396,150373,150128,150413,150136,150481,150323,150054,150065,150280,150101,150385,150361,150036,150322,150063,150347,150106,150451,150078,150317,150281,150077,150271,150087,150286,150470,150545,150332,150535,150312,150460,150354,150071,150406,150484,150358,150179,150471,150464";
        List<String> deviceList = Arrays.asList(dealDeviceCode.split(","));
        List<List<String>> partition = ListUtils.partition(deviceList, 200);
        AtomicReference<String> issuedStatus = new AtomicReference<>("成功");
        partition.forEach(li -> {
            String curStatus = "成功";
            String curDevice = li.stream().collect(Collectors.joining(","));
            //封装参数
            JSONObject firstData = new JSONObject();
            firstData.put("outid",employeeCode);
            firstData.put("extendids",curDevice);
            firstData.put("start", start);
            firstData.put("end", end);
            firstData.put("status",1);
            firstData.put("empcode","mjqxdj"); //下发门禁指定账号
            LogUtils.info("新开普下发的参数：{}",firstData);

            try {
                //下发门禁
                String retStr = BuildEncryptUtil.sendBuildHttpPost(grantBuildUrl, firstData.toJSONString(),
                        buildEncryKey,buildSignKey,buildAppID,buildDpcode);
                LogUtils.info("新开普下发返回值：{}",retStr);
                if(ResponseEnum.FAIL.getName().equals(retStr)){
                    curStatus = "异常";
                }else{
                    JSONObject object = JSONObject.parseObject(retStr);
                    if(object.getInteger("result") != 100){
                        LogUtils.error("门禁权限下发异常："+retStr);
                        curStatus += ";"+retStr;
                    }
                }
            } catch (Exception ex) {
                curStatus = "异常";
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"门禁权限下发异常，参数：param="+firstData+",异常信息："+ex+" \"}");
            }finally {
                if(!"成功".equals(curStatus)){
                    issuedStatus.set(curStatus);
                }
            }
        });
        System.out.println(employeeCode+"下发状态"+issuedStatus.get());
    }

    public static void test3() throws Exception{
        JSONObject firstData = new JSONObject();
        int buildPageNo = 1;
        int buildPageSize = 100;
        String buildEncryKey = "50155aa1f5e546318c2e48f3132a7283";
        String buildSignKey = "0b29a923a06541d78037cbc8b5a4669d";
        String buildAppID = "150000634047";
        String buildDpcode = "00000000";
        String getBuildTermUrl = "http://10.66.226.90/IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/GetAccessAreaInfo";
        firstData.put("pageno",buildPageNo);
        firstData.put("pagesize",buildPageSize);
        String retStr = BuildEncryptUtil.sendBuildHttpPost(getBuildTermUrl, firstData.toJSONString(),
                buildEncryKey,buildSignKey,buildAppID,buildDpcode);
        System.out.println("获得返回的结果：" + retStr);
    }

    public static void test1() throws Exception {
        String url = "http://10.66.226.90/IdentityWebService/Identity.ThirdOpenApiCall.ashx/group/GetAccessAreaInfo";
        JSONObject object = new JSONObject();
        object.put("pageno",1);
        object.put("pagesize",100);
        String encryKey = "50155aa1f5e546318c2e48f3132a7283";
        String signKey = "0b29a923a06541d78037cbc8b5a4669d";
        String entryParam = BuildEncryptUtil.desEncrypt(object.toJSONString(),encryKey);
        //String urlStr = URLEncoder.encode(entryParam);
        System.out.println("加密后编码="+entryParam);
        String cipherString = URLEncoder.encode(entryParam, String.valueOf(StandardCharsets.UTF_8));
        System.out.println("加密后的URL编码="+cipherString);
        Map<String,String> openReq = new HashMap<>();
        openReq.put("appid","150000634047");
        openReq.put("ctdate",DateUtils.format(new Date(),5));
        openReq.put("data",cipherString);
        openReq.put("dpcode","00000000");
        openReq.put("msgid",UUID.randomUUID().toString());
        String sign = BuildEncryptUtil.sha256Sign(openReq, signKey);
        openReq.put("sign",sign);
        //String paramStr = BuildUtils.decryptParam(entryParam);
        System.out.println("解析后参数="+openReq);
        org.json.JSONObject jsonBody = new org.json.JSONObject(openReq);
        System.out.println("请求地址：" + url);
        System.out.println("请求参数：" + jsonBody);
        String result = HttpUtil.createPost(url).body(jsonBody.toString()).execute().body();
        System.out.println("获得返回的结果：" + result);
        try{
            JSONObject retObj = JSONObject.parseObject(result);
            if(retObj.getInteger("retcode") != 200){
                LogUtils.error("返回异常",result);
            }else{
                String rd = URLDecoder.decode(retObj.getString("data"), "UTF-8");
                String data = BuildEncryptUtil.desDecrypt(rd,encryKey);
                System.out.println("解密后数据：" + data);
            }
        }catch (Exception e){
            LogUtils.error("返回异常",e);
        }
    }

    public static void test2() throws UnsupportedEncodingException {
        String encryKey = "50155aa1f5e546318c2e48f3132a7283";
        //对参数进行解密
        String retunData = "YGX6h6tTm6%2fHDk2%2fzCzEtbH2IEUG8QZvLD4mE0HRng4VQBCyjzqh5zN%2f0YRbXCBudYvhiL%2bi5EBM2jJEycK1vCHAbULTc%2brSIqIlC%2ffr4UVGtMtqgYJyxKShaobksvoPn0k6xrZ%2fYZo%3d";
        String rd = URLDecoder.decode(retunData, "UTF-8");
        String data = BuildEncryptUtil.desDecrypt(rd,encryKey);
        System.out.println("解密后数据：" + data);
    }

    public static String sendBuildHttpPost(String url,String data,String buildEncryKey,String buildSignKey,
                                           String buildAppID,String buildDpcode) throws Exception{
        String entryParam = BuildEncryptUtil.desEncrypt(data,buildEncryKey);
        String cipherString = URLEncoder.encode(entryParam, String.valueOf(StandardCharsets.UTF_8));
        Map<String,String> openReq = new HashMap<>();
        openReq.put("appid",buildAppID);
        openReq.put("ctdate",DateUtils.format(new Date(),5));
        openReq.put("data",cipherString);
        openReq.put("dpcode",buildDpcode);
        openReq.put("msgid", UUID.randomUUID().toString());
        String sign = BuildEncryptUtil.sha256Sign(openReq, buildSignKey);
        openReq.put("sign",sign);
        LogUtils.info("sendBuildHttpPost请求入参="+openReq+";url="+url);
        org.json.JSONObject jsonBody = new org.json.JSONObject(openReq);
        String result = HttpUtil.createPost(url).body(jsonBody.toString()).execute().body();
        try{
            JSONObject retObj = JSONObject.parseObject(result);
            if(retObj.getInteger("retcode") != 200){
                //重试一次
                result = HttpUtil.createPost(url).body(jsonBody.toString()).execute().body();
                retObj = JSONObject.parseObject(result);
            }
            if(retObj.getInteger("retcode") != 200){
                LogUtils.error("sendBuildHttpPost返回异常",result);
                return ResponseEnum.FAIL.getName();
            }
            else{
                String rd = URLDecoder.decode(retObj.getString("data"), "UTF-8");
                return BuildEncryptUtil.desDecrypt(rd,buildEncryKey);
            }
        }catch (Exception e){
            LogUtils.error("sendBuildHttpPost返回异常",e);
            return ResponseEnum.FAIL.getName();
        }
    }
}
