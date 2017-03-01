package com.zjj;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 17/2/25.
 */
public class CurrentTest {

    private static ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            //t.setDaemon(true);
            return t;
        }
    });

    public static void main(String[] args) throws InterruptedException {
        final AtomicInteger sum = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(1000000);
        for (int i = 0; i < 1000000; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
//                    sum.incrementAndGet();
                    for (; ; ) {
                        int c = sum.get();
                        int next = c + 2;
                        if (sum.compareAndSet(c, next)) {
                            break;
                        }
                    }
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println(sum);
        for(;;) {
            if (sum.get() == 2000000) {
                break;
            }
        }
    }
}
