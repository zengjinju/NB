package com.zjj.nb.biz.mqtt.server;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public abstract class AbstractBootstrapServer implements BootstrapServer{

	public void initHander(ChannelPipeline pipeline,InitBean initBean){
		pipeline.addLast("encoder", MqttEncoder.INSTANCE);
		pipeline.addLast("decoder", new MqttDecoder());
		pipeline.addLast(new IdleStateHandler(initBean.getHeart(),0,0));
		pipeline.addLast(new DefaultMqqHander(new ServerMqttService()));
	}
}
