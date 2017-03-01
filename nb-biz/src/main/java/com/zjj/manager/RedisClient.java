package com.zjj.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinju.zeng on 17/2/28.
 */
@Service
@Slf4j
public class RedisClient {

    /**
     * 缓存时效 1分钟
     */
    public static int CACHE_EXP_MINUTE = 60;

    /**
     * 缓存时效 10分钟
     */
    public static int CACHE_EXP_MINUTES = 60 * 10;

    /**
     * 缓存时效 60分钟
     */
    public static int CACHE_EXP_HOURS = 60 * 60;

    /**
     * 缓存时效 1天
     */
    public static int CACHE_EXP_DAY = 3600 * 24;

    /**
     * 缓存时效 1周
     */
    public static int CACHE_EXP_WEEK = 3600 * 24 * 7;

    /**
     * 缓存时效 1月
     */
    public static int CACHE_EXP_MONTH = 3600 * 24 * 30 * 7;

    /**
     * 缓存时效 永久
     */
    public static int CACHE_EXP_FOREVER = 0;
    /**
     * 账务中心使用16
     */
    @Value("${redis.db.index}")
    private int DEFAULT_DB_INDEX;
    /**
     * 缓存时效 6:小时
     */
    public static int CACHE_EXP_QUARTER_DAY = 6 * 60 * 60;

    private static final List<Class<?>> SIMPLE_CLASS_OBJ = Lists.newArrayList();

    static {
        SIMPLE_CLASS_OBJ.add(Number.class);
        SIMPLE_CLASS_OBJ.add(String.class);
        SIMPLE_CLASS_OBJ.add(Boolean.class);
    }

    @Resource
    private JedisPool jedisPool;

    public Boolean set(String key, int seconds, Object value) {
        return set(DEFAULT_DB_INDEX, key, seconds, value);
    }

    private Boolean set(final int dbindex, final String key, final int seconds, final Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return false;
        }
        Object result = runTask(new CallBack() {
            @Override
            public Object call(Jedis jedis) {
                String jsonValue = JSON.toJSONString(value);
                jedis.select(dbindex);
                String result = null;
                if (seconds <= 0) {
                    result = jedis.set(key, jsonValue);
                } else {
                    result = jedis.setex(key, seconds, jsonValue);
                }
                return result;
            }
        });
        return result != null ? "OK".equals(result) : false;
    }

    public <T> T get(final String key, final Class<T> clazz) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object object = runTask(new CallBack() {
            @Override
            public Object call(Jedis jedis) {
                jedis.select(DEFAULT_DB_INDEX);
                String jsonResult = jedis.get(key);
                if (jsonResult == null) {
                    return null;
                }
                return JSON.parseObject(jsonResult, clazz);
            }
        });
        return object != null ? (T) object : null;
    }

    public <T> List<T> getList(final String key, final Class<T> clazz) {
        if (StringUtils.isEmpty(key)) {
            return new ArrayList<>();
        }
        Object object = runTask(new CallBack() {
            @Override
            public Object call(Jedis jedis) {
                jedis.select(DEFAULT_DB_INDEX);
                String result = jedis.get(key);
                if (result == null) {
                    return null;
                }
                List<T> list = JSON.parseArray(result, clazz);
                return list;
            }
        });
        return object != null ? (List<T>) object : null;
    }


    public Object runTask(CallBack callBack) {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            return callBack.call(jedis);
        } catch (JedisException e) {
            broken = handleJedisException(e);
        } catch (Exception e) {
            log.error("redis出现未知异常:" + e);
        } finally {
            closeResource(jedis, broken);
            jedis = null;
        }
        return null;
    }

    /**
     * Handle jedisException, write log and return whether the connection is broken.
     */
    private boolean handleJedisException(JedisException jedisException) {
        if (jedisException instanceof JedisConnectionException) {
            log.error("Redis connection lost.", jedisException);
        } else if (jedisException instanceof JedisDataException) {
            if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) {
                log.error("Redis connection are read-only slave.", jedisException);
            } else {
                // dataException, isBroken=false
                return false;
            }
        } else {
            log.error("Jedis exception happen.", jedisException);
        }
        return true;
    }

    /**
     * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
     */
    private void closeResource(Jedis jedis, boolean conectionBroken) {
        try {
            if (conectionBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        } catch (Exception e) {
            log.error("return back jedis failed, will fore close the  jedis.", e);
            jedis.close();
        }
    }
}
