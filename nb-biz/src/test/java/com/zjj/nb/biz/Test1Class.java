package com.zjj.nb.biz;

import java.lang.reflect.*;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class Test1Class {


    public static void main(String[] args) throws Exception {
        String str1=new StringBuilder("abc").append("efg").toString();
        String str2="abcefg";
        System.out.println(str1.intern()==str2);
    }
}
