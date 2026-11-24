package org.jeecg.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

/**
 * 文件上传公用类
 */
@Slf4j
public class HttpFileUtil {

    /**
     * 文件上传
     * @param url
     * @param file
     * @return
     */
    public static String uploadFile(String url, MultipartFile file, String tocken) {
        DataOutputStream out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            String BOUNDARY = "----WebKitFormBoundaryikGe53euWTLsWZyy";
            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
            conn.setRequestProperty("sessionKey", tocken);
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.connect();
            out = new DataOutputStream(conn.getOutputStream());
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            //添加参数file
            StringBuffer sb = new StringBuffer();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"file\";filename=\"").append(file.getName()).append("\"");
            sb.append("\r\n");
            sb.append("Content-Type: ").append(file.getContentType());
            sb.append("\r\n");
            sb.append("\r\n");
            out.write(sb.toString().getBytes());
            DataInputStream in1 = new DataInputStream(file.getInputStream());
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in1.read(bufferOut)) != -1) {
                out.write(bufferOut,0,bytes);
            }
            out.write("\r\n".getBytes());
            in1.close();
            out.write(end_data);

            //flush输出流的缓冲
            out.flush();
            //定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error("文件上传错误-fileName：{fileName:{},e:{}}", file.getName(), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                log.error("文件上传错误-fileName：{fileName:{},e:{}}", file.getName(), ex);
            }
        }
        return result.toString();
    }

    public static String uploadNewFile(String url, MultipartFile file, String tocken) {
        String result = "";
        try {
            OkHttpClient client = new OkHttpClient();
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //获取文件后缀
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            //用uuid作为文件名称，防止生成的临时文件重复
            final File excelFile = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            file.transferTo(excelFile);
            MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
            RequestBody fileBody  = RequestBody.create(mediaType,excelFile);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Accept","application/json, text/plain, */*")
                    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("Accept-Language","zh-CN,zh;q=0.9")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Length", String.valueOf(file.getSize()))
                    .addHeader("Origin","chrome-extension://gbpdienpcldibplacgacghoiaimildlo")
                    .addHeader("sessionKey", tocken)
                    .addHeader("Host","apitest.yutong.com:20443")
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                    .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                    .build();

            Response response = client.newCall(request).execute();

            assert response.body() != null;
            result = response.body().string();

            if(excelFile.exists()){
                excelFile.delete();
            }

        } catch (Exception e) {
            log.error("文件上传错误-fileName：{fileName:{},e:{}}", file.getName(), e);
        }
        return result;
    }


    /**
     * 网络图片转base64
     */

    public static String getImgUrlToBase64(String imgUrl) {
        byte[] buffer = null;
        InputStream inputStream = null;
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            // 创建URL
            URL url = new URL(imgUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            inputStream = conn.getInputStream();
            // 将内容读取内存中
            buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            buffer = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        Base64.Encoder encode = Base64.getEncoder();
        return encode.encodeToString(buffer);
    }

    /**
     * http post请求
     * @param url 请求连接
     * @param paramJson 参数
     */
    public static String sendPost(String url, String paramJson) {

        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(url);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");
        try {
            int statusCode = 0;
            int i = 0;
            //链接异常时，可重试三次
            while (statusCode != HttpStatus.SC_OK && i<3) {
                // json格式的参数解析
                RequestEntity entity = new StringRequestEntity(paramJson, "application/json", "UTF-8");
                postMethod.setRequestEntity(entity);
                statusCode = httpClient.executeMethod(postMethod);
                if (statusCode == HttpStatus.SC_OK) {
                    String result = new String(postMethod.getResponseBody(), "UTF-8");
                    postMethod.releaseConnection();
                    return result;
                }
                i++;
            }
            log.error("POST请求发出失败，请求的地址为{}，参数为{}，错误信息为{}", url, paramJson, "HTTP状态码: " + statusCode);
        } catch (IOException e) {
            log.error("POST请求发出失败，请求的地址为{},错误信息为{}",url, e.getMessage());
        }finally {
            postMethod.releaseConnection();
        }
        return null;
    }
}
