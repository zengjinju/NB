package com.zjj.nb.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjj.nb.biz.jkd8stream.StreamTest;
import com.zjj.nb.biz.util.DateUtil;
import com.zjj.nb.biz.util.Jdk8DateUtil;
import com.zjj.nb.biz.util.MD5Util;
import com.zjj.nb.biz.util.http.HttpUtil;
import com.zjj.nb.biz.util.http.OkHttpUtil;
import net.sf.ehcache.util.ProductInfo;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.CRC32;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class Test1Class {

    private static final Logger logger = LoggerFactory.getLogger(Test1Class.class);

    private static Object object=new Object();
    private static final String appId= "1257141345";
    private static final String secretId ="AKIDE3v9Qj8Ne9Uh1RQKkAbJwU8iBWkrtdFg";
    private static final String secretKey ="QemwxnTyKWWKxOdgXIrEPPNAlBUbWIuK";
    private static final String baiduDetectUrl = "https://aip.baidubce.com/rest/2.0/face/v3/detect";

    public static void main(String[] args)  {
      BigDecimal item = new BigDecimal(100);
        item.add(new BigDecimal(100));
      System.out.println(item);


    }

    private static synchronized void test1(){
        System.out.println("test");
    }

    public static Long crc32(String value){
        return null;
    }

    public static class TestDto{
        Integer num;

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }
    }

}
