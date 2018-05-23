package com.zjj.nb.biz.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.List;

public interface MqttService {

	public void close(Channel channel);

	public void pubMessage(Channel channel,String topic,String message, int messsageId,Boolean isDup);

	public void puhback(Channel channel, MqttMessage mqttMessage);

	public void doTimeOut(Channel channel, IdleStateEvent evt);

	public void subBack(Channel channel,MqttSubscribeMessage mqttSubscribeMessage,int num);

	public void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId);
}
