package com.zjj.nb.biz.manager.localcache;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinju.zeng on 2017/6/7.
 * 使用guava cache 做全内存的本地缓存操作
 */
public abstract class AbstractCacheService implements CacheService {

    private static final int MAXIMUM_SIZE=200;

    protected LoadingCache<String,Optional> cacheLoader;

    @PostConstruct
    public void beforeInit(){
        cacheLoader= CacheBuilder.newBuilder()
                .maximumSize(MAXIMUM_SIZE)//最大缓存数
                .refreshAfterWrite(10, TimeUnit.MINUTES)//缓存时间10分钟
                .build(new CacheLoader<String, Optional>() {
                    @Override
                    public Optional load(String key) throws Exception {
                        return Optional.fromNullable(process(key));
                    }
                });
    }

    public Object process(String key){
        return doProcess(key);
    }
}
