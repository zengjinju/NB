package com.zjj.nb.biz.manager.ehcache;

/**
 * Created by admin on 2017/9/6.
 */
public interface ICacheProxy {

    Object getValue(String key);

    void refresh(String key);
}
