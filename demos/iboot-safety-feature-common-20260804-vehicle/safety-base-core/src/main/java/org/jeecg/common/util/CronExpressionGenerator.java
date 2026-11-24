package org.jeecg.common.util;

import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDate;
import java.time.YearMonth;

public class CronExpressionGenerator {

    /**
     * 生成CRON表达式：每月指定月份、指定日期、时分执行
     * cron格式：秒 分 时 日 月 星期
     *
     * @param month  目标月份 1-12
     * @param day    每月日期 1-31
     * @param hour   小时 0-23
     * @param minute 分钟 0-59
     * @return 标准6位cron字符串
     */
    public static String generateMonthDayCron(int month, int day, int hour, int minute) {
        // 参数合法性校验
        checkParam(month, day, hour, minute);
        // 秒固定0，分、时、日、传入月，星期?（不区分星期）
        // 示例：0 30 10 25 6 ?  代表每年6月25日10:30执行
        return String.format("0 %d %d %d %d ?", minute, hour, day, month);
    }

    /**
     * 生成每日定点执行CRON（不限制月日，每天固定时分）
     *
     * @param hour   小时
     * @param minute 分钟
     * @return cron
     */
    public static String generateDailyCron(int hour, int minute) {
        checkHourMinute(hour, minute);
        // 每日：日*,月*,星期?
        return String.format("0 %d %d * * ?", minute, hour);
    }

    /**
     * 参数校验：月、日、时、分合法
     */
    private static void checkParam(int month, int day, int hour, int minute) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("月份必须在1-12之间");
        }
        // 校验当月最大天数，避免2月30、4月31这种非法日期
        int maxDay = YearMonth.of(LocalDate.now().getYear(), month).lengthOfMonth();
        if (day < 1 || day > maxDay) {
            throw new IllegalArgumentException(month + "月不存在" + day + "日，最大天数：" + maxDay);
        }
        checkHourMinute(hour, minute);
    }

    /**
     * 校验时分
     */
    private static void checkHourMinute(int hour, int minute) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("小时必须在0-23之间");
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("分钟必须在0-59之间");
        }
    }

    /**
     * 校验CRON表达式是否合法
     */
    public static boolean isValidCron(String cron) {
        if (cron == null || cron.trim().isEmpty()) {
            return false;
        }
        return CronExpression.isValidExpression(cron);
    }

    // 测试主方法
    public static void main(String[] args) {
        // 示例1：6月25日 10:30 执行
        String cron1 = generateMonthDayCron(6, 25, 10, 30);
        System.out.println("任务1 cron：" + cron1); // 输出：0 30 10 25 6 ?

        // 示例2：12月1日 14:00 执行
        String cron2 = generateMonthDayCron(12, 1, 14, 0);
        System.out.println("任务2 cron：" + cron2); // 输出：0 0 14 1 12 ?

        // 示例3：每日15:20执行
        String dailyCron = generateDailyCron(15, 20);
        System.out.println("每日定时cron：" + dailyCron); // 输出：0 20 15 * * ?
    }
}
