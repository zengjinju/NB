package com.zjj.nb.biz.mqtt.server;

import com.zjj.nb.biz.mqtt.MqttService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerMqttService implements MqttService {
	@Override
	public void close(Channel channel) {
		channel.close();
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
}
