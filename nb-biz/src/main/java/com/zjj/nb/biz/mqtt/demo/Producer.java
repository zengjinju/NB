package com.zjj.nb.biz.mqtt.demo;

import com.alibaba.fastjson.JSONObject;
import com.zjj.nb.biz.mqtt.demo.ConnectOptions;
import com.zjj.nb.biz.mqtt.demo.MacSignature;
import com.zjj.nb.biz.mqtt.demo.MqttMessageType;
import com.zjj.nb.biz.mqtt.demo.MqttProducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * https://help.aliyun.com/document_detail/44874.html?spm=a2c4g.11186623.6.697.WSspQN
 * aliyun_MQTT 服务调用
 * 跑步机端
 */
public class Producer {

	/**
	 * MQTT接入点
	 */
	private static final String broker = "tcp://post-cn-4590mcobv04.mqtt.aliyuncs.com:1883";

	private static final String accessKey = "LTAIPJ7NiKeFJvjK";

	private static final String secretKey = "VmJ5389bKN7rMGMprWVigpIGJpvk0N";

	private static final String topic = "RUNING_HEART_STATUS_SYNC_TEST";

	private static final String GroupId = "GID_lefit_device_test";

	private static final String clientId = "GID_lefit_device_test@@@zjj_send_test";

	private static final String PUBLISH_TOPIC = topic + "/device/";

	private static final String SUBSCRIBE_TOPIC = topic + "/iphone/";

	private static final ExecutorService pool = Executors.newSingleThreadExecutor();

	private static volatile Boolean sub_flag = false;

	public static void main(String[] args) {
		MqttProducer producer = new MqttProducer();
		ConnectOptions options=new ConnectOptions();
		options.setUserName(accessKey);
		options.setAccessKey(accessKey);
		options.setSecretKey(secretKey);
		options.setClientId(clientId);
		options.setServerURIs(new String[]{broker});
		try {
			String sign = MacSignature.macSignature(GroupId, secretKey);
			options.setPassword(sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		options.setKeepAliveInterval(90);
		options.setCleanSession(true);
		options.setAutomaticReconnect(true);
		options.setPublishTopic(PUBLISH_TOPIC);
		options.setSubscribeTopics(new String[]{SUBSCRIBE_TOPIC});
		options.setQos(new int[]{0});
		producer.start(options, new MqttMessageArrivedCallback() {
			@Override
			public void callBack(byte[] payload) {
				try {
					JSONObject object = JSONObject.parseObject(new String(payload));
					switch (MqttMessageType.getByValue(object.getString("cmd"))) {
						case VERIFICATION_ACK:
							break;
						case START_RUNNING_ACK:
							break;
						case STOP_RUNNING_ACK:
					}
				}catch (Exception e){

				}
			}
		});
		producer.pubMessage(MqttMessageType.START_RUNNING.getValue(),"21094de3423q3",0);
		producer.subMessage();
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		producer.pubMessage(MqttMessageType.STOP_RUNNING.getValue(),"21094de3423q3",0);
	}
}
