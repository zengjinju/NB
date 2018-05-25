package com.zjj.nb.biz;

import com.alibaba.fastjson.JSON;
import com.zjj.nb.biz.util.http.HttpUtil;

import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class Test1Class {

    private static Object object=new Object();

    public static void main(String[] args) throws Exception {
       String value="18.3";
       String end="16";
       String start="8.1";
       System.out.println(value.compareTo(end)>0);
       System.out.println(value.compareTo(start)<0);

    }

    public static void test1(){
        synchronized (object) {
            System.out.println("test1");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void test2(){
        synchronized (object) {
            System.out.println("test2");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
