package com.yutong.safety.common.utils;

import com.google.common.collect.Maps;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>
 * 接口调用工具类
 *
 * doWebservicePost webservice
 * sendHttp rest及其他http接口
 * </p>
 *
 * @author gwcuiyx
 * @since 2023-01-03
 */
@Component
public class WebserviceUtil {

    private static final String HTTP = "http";

    private static final String HTTPS = "https";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final int HTTP_DEFAULT_ERROR = -1;

    private CloseableHttpClient httpClient = null;

    private RequestConfig defaultRequestConfig = null;

    private PoolingHttpClientConnectionManager cm = null;

    private static SSLConnectionSocketFactory sslsf = null;

    private OkHttpClient client = null;

    @Value("${httpclient.socket-timeout}")
    private int socketTimeout;

    @Value("${httpclient.connect-timeout}")
    private int connectTimeout;

    @Value("${httpclient.max-perroute}")
    private int maxPerRoute;

    @Value("${httpclient.max-total}")
    private int maxTotal;

    @Value("${httpclient.validateAfterInactivity}")
    private int validateAfterInactivity;

    @Value("${httpclient.tcpNoDelay}")
    private boolean tcpNoDelay;

    @Value("${httpclient.staleConnectionCheckEnabled}")
    private boolean staleConnectionCheckEnabled;

    @Value("${httpclient.soKeepAlive}")
    private boolean soKeepAlive;

    @PostConstruct
    public void init() {
        LogUtils.info(
                "HttpClient socketTimeout:{} connectTimeout:{} maxPerRoute:{} maxTotal:{}",
                socketTimeout, connectTimeout, maxPerRoute, maxTotal);

        SSLContextBuilder builder = new SSLContextBuilder();
        // 全部信任 不做身份鉴定
        try {
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(HTTP, new PlainConnectionSocketFactory())
                .register(HTTPS, sslsf)
                .build();


        cm = new PoolingHttpClientConnectionManager(registry, null, null, null, 2, TimeUnit.HOURS);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        cm.setMaxTotal(maxTotal);
        cm.setValidateAfterInactivity(validateAfterInactivity);
        SocketConfig socketConfig = SocketConfig.custom()/*.setTcpNoDelay(tcpNoDelay).setSoKeepAlive(soKeepAlive)*/
                .build();
        cm.setDefaultSocketConfig(socketConfig);

        defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)/*.setStaleConnectionCheckEnabled(staleConnectionCheckEnabled)*/.build();
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setConnectionManager(cm)/*.setKeepAliveStrategy(myStrategy)*/.build();

        client = new OkHttpClient();
    }

