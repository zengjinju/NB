package com.zjj.nb.biz.manager.redis;

import com.zjj.configmanager.manager.HostConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by jinju.zeng on 17/2/28.
 */
@Service
@Slf4j
public class RedisLock {

    @Autowired(required = false)
    private RedisClient redisClient;

    private int DEFAULT_DB_INDEX=Integer.parseInt(HostConfig.get("redis.db.index","1"));

    public static final long ONE_MILLI_NANOS = 1000000L;
    //默认超时时间（毫秒）
    public static final long DEFAULT_TIME_OUT = 15 * 1000;

    public static final String TIME_VALUE = "lockTime";
    public static final String BOOLEAN_VALUE = "result";

    private final Random r = new Random();
    //锁的超时时间（秒），过期删除
    public static final int EXPIRE = 4 * 60;

    public Object lock(final String key, final LockCallBack callBack) {
        if (StringUtils.isEmpty(key)) {
            log.info("lock key为空");
            return false;
        }
        Object result = redisClient.runTask(new CallBack() {
            @Override
            public Object call(Jedis jedis) {
                return lock(key, jedis, callBack);
            }
        });
        return result;
    }

    private Object lock(String key, Jedis jedis, LockCallBack callBack) {
        Map<String, Object> map = null;
        try {
            map = tryLock(key, DEFAULT_TIME_OUT, jedis);
            if ((Boolean) map.get(BOOLEAN_VALUE)) {
                return callBack.runTask();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.info("系统在执行任务时出现未知异常:" + e);
            throw e;
        } finally {
            releaseLock(jedis, key, map);
        }
        return null;
    }

    /**
     * 使用setnx()方法设置锁，
     *
     * @param key
     * @param timeOut
     * @param jedis
     * @return
     * @throws InterruptedException
     */
    private Map<String, Object> tryLock(String key, long timeOut, Jedis jedis) throws InterruptedException {
        Map<String, Object> map = new HashMap();
        long nano = System.nanoTime();
        timeOut *= ONE_MILLI_NANOS;
        jedis.select(DEFAULT_DB_INDEX);
        while ((System.nanoTime() - nano) < timeOut) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            if (jedis.setnx(key, currentTime) == 1) {
                //设置当前获取key，在redis中的超时时间
                jedis.expire(key, EXPIRE);
                map.put(BOOLEAN_VALUE, Boolean.TRUE);
                map.put(TIME_VALUE, currentTime);
                return map;
            }

            //防止死锁
            Thread.sleep(3, r.nextInt(500));
        }
        map.put(BOOLEAN_VALUE, Boolean.FALSE);
        map.put(TIME_VALUE, null);
        return map;
    }

    private void releaseLock(Jedis jedis, String key, Map<String, Object> map) {
        if ((Boolean) map.get(BOOLEAN_VALUE)) {
            String oldTime = map.get(TIME_VALUE).toString();
            String redisTime = jedis.get(key);
            /**
             * 目的是防止当一个客户端被某个操作阻塞了很长的时间,导致锁到了过期时间后自动释放
             * 然后这个客户端之后又尝试删除这个其实已经被其他客户端拿到的锁
             */
            if (oldTime != null && oldTime.equals(redisTime)) {
                jedis.del(key);
            }
        }
    }

    // 获得锁
    private Map<String, Object> acquire(Jedis jedis, String lockKey) throws InterruptedException {
        Map<String, Object> map = new HashMap<>();

        long timeout = DEFAULT_TIME_OUT;
        while (timeout > 0) {
            long expires = System.currentTimeMillis() + EXPIRE * 1000 + 1;
            String expiresStr = String.valueOf(expires); //锁到期时间

            if (jedis.setnx(lockKey, expiresStr) == 1) {
                map.put(BOOLEAN_VALUE, true);
                map.put(TIME_VALUE, expiresStr);
                //获得锁，
                return map;
            }
            String currentValueStr = jedis.get(lockKey);// redis 中存的时间
            /* a.在此时若有 服务器释放了锁，则current 为null。进行下次遍历的操作
            ** b. 锁还在，且时间比当前时间要小，却超时了。老的锁还在，且 超时
            *  c. currentValueStr == null
             */
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                //获取上一个锁到期时间，并设置现在的锁到期时间
                String oldValueStr = jedis.getSet(lockKey, expiresStr);
                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是原子性的
                // 若不相等，说明有个线程 已经早一步执行了getSet，把它时间后延一点，影响不大因此继续等待
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    map.put(BOOLEAN_VALUE, true);
                    map.put(TIME_VALUE, expiresStr);
                    //获得锁，
                    return map;
                }
                // key所在的锁已经被释放，此时被此线程设置值了，获得锁
                if (oldValueStr == null) {
                    map.put(BOOLEAN_VALUE, true);
                    map.put(TIME_VALUE, expiresStr);
                    //获得锁，
                    return map;
                }
            }
            timeout -= 100;
            // 短暂休眠，nano为了防止饥饿进程的出现
            Thread.sleep(100, r.nextInt(500));
        }
        return null;
    }


}
