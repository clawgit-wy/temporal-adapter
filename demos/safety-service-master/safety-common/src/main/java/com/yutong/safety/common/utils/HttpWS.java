package com.yutong.safety.common.utils;

import cn.hutool.core.codec.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Http方式调用webservice接口
 * */
public class HttpWS {
    static int socketTimeout = 30000;// 请求超时时间
    static int connectTimeout = 30000;// 传输超时时间

    public static String postSoap(String postUrl, String soapXml) {
        return postSoap1_1(postUrl,soapXml,null,null,null);
    }

    /**
     * 使用SOAP1.1发送消息
     *
     * @param postUrl webservice地址
     * @param soapXml 发送的报文
     * @param soapAction
     * @param user 用户名
     * @param pass 密码
     * @return
     */
    public static String postSoap1_1(String postUrl, String soapXml, String soapAction, String user,String pass) {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(reWriteUrl(postUrl));

        // 需要用户名密码验证
        if(StringUtils.isNotEmpty(user)){
            httpPost.addHeader(new BasicHeader("Authorization","Basic "
                    + Base64.encode((user+":"+pass).getBytes())));
        }

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        if(soapAction != null){
            soapAction = "";
        }
        try {
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", soapAction);
            StringEntity data = new StringEntity(soapXml, Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 打印响应内容
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null)
                    closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retStr;
    }

    /**
     * 使用SOAP1.2发送消息
     *
     * @param postUrl
     * @param soapXml
     * @param soapAction
     * @param user
     * @param pass
     * @return
     */
    public static String postSoap1_2(String postUrl, String soapXml, String soapAction, String user,String pass) {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);

        // 需要用户名密码验证
        if(StringUtils.isNotEmpty(user)){
            httpPost.addHeader(new BasicHeader("Authorization","Basic "
                    + Base64.encode((user+":"+pass).getBytes())));
        }

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        if(soapAction != null){
            soapAction = "";
        }
        try {
            httpPost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", soapAction);
            StringEntity data = new StringEntity(soapXml, Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 打印响应内容
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null)
                    closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retStr;
    }

    public static String reWriteUrl(String wsdlUrl){
        if(wsdlUrl.endsWith("?wsdl")){
            int index = wsdlUrl.indexOf("?wsdl");
            String address = wsdlUrl;
            if (index > 0) {
                address = wsdlUrl.substring(0, index);
            }
            return address;
        }else {
            return wsdlUrl;
        }
    }

    public static void main(String args[]){
        String postUrl = "http://ytsapci.yutong.com:8001/sap/bc/srt/rfc/sap/zeip_get_pernr_status/300/zeip_get_pernr_status/zeip_get_pernr_status";
        /*String soapXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com.yutong.framework.produce.core.other.service\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <com:testWS>" +
                "         <name>?</name>" +
                "      </com:testWS>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";*/
        String soapXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:soap:functions:mc-style\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <urn:ZeipGetPernrStatus>" +
                "         <PtTab>" +
                "            <item>" +
                "               <Icnum>411503198709130057</Icnum>" +
                "            </item>" +
                "         </PtTab>" +
                "      </urn:ZeipGetPernrStatus>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";

        String username = "eipuser";
        String password = "eip@123";
        String result = postSoap1_1(postUrl, soapXml, "",username,password);
        System.out.println(result);
    }
}
