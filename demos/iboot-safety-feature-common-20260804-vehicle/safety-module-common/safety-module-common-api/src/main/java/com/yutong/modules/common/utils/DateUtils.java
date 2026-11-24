package com.yutong.modules.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jeecg.common.util.BizException;
import org.jeecg.common.util.PreconditonsEx;
import org.jeecg.common.util.ResponseEnum;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jackliu on 17-3-23.
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static final FastDateFormat[] FORMAT = new FastDateFormat[]{
            FastDateFormat.getInstance("yyyyMM"),
            FastDateFormat.getInstance("yyyy-MM-dd"),
            FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss"),
            FastDateFormat.getInstance("yyyy-MM-dd HH:mm"),
            FastDateFormat.getInstance("HH:mm"),
            FastDateFormat.getInstance("yyyyMMddHHmmss"),
            FastDateFormat.getInstance("HH:mm:ss"),
            FastDateFormat.getInstance("yyyyMMdd"),
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
            FastDateFormat.getInstance("yyyy.MM.dd"),
            FastDateFormat.getInstance("yyyyMMddHHmm")
    } ;

    /**
     * 指定格式的日期校验， 目前只测试了部分，用之前先测试一下，是否可用 只支持两种方式
     * @param dateStr 日期
     */
    public static boolean isValidDateFormat(String dateStr, int idx) {
        String regex = "";

        switch (FORMAT[idx].getPattern()) {
            case "yyyy-MM-dd HH:mm:ss":
                regex = "^([0-9]{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
                break;
            case "yyyy-MM-dd HH:mm":
                regex = "^([0-9]{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$";
                break;
            case "yyyy-MM-dd":
                regex = "^([0-9]{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
                break;
            default:
                return false;
        }

        return Pattern.matches(regex, dateStr);
    }

    /**
     * 将日期格式化
     * @param date
     */
    public static String format(Date date, int idx) {
        PreconditonsEx.checkArgument(idx < FORMAT.length, ResponseEnum.EXCEPTION.getCode(), "时间转换idx越界！");
        PreconditonsEx.checkArgument(date != null, ResponseEnum.EXCEPTION.getCode(), "时间不允许为空！");
        return FORMAT[idx].format(date) ;
    }

    /**
     * 将字符串转日期
     * @param date
     */
    public static Date parse(String date, int idx) {
        PreconditonsEx.checkArgument(idx < FORMAT.length, ResponseEnum.EXCEPTION.getCode(), "时间转换idx越界！");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(date), ResponseEnum.EXCEPTION.getCode(), "时间不允许为空！");
        try {
            return FORMAT[idx].parse(date) ;
        } catch (Exception e) {
            throw new BizException( ResponseEnum.EXCEPTION.getCode(), "时间不允许为空！") ;
        }
    }
    public static Date parseNull(String date, int idx) {
        if(StringUtils.isBlank(date)){
            return null;
        }
        PreconditonsEx.checkArgument(idx < FORMAT.length, ResponseEnum.EXCEPTION.getCode(), "时间转换idx越界！");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(date), ResponseEnum.EXCEPTION.getCode(), "时间不允许为空！");
        try {
            return FORMAT[idx].parse(date) ;
        } catch (Exception e) {
            throw new BizException( ResponseEnum.EXCEPTION.getCode(), "时间不允许为空！") ;
        }
    }

    /**
     * 时间相减
     * @param fromDate
     * @param toDate
     * @return
     */
    public static long sub(Date fromDate, Date toDate) {
        long res = fromDate.getTime() - toDate.getTime() ;
        return res > 0 ? res/1000 : 0 ;
    }

    public static String SECODEDATE(int times) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, times);
        return format(calendar.getTime(), 2);
    }

    /**
     * 计算丢失天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static long loseDay(String startDate, String endDate) {
        Date startTime = parse(startDate, 1);
        Date endTime = parse(endDate, 1);
        long res = endTime.getTime() - startTime.getTime();
        return res > 0 ? res/(24*3600*1000) : 0;
    }

    /**
     * 字符串传字符串类型数据
     * @param dateStr
     * @return
     */
    public static String dateToString(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(dateStr);
            return format(date, 3);
        } catch (Exception e) {
            throw new BizException( ResponseEnum.EXCEPTION.getCode(), "时间不允许为空！") ;
        }
    }

    /**
     * 时间相减
     * @param fromDate
     * @param toDate
     * @return
     */
    public static long subNew(Date fromDate, Date toDate) {
        long res = fromDate.getTime() - toDate.getTime() ;
        return res >= 0 ? res/1000 : -1 ;
    }

    /**
     * 计算丢失天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static long loseDayNew(String startDate, String endDate) {
        Date startTime = parse(startDate, 1);
        Date endTime = parse(endDate, 1);
        long res = endTime.getTime() - startTime.getTime();
        return res >= 0 ? res/(24*3600*1000) : -1;
    }

    /**
     * 比较时间大小
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return boolean
     */
    public static boolean isBeforeStr(String startDate, String endDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startTime = LocalDate.parse(startDate,dateTimeFormatter);
        LocalDate endTime = LocalDate.parse(endDate,dateTimeFormatter);

        return startTime.isBefore(endTime);
    }

    /**
     * 比较时间大小
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return boolean
     */
    public static boolean isBeforeTime(String startDate, String endDate){
        Long times = DateUtils.parse(startDate,2).getTime();
        Long endTimes = DateUtils.parse(endDate,2).getTime();
        return times<endTimes;
    }

    /**
     * 比较两个日期的先后顺序
     *
     * @param date1  日期1
     * @param date2  日期2
     * @return 返回-1表示date1在date2之前，0表示相同，1表示date1在date2之后
     */
    public static int compareDates(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return -1;
        }
        return date1.compareTo(date2);
    }


    /**
     * 比较时间大小
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return boolean
     */
    public static boolean isAfterStr(String startDate, String endDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startTime = LocalDate.parse(startDate.substring(0,10),dateTimeFormatter);
        LocalDate endTime = LocalDate.parse(endDate.substring(0,10),dateTimeFormatter);

        return startTime.isAfter(endTime);
    }

    /*
     * 验证日期格式是否符合yyyy-MM-dd
     * */
    public static boolean isDate(String date){
        /**
         * 判断日期格式和范围
         */
        //String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        String rexp1 = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
        String rexp2 = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[13579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        Pattern pat = Pattern.compile(rexp1);
        Matcher mat = pat.matcher(date);
        boolean dateType = mat.matches();
        if(!dateType){
            pat = Pattern.compile(rexp2);
            mat = pat.matcher(date);
            dateType = mat.matches();
        }
        return dateType;
    }

    /**
     * 计算丢失小时数
     * @param startDate
     * @param endDate
     * @return
     */
    public static long loseHourNew(String startDate, String endDate) {
        Date startTime = parse(startDate, 1);
        Date endTime = parse(endDate, 1);
        long res = endTime.getTime() - startTime.getTime();
        return res >= 0 ? res/(3600*1000) : -1;
    }

    /**
     * 获取今天前days天，跳过周六日
     * @param days
     * @return
     */
    public static Date getDaysAgo(Integer days) {
        LocalDate today = LocalDate.now();
        int count = 0;

        while (count < days) {
            if (!today.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                count++;
            }
            today = today.minusDays(1);
        }

        return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当前日期的下n个工作日
     * @return
     */
    public static Date getDaysWithOutWeekend(Integer n) {
        LocalDate today = LocalDate.now();
        int count = 0;

        while (count < n) {
            if (!today.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                count++;
            }
            today = today.plusDays(1);
        }

        return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 时间增加指定天数
     * @param date 日期
     * @param num 天数
     * @return 返回日期字符串
     */
    public static String reduceDate(Date date, int num, int idx) {
        // 创建 Calendar 实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 将日期减去一天
        calendar.add(Calendar.DATE, num);

        return format(calendar.getTime(), idx);
    }

    /**
     * 时间增加指定分钟
     * @param date 日期
     * @param num 分钟
     * @return 返回日期字符串
     */
    public static String reduceMinute(Date date, int num, int idx) {

        // 创建 Calendar 实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 将日期减去一天
        calendar.add(Calendar.MINUTE, num);

        return format(calendar.getTime(), idx);
    }


    /**
     * 发送机器人消息
     * @param url 机器人消息推送URL
     * @param rebotId 机器人ID
     * @param toEmpNos 消息接收人员工号
     * @param msg 消息内容
     * @param restTemplate rest模板
     */
    public static void postForEntity(String url,String rebotId,String toEmpNos,String msg, RestTemplate restTemplate) {

        Map<String, String> params = new HashMap<>();
        YTLogUtils.info("发送机器人消息开始，接收人={}", toEmpNos);
        params.put("appId", "1");
        params.put("sessionType", "r");
        params.put("contentType", "0");
        params.put("content", msg);
        params.put("from", rebotId);
        params.put("to", toEmpNos);

        if(com.alibaba.excel.util.StringUtils.isBlank(url)){
            url = "http://console.yutong.com/shake/admin/push/robot";
        }
        //提交参数设置
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        //将参数放入MultiValueMap中
        for(Map.Entry<String, String> entry : params.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            map.add(mapKey, mapValue);
        }
        // 发送post请求，并输出结果
        String result = restTemplate.postForObject(url, map, String.class);
        YTLogUtils.info("发送机器人消息结果={}", result);
    }

    /**
     * 获取当前时间的剩余秒数
     * @return 返回剩余的秒
     */
    public static long getRemainderMinute() {
        // 当前时间戳
        long currentTime = System.currentTimeMillis();
        // 获取当天的23:59:59的时间
        Date date = new Date();
        long time = parse(format(date, 1) + " 23:59:59", 2).getTime();
        //计算时间差
        long difference = time - currentTime;
        System.out.println(difference);
        //转换为秒
        return difference / 1000;
    }

    public static void main(String[] args) {
        String rebotId = "340117";  //楼宇权限
        String toEmps = "";
        String msg = "老师您好，您的研发中心12楼东、研发中心12楼西门禁已关闭，抱歉给您带来的不便";

        RestTemplate restTemplate = new RestTemplate();
        DateUtils.postForEntity("",rebotId,toEmps,msg,restTemplate);
    }
}
