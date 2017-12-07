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
       //System.out.println(HttpUtil.get("http://sdkv2.linkingcloud.cn/#a_c6c5d23b25b444fa94c70e4d0dd199fa"));
        TestDO testDO=new TestDO();
        testDO.setHostName("172.16.21.218");
        testDO.setSignPort("449");
        testDO.setTransferPort("448");
        testDO.setCis("120290001066991");
        testDO.setId("201701.y.1202");
        testDO.setBankCode("ICBC");
        testDO.setBankNo("102");
        testDO.setBankName("中国工商银行");
        testDO.setBankCardName("时贱醇恍剃牙桔好猜迹灿描寂件粉");
        testDO.setBankCardNo("1202022719927388888");
        System.out.println(JSON.toJSONString(testDO));
        System.out.println(1&7);
        System.out.println(3&7);
        System.out.println(4&7);
        System.out.println(6&7);
        System.out.println(8&7);

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
