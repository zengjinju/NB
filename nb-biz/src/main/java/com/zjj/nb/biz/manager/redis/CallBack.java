package com.zjj.nb.biz.manager.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by admin on 17/2/28.
 */
public interface CallBack {
    Object call(Jedis jedis);
}
