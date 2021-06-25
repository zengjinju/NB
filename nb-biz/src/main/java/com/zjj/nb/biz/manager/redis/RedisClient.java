package com.zjj.nb.biz.manager.redis;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zjj.configmanager.manager.HostConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
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
     * 缓存时效 6:小时
     */
    public static int CACHE_EXP_QUARTER_DAY = 6 * 60 * 60;

    private int DEFAULT_DB_INDEX= Integer.parseInt(HostConfig.get("redis.db.index","1"));

    private static final List<Class<?>> SIMPLE_CLASS_OBJ = Lists.newArrayList();

    static {
        SIMPLE_CLASS_OBJ.add(Number.class);
        SIMPLE_CLASS_OBJ.add(String.class);
        SIMPLE_CLASS_OBJ.add(Boolean.class);
    }

    @Autowired(required = false)
    private JedisPool jedisPool;

    public Boolean set(String key, int seconds, Object value) {
        return set(DEFAULT_DB_INDEX, key, seconds, value);
    }

    private Boolean set(final int dbindex, final String key, final int seconds, final Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            log.info("在设置redis缓存时key或者value的值为空");
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
            log.info("获取redis缓存中的内容时key为空");
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

    /**
     * 删除
     * @param key
     */
    public void del(final String key){
        runTask(new CallBack() {
            @Override
            public Object call(Jedis jedis) {
                jedis.select(DEFAULT_DB_INDEX);
                return jedis.del(key);
            }
        });
    }

    /**
     * 实现分布式锁(set key value EX NX)
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public Boolean setExpireNx(final String key, final int seconds, final String value){
        Object obj=runTask(new CallBack() {
            @Override
            public Object call(Jedis jedis) {
                jedis.select(DEFAULT_DB_INDEX);
                String result = jedis.set(key,value,"NX","EX",seconds);
                return "OK".equals(result);
            }
        });
        return (Boolean)obj;
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
            log.error("返回jedis资源异常，将强制关闭jedis", e);
            jedis.close();
        }
    }

    public void batchSet(String value,String ... keys){
        runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            Pipeline pipe = jedis.pipelined();
            for (String key : keys){
                pipe.set(key,value);
            }
            pipe.sync();
            return true;
        });
    }

    public List<Object> batchGet(String ... keys){
        Object result = runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            Pipeline pipe = jedis.pipelined();
            for (String key : keys){
                pipe.get(key);
            }
            return pipe.syncAndReturnAll();
        });
        return (List<Object>) result;
    }

    /**
     * 基于HyperLogLog的大量数据统计
     * @param key
     * @param values
     */
    public void hyperLogAdd(String key,String ... values){
        Object result = runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            return jedis.pfadd(key,values);
        });
    }

    public Long hyperLogCount(String key){
        Object result = runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            return jedis.pfcount(key);
        });
        return (Long) result;
    }

    /**
     * HyperLogLog的多key合并
     * @param keys
     */
    public String  hyperLogUnion(String destination ,String ... keys){
        Object result = runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            return jedis.pfmerge(destination,keys);
        });
        return result.toString();
    }


    /**
     * bitmap的二值状态统计
     *
     * 使用用例 https://mp.weixin.qq.com/s/MbrEkyiFnHq_5f9M3pWZeg
     */
    public void setBit(String key,long offset,String value){
        Object result = runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            return jedis.setbit(key,offset,value);
        });
    }

    /**
     * 返回给定的bit数组中值为1的数量
     * @param key
     * @return
     */
    public long bitCount(String key){
        Object result = runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            return jedis.bitcount(key);
        });
        return (Long)result;
    }

    /**
     * 返回数据表示 Bitmap 中第一个值为 bitValue 的 offset 位置
     * @param key
     * @param startOffset
     * @param endOffset
     * @param bitValue
     * @return
     */
    public long bitTop(String key,Long startOffset,Long endOffset,Boolean bitValue){
        Object result = runTask((Jedis jedis)->{
            jedis.select(DEFAULT_DB_INDEX);
            if (startOffset != null && endOffset == null){
                return jedis.bitpos(key,bitValue,new BitPosParams(startOffset));
            } else if (startOffset != null && endOffset != null){
                return jedis.bitpos(key,bitValue,new BitPosParams(startOffset,endOffset));
            } else {
                return jedis.bitpos(key,bitValue);
            }
        });
        return (Long)result + 1;
    }
}
