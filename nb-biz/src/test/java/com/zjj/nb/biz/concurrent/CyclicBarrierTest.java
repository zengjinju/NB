package com.zjj.nb.biz.concurrent;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jinju.zeng on 2017/3/21.
 */
public class CyclicBarrierTest {
    public static void main(String[] args) {
        final AtomicInteger sum=new AtomicInteger(0);
        //设置100000个互相等待的线程,当最后一个线程到达屏障点时该线程会去执行barrier中的Runnable任务
        final CyclicBarrier barrier=new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                System.out.println(sum.get());
            }
        });
        ExecutorService service= Executors.newFixedThreadPool(3);
        for(int i=0;i<3;i++){
            service.execute(new Runnable() {
                @Override
                public void run() {
                    sum.incrementAndGet();
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
