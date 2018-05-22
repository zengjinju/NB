package com.zjj.nb.biz.mqtt.client;

import com.zjj.nb.biz.mqtt.MqttService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientMqttService implements MqttService {
	@Override
	public void close(Channel channel) {

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
}
