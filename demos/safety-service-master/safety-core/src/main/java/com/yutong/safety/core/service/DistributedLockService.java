package com.yutong.safety.core.service;

import com.yutong.safety.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DistributedLockService {

    @Autowired
    private RedisUtil redisUtil;

    private static final String LOCK_PREFIX = "lock:safety:";
    private static final long LOCK_TTL = 30 * 1000; // 锁的超时时间

    /**
     * 尝试获取锁
     * @param lockKey 锁的唯一标识符
     * @return 是否成功获取锁
     */
    public boolean tryLock(String lockKey) {
        return redisUtil.set(LOCK_PREFIX + lockKey, "1", LOCK_TTL);
    }

    /**
     * 释放锁
     * @param lockKey 锁的唯一标识符
     */
    public void unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        if (redisUtil.hasKey(key)) {
            redisUtil.del(key);
        }
    }

    /**
     * 加锁执行业务逻辑并释放锁
     * @param lockKey 锁的唯一标识符
     */
    public void performBusinessLogic(String lockKey) {
        boolean isLocked = tryLock(lockKey);

        if (isLocked) {
            try {
                // 执行业务逻辑
                System.out.println("Acquired the lock and performing business logic...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                unlock(lockKey); // 释放锁
            }
        } else {
            System.out.println("Failed to acquire the lock.");
        }
    }
}
