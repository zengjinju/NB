package com.zjj.nb.biz.mqtt.aliyun;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;

public class MqttRecvMsg {
	/**
	 * MQTT接入点
	 */
	private static final String broker = "tcp://post-cn-4590mcobv04.mqtt.aliyuncs.com:1883";

	private static final String accessKey = "LTAIPJ7NiKeFJvjK";

	private static final String secretKey = "VmJ5389bKN7rMGMprWVigpIGJpvk0N";

	private static final String topic="RUNING_HEART_STATUS_SYNC_TEST";

	private static final String GroupId="GID_STATUS_SYNC";

	private static final String clientId="GID_STATUS_SYNC@@@zjj_recv";

	public static void main(String[] args){
		MemoryPersistence persistence=new MemoryPersistence();
		try {
			MqttClient client=new MqttClient(broker,clientId,persistence);
			MqttConnectOptions connectOptions=new MqttConnectOptions();
			String sign = MacSignature.macSignature(GroupId, secretKey);
			connectOptions.setUserName(accessKey);
			connectOptions.setServerURIs(new String[]{broker});
			connectOptions.setPassword(sign.toCharArray());
			connectOptions.setCleanSession(true);
			connectOptions.setKeepAliveInterval(90);
			connectOptions.setAutomaticReconnect(true);
			int[] qos={0};
			String[] topics=new String[]{topic+"/test/"};
			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void connectComplete(boolean b, String s) {
					System.out.println("connection success");
					try {
						client.subscribe(topics,qos);
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void connectionLost(Throwable throwable) {

				}

				@Override
				public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
					System.out.println(new Date()+",message arrived,topic:"+s+",payload:"+new String(mqttMessage.getPayload()));
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
					System.out.println("messageId:"+iMqttDeliveryToken.getMessageId());
				}
			});
			client.connect(connectOptions);
			client.subscribe(topics,qos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