    public String doWebservicePost(String url, String data) {
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String result = null;
        try{
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            httpPost.setHeader("Connection", "Close");
            httpPost.setHeader("SOAPAction","application/soap+json; charset=UTF-8");
            StringEntity se = new StringEntity(data,"UTF-8");
            se.setContentType("UTF-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,"UTF-8");
                    JSONObject jsonObject = XML.toJSONObject(result);
                    return jsonObject.toString();
                }
            }
        }catch(Exception ex){
            LogUtils.error("服务器调用失败：{url:{},error:{}}",url, ex);
        }finally {
            httpPost.releaseConnection();
            try {
                if (null != response) {
                    EntityUtils.consumeQuietly(response.getEntity());
                    response.close();
                }
            } catch (Exception exc) {
                LogUtils.warn("post release异常，url:{}", url);
            }
        }
        return result;
    }

    /**
     * BPM接口不支持 SOAPAction application/soap+json; charset=UTF-8
     * @param url
     * @param data
     * @return
     */
    public String doWebservicePostNew(String url, String data) {
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String result = null;
        try{
            httpPost = new HttpPost(url);
            httpPost.setHeader("Connection", "Close");
            StringEntity se = new StringEntity(data,"UTF-8");
            se.setContentType("UTF-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,"UTF-8");
                    JSONObject jsonObject = XML.toJSONObject(result);
                    return jsonObject.toString();
                }
            }
        }catch(Exception ex){
            LogUtils.error("服务器调用失败：{url:{},error:{}}",url, ex);
        }finally {
            httpPost.releaseConnection();
            try {
                if (null != response) {
                    EntityUtils.consumeQuietly(response.getEntity());
                    response.close();
                }
            } catch (Exception exc) {
                LogUtils.warn("post release异常，url:{}", url);
            }
        }
        return result;
    }
    /**
     * 发送http请求
     * @param url 请求地址
     * @param type 类型 1 from表单   2 JSON
     * @param bodyParams body参数
     * @param headParams head参数
     * @return
     */
    public String sendHttp(String url, String type, String bodyParams, Map headParams, String requestType) {
        URL u;
        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        //from 表单提交
        if("1".equals(type)){
            if (StringUtils.isNotEmpty(bodyParams)) {
                if(bodyParams.indexOf("[") == 0){
                    JSONArray array = new JSONArray(bodyParams);
                    for(int i=0; i<array.length(); i++){
                        JSONObject params= array.getJSONObject(i);
                        for (Object key:params.keySet()) {
                            sb.append(key.toString());
                            sb.append("=");
                            sb.append(params.getString(key.toString()));
                            sb.append("&");
                        }
                    }
                }else{
                    JSONObject params= new JSONObject(bodyParams);
                    for (Object key:params.keySet()) {
                        sb.append(key.toString());
                        sb.append("=");
                        sb.append(params.getString(key.toString()));
                        sb.append("&");
                    }
                }
                sb.substring(0, sb.length() - 1);
            }
        }
        // 尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            //// POST 只能为大写，严格限制，post会不识别    默认post请求
            if(StringUtils.isEmpty(requestType)){
                requestType="POST";
            }
            con.setRequestMethod(requestType);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置连接时间
            con.setReadTimeout(30000);
            if(headParams!=null){
                for(Object key: headParams.keySet()){
                    con.setRequestProperty(key.toString(), headParams.get(key).toString());
                }
            }
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            if("1".equals(type)){
                osw.write(sb.toString());
            }
            if("2".equals(type)){
                osw.write(bodyParams.toString());
            }
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        // 读取返回内容
        StringBuilder buffer = new StringBuilder();
        try {
            //一定要有返回值，否则无法把请求发送给server端。

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
            br.close();
        } catch (Exception e) {
            LogUtils.error("服务器调用失败：{url:{},error:{}}",url, e);
        }
        return buffer.toString();
    }

    /**
     * 流程字段的XML生成
     * @param obj
     * @return
     */
    public String createXML(Object obj) {
        // 构造输出XML文件的字节输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 构造一个XML编码器
        XMLEncoder xmlEncoder = new XMLEncoder(bos);
        // 使用XML编码器写对象
        xmlEncoder.writeObject(obj);
        // 关闭编码器
        xmlEncoder.close();
        String rtnVal = null;
        try {
            rtnVal = new String(bos.toByteArray(), "UTF-8");
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rtnVal;
    }

    /**
     * 方法调用
     * @param url
     * @param tocken
     * @return
     */
    public String sendHttps(String url, String tocken) {
        Response response = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Connection","close")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", tocken)
                    .build();
            response = client.newCall(request).execute();
            if(response != null){
                return response.body().string();
            }
        } catch (Exception e) {
            LogUtils.error("服务器调用失败：{url:{},error:{}}",url, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    public String sendHttpForGet(String url) {
        Response response = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Connection","close")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "f3df757a-f4b7-4081-b002-1ba38e9ecaf0")
                    .build();
            response = client.newCall(request).execute();
            if(response != null){
                return response.body().string();
            }
        } catch (Exception e) {
            LogUtils.error("服务器调用失败：{url:{},error:{}}",url, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    public String postJsonStr(String url, String jsonParams, Map<String, String> headerMap) {
        return postStr(url, buildJsonEntity(jsonParams), headerMap);
    }

    private HttpEntity buildJsonEntity(String jsonParam) {
        ContentType ct = ContentType.create("application/json", Charset.forName(DEFAULT_CHARSET));
        return new StringEntity(jsonParam, ct);
    }

    private String postStr(String url, HttpEntity httpEntity, Map<String, String> headerMap) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(defaultRequestConfig);
        addHeaders(httpPost, headerMap);
        int statusCode = HTTP_DEFAULT_ERROR;

        StringBuffer sb = new StringBuffer();
        CloseableHttpResponse response = null;
        try {
            if (null != httpEntity) {
                httpPost.setEntity(httpEntity);
            }
            response = httpClient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            reader.close();
            if (StringUtils.isBlank(sb)) {
                sb.append(response.getStatusLine().getStatusCode());
            }
        } catch (HttpHostConnectException e) {
            statusCode = HttpStatus.SC_NOT_FOUND;
            LogUtils.error("连接异常 url:" + url, e);
        } catch (Exception e) {
            LogUtils.error("服务器调用失败 url:" + url, e);
        } finally {
            try {
                httpPost.releaseConnection();
                if (null != response) {
                    EntityUtils.consumeQuietly(response.getEntity());
                    response.close();
                }
            } catch (Exception e) {
                LogUtils.error("post release异常，url:{url:{},message:{}}", url, e.getMessage());
            }
        }
        return sb.toString();
    }

    private void addHeaders(HttpRequestBase httpRequestBase, Map<String, String> headerMap) {
        if (null == httpRequestBase || null == headerMap) {
            return;
        }
        for (String key : headerMap.keySet()) {
            httpRequestBase.addHeader(key, headerMap.get(key));
        }
    }

    /**
     * 测试调用接口并解析返回值 xml转json
     */
    public static void main(String[] args) {
       /* String url = "http://ehrapiuat.yutong.com/oauth/oauth/token";
        String bodyParams = "{\"grant_type\":\"client_credentials\",\"client_id\":\"eip\",\"client_secret\":\"eip@123\"}";
        String post = sendHttp(url, "1", bodyParams, new HashMap(), "POST");
        System.out.println(post);*/
        Map<String, String> test = Maps.newHashMap();
        test.put("visited_name","test");
        test.put("vendor_unit","test1");
        test.put("visitor_name","test2");
        test.put("start_time","test3");
        test.put("end_time","test4");
        String text = "尊敬的{visited_name}老师：{vendor_unit}{visitor_name}预约{start_time}-{end_time}来访，请通过EIP或掌上宇通及时审批访客预约流程。";
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
        Matcher matcher = pattern.matcher(text);
        System.out.println(text);
        while (matcher.find()) {
            System.out.println(matcher.group(1));
            text = text.replaceFirst("\\{[^}]*\\}", test.get(matcher.group(1)));
        }
        System.out.println(text);
    }
}
