package com.zjj.nb.biz.mqtt.server;

import com.zjj.nb.biz.mqtt.MqttHandler;
import com.zjj.nb.biz.mqtt.MqttService;
import com.zjj.nb.biz.util.IPUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import java.net.InetSocketAddress;

public class DefaultMqqHander extends MqttHandler {


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
				ByteBuf byteBuf = message.payload();
				byte[] bytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				String val = new String(bytes);
				String clientIp=((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
				System.out.println(clientIp+",客户端信息："+val);
				mqttService.puhback(ctx.channel(),mqttMessage);
				break;
		}
	}
}
