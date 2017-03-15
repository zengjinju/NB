package com.zjj.manager.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

/**
 * Created by jinju.zeng on 2017/3/15.
 */
public class ThreadPool {

    private static final ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

    public static void execute(Runnable runnable){
        executorService.execute(runnable);
    }
}
