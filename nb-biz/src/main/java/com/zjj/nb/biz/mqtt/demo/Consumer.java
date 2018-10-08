package com.zjj.nb.biz.mqtt.demo;

import com.alibaba.fastjson.JSONObject;
import com.zjj.nb.biz.mqtt.demo.*;

/**
 * 手机端
 */
public class Consumer {
	/**
	 * MQTT接入点
	 */
	private static final String broker = "tcp://post-cn-4590mcobv04.mqtt.aliyuncs.com:1883";

	private static final String accessKey = "LTAIPJ7NiKeFJvjK";

	private static final String secretKey = "VmJ5389bKN7rMGMprWVigpIGJpvk0N";

	private static final String topic = "RUNING_HEART_STATUS_SYNC_TEST";

	private static final String GroupId = "GID_lefit_device_test";

	private static final String clientId = "GID_lefit_device_test@@@zjj_recv";

	private static final String PUBLISH_TOPIC = topic + "/iphone/";

	private static final String SUBSCRIBE_TOPIC = topic + "/device/";


	public static void main(String[] args) {
		MqttConsumer consumer = new MqttConsumer();
		ConnectOptions options = new ConnectOptions();
		options.setAccessKey(accessKey);
		options.setSecretKey(secretKey);
		options.setClientId(clientId);
		options.setServerURIs(new String[]{broker});
		options.setUserName(accessKey);
		try {
			String sign = MacSignature.macSignature(GroupId, secretKey);
			options.setPassword(sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		options.setAutomaticReconnect(true);
		options.setKeepAliveInterval(90);
		options.setCleanSession(true);
		options.setPublishTopic(PUBLISH_TOPIC);
		options.setSubscribeTopics(new String[]{SUBSCRIBE_TOPIC});
		options.setQos(new int[]{0});
		consumer.connect(options, new MqttMessageArrivedCallback() {
			@Override
			public void callBack(byte[] payload) {
				JSONObject object=JSONObject.parseObject(new String(payload));
				switch (MqttMessageType.getByValue(object.getString("cmd"))){
					case VERIFICATION:
						break;
					case START_RUNNING:
						consumer.subBack(MqttMessageType.START_RUNNING_ACK.getValue(),"15757116404");
						break;
					case STOP_RUNNING:
						consumer.subBack(MqttMessageType.STOP_RUNNING_ACK.getValue(),"15757116404");
						consumer.unSubscribe();
						break;
				}
			}
		});
		consumer.subMessage();
	}

}
