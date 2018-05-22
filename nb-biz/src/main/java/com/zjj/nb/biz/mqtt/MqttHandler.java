package com.zjj.nb.biz.mqtt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleStateEvent;


public abstract class MqttHandler extends SimpleChannelInboundHandler<MqttMessage> {

	protected MqttService mqttService;

	public MqttHandler(MqttService mqttService) {
		this.mqttService = mqttService;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
		MqttFixedHeader header = mqttMessage.fixedHeader();
		if (header != null) {
			doMessage(channelHandlerContext, mqttMessage);
		}
	}

	public abstract void doMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage);

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("关闭成功");
		ctx.channel().close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			mqttService.doTimeOut(ctx.channel(),(IdleStateEvent) evt);
		}
		super.userEventTriggered(ctx, evt);
	}


}
