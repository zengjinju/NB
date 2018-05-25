package com.zjj.nb.biz.mqtt.demo;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;
import java.util.List;

public class MqttConsumer {
	private MqttClient client;

	private ConnectOptions options;

	public void connect(ConnectOptions options,MqttMessageArrivedCallback callback) {
		options.checkConnectOptions();
		this.options = options;
		MqttConnectOptions connectOptions = options.convert2MqttOptions();
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			if (client == null) {
				client = new MqttClient(options.getServerURIs()[0], options.getClientId(), persistence);
			}
			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void connectComplete(boolean b, String s) {
					if (options.getSubscribeTopics() != null && options.getSubscribeTopics().length != 0) {
						if (options.getSubscribeTopics().length != options.getQos().length) {
							throw new RuntimeException();
						}
						try {
							client.subscribe(options.getSubscribeTopics(), options.getQos());
						} catch (MqttException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void connectionLost(Throwable throwable) {
				}

				@Override
				public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
					System.out.println("订阅消息：topic:"+s+",message:"+new String(mqttMessage.getPayload()));
					List<String> list = Arrays.asList(options.getSubscribeTopics());
					if (list != null && list.contains(s)) {
						callback.callBack(mqttMessage.getPayload());
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

				}
			});
			client.connect(connectOptions);
		} catch (Exception e) {

		}
	}

	public void pubMessage(byte[] bytes, int qos) {
		if (client == null) {
			throw new RuntimeException();
		}
		if (options.getPublishTopic() == null || "".equals(options.getPublishTopic())) {
			throw new RuntimeException("请选择要发布的topic");
		}
		MqttMessage message=new MqttMessage(bytes);
		message.setQos(qos);
		try {
			client.publish(options.getPublishTopic(),message);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void subMessage(){
		if(client==null){
			throw new RuntimeException();
		}
		if(options.getSubscribeTopics()==null || options.getSubscribeTopics().length==0){
			throw new RuntimeException("请选择要订阅的topic");
		}
		try {
			client.subscribe(options.getSubscribeTopics(),options.getQos());
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void subBack(byte[] bytes){
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			MqttClient publishClient = new MqttClient(options.getServerURIs()[0], options.getClientId(), persistence);
			publishClient.connect(options.convert2MqttOptions());
			MqttMessage message = new MqttMessage(bytes);
			message.setQos(0);
			//向Producer反馈消息
			publishClient.publish(options.getPublishTopic(), message);
			publishClient.close();
		}catch (Exception e){

		}
	}

}
