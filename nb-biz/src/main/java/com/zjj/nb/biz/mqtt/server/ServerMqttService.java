package com.zjj.nb.biz.mqtt.server;

import com.zjj.nb.biz.mqtt.MqttService;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerMqttService implements MqttService {
	@Override
	public void close(Channel channel) {
		channel.close();
	}

	@Override
	public void pubMessage(Channel channel,String topic,String message,int messsageId,Boolean isDup) {
		MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,isDup, MqttQoS.AT_MOST_ONCE,false,0);
		MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(topic,messsageId);
		MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(message.getBytes(Charset.forName("utf-8"))));
		channel.writeAndFlush(mqttPublishMessage);
	}

	/**
	 * 确认消息
	 * @param channel
	 * @param mqttMessage
	 */
	@Override
	public void puhback(Channel channel, MqttMessage mqttMessage) {
		MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK,false, MqttQoS.AT_MOST_ONCE,false,0x02);
		MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(11);
		MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
		channel.writeAndFlush(mqttPubAckMessage);
	}

	@Override
	public void doTimeOut(Channel channel, IdleStateEvent evt) {
		System.out.println("心跳超时");
		channel.close();
	}

	@Override
	public void subBack(Channel channel, MqttSubscribeMessage mqttSubscribeMessage,int num) {
		MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
		MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(mqttSubscribeMessage.variableHeader().messageId());
		List<Integer> grantedQoSLevels = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
			grantedQoSLevels.add(mqttSubscribeMessage.payload().topicSubscriptions().get(i).qualityOfService().value());
		}
		MqttSubAckPayload payload = new MqttSubAckPayload(grantedQoSLevels);
		MqttSubAckMessage mqttSubAckMessage = new MqttSubAckMessage(mqttFixedHeader, variableHeader, payload);
		channel.writeAndFlush(mqttSubAckMessage);
	}

	@Override
	public void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId) {

	}
}
