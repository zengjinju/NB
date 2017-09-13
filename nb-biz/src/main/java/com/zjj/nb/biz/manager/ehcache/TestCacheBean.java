package com.zjj.nb.biz.manager.ehcache;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/9/6.
 */
@Service
public class TestCacheBean implements CacheBean<Map<String,String>> {
    public static final String CACHE_KET="test_key";
    @Override
    public String getKey() {
        return CACHE_KET;
    }

    @Override
    public Map<String, String> build() {
        Map<String,String> map=new HashMap<>();
        map.put("zjj","zjj");
        return map;
    }

    @Override
    public Boolean isLazy() {
        return false;
    }
}
