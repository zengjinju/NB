package com.zjj.nb.biz.manager.threadPool;

import java.util.concurrent.*;

/**
 * Created by jinju.zeng on 2017/3/15.
 */
public class ThreadPool {

    private static final ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

    public static void execute(Runnable runnable){
        executorService.execute(runnable);
    }

    public static Future submit(Callable callable){
        return executorService.submit(callable);
    }
}
