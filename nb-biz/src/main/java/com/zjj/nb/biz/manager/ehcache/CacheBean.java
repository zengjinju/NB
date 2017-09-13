package com.zjj.nb.biz.manager.ehcache;

/**
 * Created by admin on 2017/9/5.
 */
public interface CacheBean<T> {

     String getKey();

     T build();

    /**
     * isLazy返回false，应用启动的时候就去加载缓存
     * @return
     */
     Boolean isLazy();

}
