package org.jeecg.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 业务编码生成器
 * <p>编码格式：{PREFIX}{yyyyMMdd}{NNNN}，例：XFYH202606290001</p>
 * <p>序号每日重置，支持多实例分布式部署</p>
 */
@Component
@Slf4j
public class CodeGeneratorUtil {

    private static final String CODE_KEY_PREFIX = "ytsafety:hazard:report:code:";
    private static final int SEQ_PAD = 4;
    private static final long SEQ_MAX = 9999;

    @Resource
    private RedisUtil redisUtil;
    /**
     * 生成业务编码
     *
     * @param prefix 编码前缀，如 "XFYH"
     * @return 生成的编码，如 "XFYH202606290001"
     */
    public String generate(String prefix) {
        String dateStr = DateUtils.yyyyMMdd.get().format(new Date());
        String redisKey = CODE_KEY_PREFIX + prefix + ":" + dateStr;

        long seq = redisUtil.incr(redisKey, 1);
        if (seq == 1) {
            redisUtil.expire(redisKey, calcTodayRemainingSeconds());
        }

        if (seq > SEQ_MAX) {
            throw new RuntimeException(
                String.format("编码序号溢出, prefix=%s, date=%s, maxSeq=%d", prefix, dateStr, SEQ_MAX));
        }

        return prefix + dateStr + padSeq(seq);
    }

    private String padSeq(long seq) {
        return StringUtils.leftPad(String.valueOf(seq), SEQ_PAD, '0');
    }

    /**
     * 计算当天剩余秒数（含当前秒）
     */
    private long calcTodayRemainingSeconds() {
        long nowMillis = System.currentTimeMillis();
        long dayMillis = 24L * 3600 * 1000;
        long todayStart = (nowMillis / dayMillis) * dayMillis;
        long tomorrowStart = todayStart + dayMillis;
        return (tomorrowStart - nowMillis) / 1000 + 1;
    }
}
