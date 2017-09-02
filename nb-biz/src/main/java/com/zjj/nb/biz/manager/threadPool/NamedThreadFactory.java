package com.zjj.nb.biz.manager.threadPool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jinju.zeng on 2017/8/30.
 */
public class NamedThreadFactory implements ThreadFactory {

    private String name;
    //记录线程的编号
    private AtomicInteger threadNum = new AtomicInteger(1);
    private Boolean demo;

    public NamedThreadFactory() {
        this("pool", false);
    }

    public NamedThreadFactory(String name, Boolean demo) {
        this.name = name;
        this.demo = demo;
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = name + "-thread-" + threadNum.getAndIncrement();
        Thread ret = new Thread(r, threadName);
        ret.setDaemon(demo);
        return null;
    }
}
