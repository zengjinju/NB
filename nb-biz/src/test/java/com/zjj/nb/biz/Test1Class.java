package com.zjj.nb.biz;

import java.lang.reflect.*;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class Test1Class {


    public static void main(String[] args) throws Exception {
        final String a="hello";
        final String b=" world";
        String c=a+b;
        String d="hello world";
        System.out.println(c==d);
    }
}
