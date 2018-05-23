package com.zjj.nb.biz.mqtt.client;

import com.zjj.nb.biz.mqtt.MqttHandler;
import com.zjj.nb.biz.mqtt.MqttService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;

import java.util.List;

public class DefaultMqHandler extends MqttHandler {

	private NettyBootstrapClient client;

	public DefaultMqHandler(MqttService mqttService,NettyBootstrapClient client) {
		super(mqttService);
		this.client=client;
	}

	@Override
	public void doMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
		MqttFixedHeader header = mqttMessage.fixedHeader();
		switch (header.messageType()){
			case UNSUBACK:
				System.out.println("unsuback");
				break;
			case CONNACK:
				System.out.println("connack");
				break;
			case PINGRESP:
				System.out.println("心跳消息");
				break;
			case PUBACK :
				System.out.println("服务的响应");
				break;
			case SUBACK:
				MqttSubAckMessage subAckMessage=(MqttSubAckMessage)mqttMessage;
				List<Integer> list=subAckMessage.payload().grantedQoSLevels();
				System.out.println(list.get(0));
				break;
			case PUBLISH:
				MqttPublishMessage publishMessage=(MqttPublishMessage)mqttMessage;
				ByteBuf byteBuf=publishMessage.payload();
				byte[] bytes=new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				String message=new String(bytes);
				System.out.println(message);
				break;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		this.client.dubboConnect();
	}
}
