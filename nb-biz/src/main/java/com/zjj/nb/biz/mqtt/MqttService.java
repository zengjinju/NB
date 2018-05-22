package com.zjj.nb.biz.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleStateEvent;

public interface MqttService {

	public void close(Channel channel);

	public void puhback(Channel channel, MqttMessage mqttMessage);

	public void doTimeOut(Channel channel, IdleStateEvent evt);
}
