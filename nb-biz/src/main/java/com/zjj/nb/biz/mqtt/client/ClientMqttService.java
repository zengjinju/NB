package com.zjj.nb.biz.mqtt.client;

import com.zjj.nb.biz.mqtt.MqttService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.List;

public class ClientMqttService implements MqttService {
	@Override
	public void close(Channel channel) {

	}

	@Override
	public void pubMessage(Channel channel, String topic, String message, int messsageId, Boolean isDup) {

	}

	@Override
	public void puhback(Channel channel, MqttMessage mqttMessage) {

	}

	@Override
	public void doTimeOut(Channel channel, IdleStateEvent evt) {
		MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
		MqttMessage mqttMessage  = new MqttMessage(fixedHeader);
		System.out.println("发送心跳");
		channel.writeAndFlush(mqttMessage);
	}

	@Override
	public void subBack(Channel channel, MqttSubscribeMessage mqttSubscribeMessage,int num) {

	}

	@Override
	public void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId) {
		MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(mqttTopicSubscriptions);
		MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE,false, MqttQoS.AT_LEAST_ONCE,false,0);
		MqttMessageIdVariableHeader mqttMessageIdVariableHeader =MqttMessageIdVariableHeader.from(messageId);
		MqttSubscribeMessage mqttSubscribeMessage = new MqttSubscribeMessage(mqttFixedHeader,mqttMessageIdVariableHeader,mqttSubscribePayload);
		channel.writeAndFlush(mqttSubscribeMessage);
	}
}
