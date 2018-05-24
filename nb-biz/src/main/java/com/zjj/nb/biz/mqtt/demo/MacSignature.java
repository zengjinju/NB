package com.zjj.nb.biz.mqtt.demo;

import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

public class MacSignature {

	/**
	 * 签名
	 * @param text
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public static String macSignature(String text,String secretKey) throws Exception {
		Charset charset=Charset.forName("utf-8");
		String algorithm="HmacSHA1";
		Mac mac=Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(secretKey.getBytes(charset),algorithm));
		byte[] bytes=mac.doFinal(text.getBytes(charset));
		return new String(Base64.encodeBytes(bytes));
	}

	/**
	 * 发送方签名方法
	 * @param clientId
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public static String publishSignature(String clientId,String secretKey) throws Exception{
		return macSignature(clientId,secretKey);
	}

	/**
	 * 订阅方签名方法
	 * @param topics
	 * @param clientId
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public static String subSignature(List<String> topics,String clientId,String secretKey)throws Exception{
		Collections.sort(topics);
		String topicText = "";
		for (String topic : topics) {
			topicText += topic + "\n";
		}
		String text = topicText + clientId;
		return macSignature(text, secretKey);
	}

	/**
	 * 订阅方签名方法
	 * @param topic
	 * @param clientId
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public static String subSignature(String topic,String clientId,String secretKey)throws Exception{
		return subSignature(Collections.singletonList(topic),clientId,secretKey);
	}
}
