package com.zjj.nb.biz.manager.localcache;

/**
 * Created by admin on 2017/6/7.
 */
public interface CacheService {
    /**
     * 返回具体的缓存对象
     * @param key
     * @return
     */
     Object doProcess(String key);

    /**
     * 如果缓存中存在key对应的值则返回，否则将新的<key,value>添加到缓存中并返回这个值
     * @param key
     * @return
     */
    Object get(String key);
}
