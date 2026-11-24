package com.yutong.hksync.handler;

import com.yutong.hksync.common.VO;
import com.yutong.hksync.common.VoUtil;
import com.yutong.hksync.service.HikTbService;
import com.yutong.hksync.service.SafetyTbService;
import com.yutong.hksync.vo.HkMiddleTableVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HikMiddleHandler {

    @Resource
    private SafetyTbService safetyTbService;

    @Resource
    private HikTbService hikTbService;

    /**
     * 增量同步
     */
    public VO syncToHkAdd() {
        Date start = reduceMinute(new Date(), -123);
        Date end = new Date();

        List<HkMiddleTableVO> list = safetyTbService.selectBlackMessageList(start, end);
        log.info("syncToHkAdd-黑名单增量数据{}", list.size());

        List<HkMiddleTableVO> employeeMessageList = safetyTbService.selectEmployeeMessageList(start, end);
        log.info("syncToHkAdd-员工台账增量数据{}", employeeMessageList.size());

        List<HkMiddleTableVO> registerList = safetyTbService.selectEnterRegisterList(start, end);
        log.info("syncToHkAdd-外来人员备案增量数据{}", registerList.size());

        List<HkMiddleTableVO> visitorHairpinList = safetyTbService.selectVisitorHairpinList(start, end);
        log.info("syncToHkAdd-访客增量数据{}", visitorHairpinList.size());

        list.addAll(employeeMessageList);
        list.addAll(registerList);
        list.addAll(visitorHairpinList);

        if (CollectionUtils.isEmpty(list)) {
            return VoUtil.successData("无增量数据");
        }

        List<HkMiddleTableVO> lastList = new ArrayList<>();
        for (HkMiddleTableVO li : list) {
            if (StringUtils.isBlank(li.getJobNo()) || StringUtils.isBlank(li.getCertificateNo()) || StringUtils.isBlank(li.getFaceUrl()) || StringUtils.isBlank(li.getName())) {
                continue;
            }
            String faceUrl = li.getFaceUrl();
            if (!isPhoto(faceUrl)) {
                if (faceUrl.startsWith("http://mp.yutong.com/mpapi/user-center/file/showPhoto")) {
                    li.setFaceUrl(faceUrl.replace("http://mp.yutong.com/mpapi/user-center/file/showPhoto", "http://safetyimg.yutong.com/mpproxy"));
                } else if (faceUrl.startsWith("http://sg.yutong.com/emp-center/showPhoto")) {
                    li.setFaceUrl(faceUrl.replace("http://sg.yutong.com/emp-center/showPhoto", "http://safetyimg.yutong.com/sgproxy"));
                } else {
                    li.setFaceUrl(null);
                    li.setFaceData(imgBase64(faceUrl));
                }
            }
            if (StringUtils.isBlank(li.getFaceUrl()) && StringUtils.isBlank(li.getFaceData())) {
                continue;
            }
            lastList.add(li);
        }
        Map<String, HkMiddleTableVO> collect = lastList.stream().collect(Collectors.toMap(HkMiddleTableVO::getCertificateNo, Function.identity(), (existing, replacement) -> existing));
        lastList = new ArrayList<>(collect.values());

        log.info("syncToHkAdd-同步数量{}", lastList.size());
        if (!CollectionUtils.isEmpty(lastList)) {
            hikTbService.syncHikMiddleTable(lastList);
        }
        return VoUtil.success();
    }

    /**
     * 时间增加指定分钟
     *
     * @param date 日期
     * @param num  分钟
     * @return 返回日期字符串
     */
    public static Date reduceMinute(Date date, int num) {

        // 创建 Calendar 实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 将日期减去一天
        calendar.add(Calendar.MINUTE, num);

        return calendar.getTime();
    }

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
            ins = response.body().byteStream();
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
