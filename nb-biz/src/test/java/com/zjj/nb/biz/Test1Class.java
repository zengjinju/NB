package com.zjj.nb.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjj.nb.biz.util.DateUtil;
import com.zjj.nb.biz.util.MD5Util;
import com.zjj.nb.biz.util.http.HttpUtil;
import com.zjj.nb.biz.util.http.OkHttpUtil;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
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
        try{
            Double d = Double.parseDouble("a");
        }catch (Exception e){
            logger.error("error key={}",11,e);
        }

    }

    public static Long crc32(String value){
        return null;
    }

//    public void createOrder(String productNo){
//        if(productNo == null || "".equals(productNo)){
//            return;
//        }
//        //获取商品信息
//        ProductInfo productInfo = getItemInfo(productNo);
//        if(productInfo == null){
//            return;
//        }
//        //整理订单信息
//        OrderInfo orderInfo = convert2OrderInf(productInfo);
//        //插入订单并减库存
//        insertRecord(orderInfo);
//        reduceStore(productNo);
//    }

}
