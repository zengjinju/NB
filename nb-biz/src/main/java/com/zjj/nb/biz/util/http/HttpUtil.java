package com.zjj.nb.biz.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


/**
 * Created by jinju.zeng on 17/2/25.
 */
@Slf4j
public class HttpUtil {
    private final static String defaultCharset = "UTF-8";
    private static CloseableHttpAsyncClient httpclient = createClient();

    private static CloseableHttpAsyncClient createClient() {
        // Set HTTP params.
        // Create I/O reactor configuration
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(5).setConnectTimeout(5000).setSoTimeout(5000)
                .setSoKeepAlive(true).build();

        // Create a custom I/O reactort
        ConnectingIOReactor ioReactor;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }

        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom().setConnectionManager(connManager);

        RequestConfig defaultRequestConfig = RequestConfig.custom().setStaleConnectionCheckEnabled(true)
                //设置从主机读取数据超时（单位：毫秒）
                .setConnectTimeout(30000)
                //设置连接主机超时（单位：毫秒）
                .setSocketTimeout(30000).build();

        httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
        httpClientBuilder.setMaxConnTotal(10);
        httpClientBuilder.setUserAgent("auction-agent");

        CloseableHttpAsyncClient httpclient = httpClientBuilder.build();

        httpclient.start();

        return httpclient;
    }

    /**
     * @param url
     * @return
     */
    public static HttpClientResult getUrl(String url) {
        return getUrl(url, null);
    }

    /**
     * @param url
     * @param nvps
     * @return
     */
    public static HttpClientResult getUrl(String url, List<NameValuePair> nvps) {
        log.info("当前请求的url:" + url);
        try {
            if (nvps != null && nvps.size() > 0) {
                String params = (URLEncodedUtils.format(nvps, defaultCharset));
                url = url + "?" + params;
            }
            HttpGet httpGet = new HttpGet(url);

            Future<HttpResponse> future = httpclient.execute(httpGet, null);
            HttpResponse response = future.get();

            HttpClientResult result = new HttpClientResult();
            result.setStatusCode(response.getStatusLine().getStatusCode());

            log.info("http get url:" + url + ", status:" + result.getStatusCode());

            if (result.getStatusCode() != HttpStatus.SC_OK) {
                return result;
            }

            HttpEntity entity = response.getEntity();

            result.setResult(EntityUtils.toString(entity, defaultCharset));
            return result;
        } catch (Exception e) {
            log.error("http get error, url:" + url, e);
        }
        return null;
    }

    /**
     * 通过http get请求返回json格式的字符串,并解析成特定的Java对象
     *
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T get(String url, Class<T> clazz) {
        String jsonStr=get(url);
        if(jsonStr==null){
            return null;
        }
        T object = null;
        try {
            object = JSONObject.parseObject(jsonStr, clazz);
        } catch (Exception e) {
            log.error("json解析出错,jsonStr=", jsonStr);
        }
        return object;
    }

    public static String get(String url){
        HttpClientResult result = getUrl(url);
        if (result == null) {
            return null;
        }
        if (result.getStatusCode() != HttpStatus.SC_OK) {
            log.info("get url not 200,code:{},result:{}", result.getStatusCode(), result.getResult());
            return null;
        }
        return result.getResult();
    }

    /**
     * 使用默认的编码方式发送Post请求
     * @param url
     * @param map
     * @param headers
     * @return
     */
    public static String post(String url,Map<String,String> map,Header[] headers){
        return post(url,map,headers,defaultCharset);
    }

    /**
     * 返回json格式的数据
     *
     * @param url
     * @return
     */
    public static String post(String url, Map<String,String> map , Header[] headers,String charset) {
        if (CollectionUtils.isEmpty(map)) {
            log.info("post 请求的数据为空");
            return null;
        }
        try {
            HttpPost post = new HttpPost(url);
            if (headers != null && headers.length > 0) {
                post.setHeaders(headers);
            }
            List<NameValuePair> list=new ArrayList<>();
            for(Map.Entry<String,String> entry : map.entrySet()){
                list.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(list,charset));
            log.info("当前post请求的参数："+ JSON.toJSONString(list));
            Future<HttpResponse> result = httpclient.execute(post, null);
            HttpResponse response = result.get();
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("response code is not 200,code:{},result:{}",response.getStatusLine().getStatusCode(),response.getEntity().toString());
                return null;
            }
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, charset);
        } catch (Exception e) {
            log.error("当前请求出现未知异常，" + e);
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        List<NameValuePair> list = new ArrayList<>();
//        list.add(new BasicNameValuePair("name", "zjj"));
//        list.add(new BasicNameValuePair("id", "1"));
//        Header header=new BasicHeader("","");
//        //post("http://localhost:8080/nb/demo/post", null, null);
//        String result=post("http://172.16.20.8:449",new HashMap<String, String>(),null);
//        System.out.println(result);

        String str=get("http://192.168.0.104:8088/test/sdk-service");
//        JSONObject object=JSONObject.parseObject(str).get("data");
        JSONArray array=JSONArray.parseArray(JSONObject.parseObject(str).get("data").toString());
        System.out.println(str);
    }
}
