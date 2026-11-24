package com.yutong.modules.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.Base64;

@Slf4j
public class Base64ImageDecoder {

    /**
     * 解码Base64图片并保存到文件
     * @param base64Data 包含头部的完整Base64字符串(如"data:image/png;base64,...")
     * @param outputPath 输出文件路径(需包含扩展名)
     * @throws IllegalArgumentException 当数据格式错误时抛出
     * @throws IOException 当文件写入失败时抛出
     */
    public static void decodeToFile(String base64Data, String outputPath)
            throws IOException {
        // 分离Base64元数据和实际数据
        String[] parts = base64Data.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Base64 image format");
        }

        // 获取实际Base64编码部分
        String imageData = parts[1];
        Base64.Decoder decoder = Base64.getDecoder();

        // 解码字节数组
        byte[] imageBytes = decoder.decode(imageData);

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(imageBytes);
        }
    }

    /**
     * 将图片文件转换为Base64字符串（带MIME类型头）
     * @param filePath 图片文件路径
     * @return 带数据头的Base64字符串
     * @throws IOException 当文件读取失败时抛出
     */
    public static String convert(String filePath) throws IOException {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);

            // 自动识别图片类型
            String mimeType = getMimeType(filePath);
            Base64.Encoder encoder = Base64.getEncoder();

            return "data:" + mimeType + ";base64," +
                    encoder.encodeToString(bytes);
        }
    }

    private static String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        switch (extension.toLowerCase()) {
            case "png": return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "gif": return "image/gif";
            case "bmp": return "image/bmp";
            default: return "application/octet-stream";
        }
    }

    public static byte[] getPictureDataInputStream(String path) {

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
            ins = response.body().byteStream();
            output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = ins.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();

        } catch (IOException e) {
            log.info("加载图片失败",e);
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(output);
        }
        return data;
    }

    public static String imgBase64(String lifePhoto){
        if (StringUtils.isBlank(lifePhoto)) {
            return "";
        }
        byte[] imageBytes = getPictureDataInputStream(lifePhoto);
        // 自动识别图片类型
        String mimeType = getMimeType(lifePhoto);
        Base64.Encoder encoder = Base64.getEncoder();
        String base64_str = DatatypeConverter.printBase64Binary(imageBytes);
       /* return "data:" + mimeType + ";base64," +
                encoder.encodeToString(imageBytes);*/
        return base64_str;
    }

    /**
     * 判断URL是不是照片地址
     * @param address 照片地址
     * @return true 是； false不是
     */
    public static boolean isPhoto(String address){
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
    public static void main(String[] args) {
        String base64 = imgBase64("http://sg.yutong.com/emp-center/showPhoto?id=1838166460916191233&apikey=330b46484c0a456b99a92e199ef6ab4e");
        System.out.println(base64); // 打印部分结果
    }
}
