package com.zjj.nb.controller;

import com.zjj.nb.biz.manager.localcache.CacheService;
import com.zjj.nb.biz.manager.redis.BloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinju.zeng on 2017/5/26.
 */
@Controller
@RequestMapping("demo2")
public class DemoTest2Controller {

    /**
     * ThreadLocal的使用
     */
    private static final ThreadLocal<Map<String,Object>> THREAD_LOCAL=new ThreadLocal<>();

    @Autowired
    private CacheService cacheService;
    @Autowired
    private BloomFilter bloomFilter;


    @RequestMapping("out1")
    @ResponseBody
    public Object out(){
        try {
            Map<String, Object> map = getMap();
            map.put("zjj", 1);
            return map;
        }finally {
            THREAD_LOCAL.remove();
        }
    }

    @RequestMapping("out2")
    @ResponseBody
    public Object out2(){
        try {
            Map<String, Object> map = getMap();
            map.put("zjj1", 2);
            return map;
        }finally {
            THREAD_LOCAL.remove();
        }
    }

    private Map<String,Object> getMap(){
        Map<String,Object> map=THREAD_LOCAL.get();
        if(map==null){
            map=new HashMap<>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    @RequestMapping("cache")
    public void cache(){
        System.out.println(cacheService.get("zjj"));
        System.out.println(cacheService.get("zjj"));
        System.out.println(cacheService.get("zjj"));
    }

    @RequestMapping("testBoolm")
    public void test_boolm(){
        Boolean flag = bloomFilter.isExit("bloom_redis","TestExample");
        System.out.println(flag);
    }

}
