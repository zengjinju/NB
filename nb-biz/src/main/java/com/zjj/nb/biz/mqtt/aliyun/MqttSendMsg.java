package com.zjj.nb.biz.mqtt.aliyun;

import com.zjj.nb.biz.util.DateUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;

/**
 * https://help.aliyun.com/document_detail/44874.html?spm=a2c4g.11186623.6.697.WSspQN
 * aliyun_MQTT 服务调用
 */
public class MqttSendMsg {

	/**
	 * MQTT接入点
	 */
	private static final String broker = "tcp://post-cn-4590mcobv04.mqtt.aliyuncs.com:1883";

	private static final String accessKey = "LTAIPJ7NiKeFJvjK";

	private static final String secretKey = "VmJ5389bKN7rMGMprWVigpIGJpvk0N";

	private static final String topic = "RUNING_HEART_STATUS_SYNC_TEST";

	private static final String GroupId = "GID_STATUS_SYNC";

	private static final String clientId = "GID_STATUS_SYNC@@@zjj_send";

	public static void main(String[] args) {
		MemoryPersistence persistence = new MemoryPersistence();
		try {
			MqttClient client = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connectOptions = new MqttConnectOptions();
			String sign = MacSignature.macSignature(GroupId, secretKey);
			connectOptions.setUserName(accessKey);
			connectOptions.setServerURIs(new String[]{broker});
			connectOptions.setPassword(sign.toCharArray());
			connectOptions.setCleanSession(true);
			connectOptions.setKeepAliveInterval(90);
			connectOptions.setAutomaticReconnect(true);
			String content= DateUtil.parseDateToString(new Date())+"mqtt test hello world";
			MqttMessage message=new MqttMessage(content.getBytes());
			message.setQos(0);
			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void connectComplete(boolean b, String s) {
					System.out.println("connection complete");
//					try {
//						client.publish(topic+"/test",message);
//					} catch (MqttException e) {
//						e.printStackTrace();
//					}
				}

				@Override
				public void connectionLost(Throwable throwable) {
					System.out.println("connection lost");
				}

				@Override
				public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
					System.out.println("topic:" + s + ",payload:" + new String(mqttMessage.getPayload()));
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
					System.out.println("messageId:"+iMqttDeliveryToken.getMessageId());
				}
			});
			client.connect(connectOptions);
			client.publish(topic+"/test/",message);
			System.out.println("message publish complete");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
