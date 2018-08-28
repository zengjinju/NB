package com.zjj.nb.biz.util.http;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpUtil.class);

	private static final OkHttpClient httpClient;
	static{
		httpClient=new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(1000,TimeUnit.MILLISECONDS)
				.sslSocketFactory(createSSLSocketFactory()) //添加对HTTPS的支持
				.hostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String s, SSLSession sslSession) {
						return true;
					}
				})
				.build();
	}

	/**
	 * 通用POST请求
	 * @param url
	 * @param jsonString
	 * @return
	 */
	public static String post(String url,String jsonString){
		byte[] bytes=postBytes(url,jsonString);
		if(bytes==null || bytes.length==0){
			return null;
		}
		String result=new String(bytes);
		LOGGER.info("response body"+result);
		return result;
	}

	/**
	 *
	 * @param url
	 * @param jsonString
	 * @return
	 */
	public static byte[] postBytes(String url,String jsonString) {
		if(StringUtils.isEmpty(url)){
			return null;
		}
		LOGGER.info("请求信息url:"+url+",param:"+jsonString);
		RequestBody body=RequestBody.create(MediaType.parse("application/json"),jsonString);
		Request request=new Request.Builder()
				.url(url)
				.post(body)
				.build();

		try {
			return httpClient.newCall(request).execute().body().bytes();
		} catch (IOException e) {
			throw new RuntimeException(e.getCause());
		}
	}

	public static byte[] downLoadPic(String url){
		Request request=new Request.Builder()
				.url(url)
				.build();
		try {
			return httpClient.newCall(request).execute().body().bytes();
		} catch (IOException e) {
			throw new RuntimeException(e.getCause());
		}
	}

	/**
	 * 通用get请求
	 * @param url
	 * @return
	 */
	public static String get(String url){
		if(StringUtils.isEmpty(url)){
			return null;
		}
		Request request = new Request.Builder()
				.url(url)
				.build();
		try {
			String result = httpClient.newCall(request).execute().body().string();
			LOGGER.info("response body:"+result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通用请求
	 * @param jsonString
	 * @param servicePubKey
	 * @param serviceUrl
	 * @param acceountKey
	 * @return
	 */
	private String post(String jsonString ,String servicePubKey ,String serviceUrl , String acceountKey) {

		String timestampUTC = System.currentTimeMillis() / 1000 + "";
		String random = "67098230";

		String signature = signature(jsonString, timestampUTC, random, servicePubKey);

		LOGGER.info("request body: {}", jsonString);
		okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json"), jsonString);

		Request request = new Request.Builder()
				.url(serviceUrl)
				.addHeader("Signature", signature)
				.addHeader("Account-Key", acceountKey)
				.addHeader("UTC-Timestamp", timestampUTC)
				.addHeader("random", random)
				.post(body)
				.build();
		try {
			String string = httpClient.newCall(request).execute().body().string();
			LOGGER.info("response body: {}", string);
			return string;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String postAI(String url,String param,String authorization){
		if(url == null || "".equals(url)){
			return null;
		}
		String result = null;
		RequestBody body = RequestBody.create(MediaType.parse("application/json"),param);
		Request request = new Request.Builder()
				.url(url)
				.addHeader("Authorization",authorization)
				.addHeader("Host","recognition.image.myqcloud.com")
//				.addHeader("Content-Type","application/json")
				.addHeader("Content-Type","multipart/form-data")
				.post(body)
				.build();
		try{
			 result= httpClient.newCall(request).execute().body().string();
			LOGGER.info("response body:"+result);
		}catch (Exception e){
			LOGGER.error("未知异常",e);
		}
		return result;
	}

	/**
	 * 百度API
	 * @param url
	 * @param token
	 * @param param
	 * @return
	 */
	public static String postBaiduAi(String url,String token,String param){
		if(url == null || "".equals(url)){
			return null;
		}
		url = url + "?access_token="+token;
		String result = null;
		RequestBody body = RequestBody.create(MediaType.parse("application/json"),param);
		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type","application/json")
				.post(body)
				.build();
		try{
			result= httpClient.newCall(request).execute().body().string();
			LOGGER.info("response body:"+result);
		}catch (Exception e){
			LOGGER.error("未知异常",e);
		}
		return result;
	}


	public static String signature(String bodyString, String timestampUTC, String random, String accountKey) {

		String builder = bodyString +
				timestampUTC +
				random +
				accountKey;
		return signature(builder);
	}

	private static String signature(String string) {
		char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		String signature = "";
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(string.getBytes("UTF-8"));
			byte[] md = mdTemp.digest();
			int j = md.length;
			char[] buf = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			signature = new String(buf);

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return signature;
	}

	private static SSLSocketFactory createSSLSocketFactory(){
		SSLSocketFactory sslSocketFactory=null;
		try {
			SSLContext sc=SSLContext.getInstance("SSL");
			sc.init(null,new TrustManager[]{new TrustAllCerts()},new SecureRandom());
			sslSocketFactory=sc.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return sslSocketFactory;
	}

	private static class TrustAllCerts implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

		@Override
		public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
	}
}
