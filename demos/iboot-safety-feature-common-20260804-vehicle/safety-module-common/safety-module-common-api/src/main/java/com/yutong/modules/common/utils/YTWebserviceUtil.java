package com.yutong.modules.common.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;


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
public class YTWebserviceUtil {

    private static final String HTTP = "http";

    private static final String HTTPS = "https";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final int HTTP_DEFAULT_ERROR = -1;

    private CloseableHttpClient httpClient = null;

    private RequestConfig defaultRequestConfig = null;

    private PoolingHttpClientConnectionManager cm = null;

    private static SSLConnectionSocketFactory sslsf = null;

    private OkHttpClient client = null;

    @Value("${httpclient.socket-timeout:10000}")
    private int socketTimeout;

    @Value("${httpclient.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${httpclient.max-perroute:20}")
    private int maxPerRoute;

    @Value("${httpclient.max-total:1000}")
    private int maxTotal;

    @Value("${httpclient.validateAfterInactivity:1000}")
    private int validateAfterInactivity;

    @Value("${httpclient.tcpNoDelay:false}")
    private boolean tcpNoDelay;

    @Value("${httpclient.staleConnectionCheckEnabled:true}")
    private boolean staleConnectionCheckEnabled;

    @Value("${httpclient.soKeepAlive:true}")
    private boolean soKeepAlive;

    @PostConstruct
    public void init() {
        YTLogUtils.info(
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
            YTLogUtils.error("服务器调用失败：{url:{},error:{}}",url, ex);
        }finally {
            httpPost.releaseConnection();
            try {
                if (null != response) {
                    EntityUtils.consumeQuietly(response.getEntity());
                    response.close();
                }
            } catch (Exception exc) {
                YTLogUtils.warn("post release异常，url:{}", url);
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
            YTLogUtils.error("服务器调用失败：{url:{},error:{}}",url, ex);
        }finally {
            httpPost.releaseConnection();
            try {
                if (null != response) {
                    EntityUtils.consumeQuietly(response.getEntity());
                    response.close();
                }
            } catch (Exception exc) {
                YTLogUtils.warn("post release异常，url:{}", url);
            }
        }
        return result;
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
            YTLogUtils.error("服务器调用失败：{url:{},error:{}}",url, e);
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
            YTLogUtils.error("服务器调用失败：{url:{},error:{}}",url, e);
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
            YTLogUtils.error("连接异常 url:" + url, e);
        } catch (Exception e) {
            YTLogUtils.error("服务器调用失败 url:" + url, e);
        } finally {
            try {
                httpPost.releaseConnection();
                if (null != response) {
                    EntityUtils.consumeQuietly(response.getEntity());
                    response.close();
                }
            } catch (Exception e) {
                YTLogUtils.error("post release异常，url:{url:{},message:{}}", url, e.getMessage());
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

}
