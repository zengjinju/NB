package com.zjj.nb.biz.mqtt.server;

import com.zjj.nb.biz.mqtt.MqttHandler;
import com.zjj.nb.biz.mqtt.MqttService;
import com.zjj.nb.biz.util.IPUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultMqqHander extends MqttHandler {


	public static final ConcurrentHashMap<String,String> TOPIC_MESSAGE_MAP=new ConcurrentHashMap<>(512);

	public DefaultMqqHander(MqttService mqttService) {
		super(mqttService);
	}

	@Override
	public void doMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
		MqttFixedHeader header = mqttMessage.fixedHeader();
		switch (header.messageType()) {
			case CONNECT:
				System.out.println("连接成功：" + IPUtils.getHost());
				break;
			case PINGREQ:
				System.out.println("ping req");
				break;
			case PUBLISH:
				MqttPublishMessage message = (MqttPublishMessage) mqttMessage;
				MqttPublishVariableHeader variableHeader=message.variableHeader();
				String topic=variableHeader.topicName();
				ByteBuf byteBuf = message.payload();
				byte[] bytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				String val = new String(bytes);
				TOPIC_MESSAGE_MAP.put(topic,val);
				mqttService.puhback(ctx.channel(),mqttMessage);
				break;
			case SUBSCRIBE:
				MqttSubscribeMessage mqttSubscribeMessage=(MqttSubscribeMessage)mqttMessage;
				MqttTopicSubscription topicSubscription=mqttSubscribeMessage.payload().topicSubscriptions().get(0);
				String topicName=topicSubscription.topicName();
				String value=TOPIC_MESSAGE_MAP.get(topicName);
				mqttService.pubMessage(ctx.channel(),topicName,value,1,false);
				break;

		}
	}
}
