package com.zjj.nb.biz;

import java.lang.reflect.*;
import java.util.concurrent.*;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class Test1Class {

    private static volatile zjj zj;

    public static void main(String[] args) throws Exception {
        zj=new zjj();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                System.out.println(zj.getA());
            }
        });
        t.start();
        zj.setA(1);
    }

    private static class zjj{
        private volatile int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }
}
