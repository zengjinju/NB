package com.zjj.nb.biz.manager.localcache;

import com.google.common.base.Optional;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * Created by admin on 2017/6/7.
 */
@Service
public class CacheServiceImpl extends AbstractCacheService {


    @Override
    public Object doProcess(String key) {
        return "zjj";
    }

    @Override
    public Object get(String key) {
        try {
            Optional optional=cacheLoader.get(key);
            return optional.isPresent()?optional.get():null;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
