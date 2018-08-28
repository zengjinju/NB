package com.zjj.nb.biz.mqtt.demo;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MqttProducer {

	private MqttClient client;

	private ConnectOptions options;


	public void start(ConnectOptions options,MqttMessageArrivedCallback callback) {
		options.checkConnectOptions();
		this.options = options;
		MqttConnectOptions connectOptions = options.convert2MqttOptions();
		MemoryPersistence persistence = new MemoryPersistence();
		try {
			if (client == null) {
				client = new MqttClient(options.getServerURIs()[0], options.getClientId(), persistence);
			}
			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void connectComplete(boolean b, String s) {
					if (options.getSubscribeTopics() != null && options.getSubscribeTopics().length != 0) {
						try {
							if (options.getQos().length != options.getSubscribeTopics().length) {
								throw new RuntimeException();
							}
							//上传客户端的订阅关系
							client.subscribe(options.getSubscribeTopics(), options.getQos());
						} catch (MqttException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void connectionLost(Throwable throwable) {
					System.out.println("close");
				}

				@Override
				public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
					System.out.println("订阅消息：topic:"+s+",message:"+new String(mqttMessage.getPayload()));
					List<String> topicList = Arrays.asList(options.getSubscribeTopics());
					//接收到来自订阅消息的响应
					if (topicList.size() != 0 && topicList.contains(s)) {
						//TODO，这里可以是跑步机启动跑步或者是跑步机结束跑步
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

	public void close() {
		if (client != null) {
			try {
				client.close();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}

	public void pubMessage(String messageType,String payload, int qos) {
		System.out.println("发送消息成功");
		if (client == null) {
			throw new RuntimeException();
		}
		if(options.getPublishTopic()==null || "".equals(options.getPublishTopic())){
			throw new RuntimeException("请选择要发布的topic");
		}
		try {
			JSONObject object=new JSONObject();
			object.put("cmd",messageType);
			object.put("value",payload);
			MqttMessage mqttMessage = new MqttMessage(object.toJSONString().getBytes());
			mqttMessage.setQos(qos);
			client.publish(options.getPublishTopic(),mqttMessage);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void subMessage(){
		if(client==null){
			throw new RuntimeException();
		}
		if(options.getSubscribeTopics()==null || options.getSubscribeTopics().length==0){
			throw new RuntimeException("请选择要订阅的Topics");
		}
		try {
			client.subscribe(options.getSubscribeTopics(),options.getQos());
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void unSubscribe(){
		if(client==null){
			throw new RuntimeException();
		}
		if(options.getSubscribeTopics()==null || options.getSubscribeTopics().length==0){
			throw new RuntimeException("请选择要订阅的Topics");
		}
		try{
			client.unsubscribe(options.getSubscribeTopics());
		}catch (Exception e){

		}
	}
}
