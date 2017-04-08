package com.zjj.nb.biz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by admin on 17/2/26.
 */
public class LockMainTest {

    private static ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            //t.setDaemon(true);
            return t;
        }
    });
    private static Integer j = 0;

    public static void main(String[] args) {
        final LockTest lock=new LockTest();
        for(int i=0;i<100000;i++){
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                        j++;
                    }finally {
                        lock.unlock();
                    }
                }
            });
        }
    }
}
