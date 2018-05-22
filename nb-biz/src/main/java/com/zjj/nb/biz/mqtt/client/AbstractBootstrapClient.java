package com.zjj.nb.biz.mqtt.client;

import com.zjj.nb.biz.mqtt.MqttHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public abstract class AbstractBootstrapClient implements BootstrapClient {

	public void initHandler(ChannelPipeline channelPipeline, ConnectOptions connectOptions, MqttHandler mqttHandler) {
		channelPipeline.addLast("decoder", new MqttDecoder());
		channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
		channelPipeline.addLast(new IdleStateHandler(0,connectOptions.getHeart(),0));
		channelPipeline.addLast(mqttHandler);
	}
}
