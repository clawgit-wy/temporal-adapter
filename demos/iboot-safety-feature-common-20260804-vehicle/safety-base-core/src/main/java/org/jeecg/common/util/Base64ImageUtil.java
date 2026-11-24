package org.jeecg.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 照片
 */
@Slf4j
public class Base64ImageUtil {

    /**
     * 判断URL是不是照片地址
     *
     * @param address 照片地址
     * @return true 是； false不是
     */
    public static boolean isPhoto(String address) {
        // 定义一个数组，包含可能的照片文件扩展名
        String[] photoExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        // 获取文件名的最后一部分（不包括路径）
        String fileName = address.substring(address.lastIndexOf('/') + 1);
        // 获取文件扩展名
        int num = fileName.lastIndexOf('.');
        if (num == -1) {
            return false;
        }
        String fileExtension = fileName.substring(num);
        // 判断扩展名是否在允许的列表中
        for (String ext : photoExtensions) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    public static String imgBase64(String lifePhoto) {
        if (StringUtils.isBlank(lifePhoto)) {
            return "";
        }
        byte[] imageBytes = getPictureDataInputStream(lifePhoto);
        return DatatypeConverter.printBase64Binary(imageBytes);
    }


    private static byte[] getPictureDataInputStream(String path) {
        Response response = null;
        InputStream ins = null;
        ByteArrayOutputStream output = null;
        byte[] data = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(path)
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
            ins = Objects.requireNonNull(response.body()).byteStream();
            output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = ins.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();

        } catch (IOException e) {
            log.info("加载图片失败", e);
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(output);
        }
        return data;
    }
}
