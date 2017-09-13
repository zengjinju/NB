package com.zjj.nb.biz.manager.ehcache;

import com.zjj.nb.biz.util.applicationcontext.ApplicationContextHelper;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2017/9/6.
 */
@Slf4j
public class CacheProxy implements ICacheProxy, InitializingBean {
    private static final Map<String,CacheBean> map=new ConcurrentHashMap<>();
    private static final Map<String,CacheBean> lazyMap=new ConcurrentHashMap<>();
    private Cache cache;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,CacheBean> cacheBeanMap=ApplicationContextUtil.getBeanByType(CacheBean.class);
        if(StringUtils.isEmpty(cacheBeanMap)){
            log.error("the instance of CacheBean interface not exist");
            return;
        }
        for(CacheBean cacheBean : cacheBeanMap.values()){
            if(cacheBean.isLazy()){
                lazyMap.put(cacheBean.getKey(),cacheBean);
                continue;
            }
            map.put(cacheBean.getKey(),cacheBean);
            cache(cacheBean);
        }
    }

    private void cache(CacheBean bean){
        Element element=new Element(bean.getKey(),bean.build());
        cache.put(element);
        lazyMap.remove(bean.getKey());
    }


    @Override
    public Object getValue(String key) {
        if(!map.containsKey(key)){
            log.info("the current key not exit in cache");
            return null;
        }
        if(lazyMap.containsKey(key)){
            cache(lazyMap.get(key));
        }
        Element element=cache.get(key);
        if(element==null){
            log.info("the current key not exit in cache or cache invalid");
            return null;
        }
        return element.getObjectValue();
    }

    @Override
    public void refresh(String key) {
        if(map.containsKey(key)){
            cache(map.get(key));
        }
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
