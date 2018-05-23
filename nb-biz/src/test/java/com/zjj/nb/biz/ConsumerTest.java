package com.zjj.nb.biz;

import com.zjj.nb.biz.mqtt.client.ConnectOptions;
import com.zjj.nb.biz.mqtt.client.NettyBootstrapClient;
import com.zjj.nb.biz.util.IPUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ConsumerTest {

	private static ScheduledExecutorService scheduledExecutorService= Executors.newScheduledThreadPool(1);

	public static void main(String[] args){
		ConnectOptions connectOptions = new ConnectOptions();
		connectOptions.setBacklog(1024);
		connectOptions.setConnectTime(1000l);
		connectOptions.setSsl(false);
		connectOptions.setServerIp(IPUtils.getHost());
		connectOptions.setPort(1884);
		connectOptions.setBossThread(1);
		connectOptions.setWorkThread(8);
		connectOptions.setMinPeriod(10);
		connectOptions.setRevbuf(1024);
		connectOptions.setSndbuf(1024);
		connectOptions.setHeart(30);
		connectOptions.setTcpNodelay(true);
		connectOptions.setKeepalive(true);
		ConnectOptions.MqttOpntions mqttOpntions = new ConnectOptions.MqttOpntions();
		mqttOpntions.setClientIdentifier("111");
		mqttOpntions.setHasPassword(false);
		mqttOpntions.setHasPassword(false);
		connectOptions.setMqtt(mqttOpntions);
		NettyBootstrapClient client=new NettyBootstrapClient(connectOptions);
		Channel channel= client.start();
		connection(channel);
//		for(int i=0;i<10;i++) {
//			try {
//				TimeUnit.SECONDS.sleep(2);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			publishMess(channel);
//		}

	}

	public static void connection(Channel channel){
		MqttFixedHeader header=new MqttFixedHeader(MqttMessageType.CONNECT,false,MqttQoS.valueOf(1),false,0);
		MqttConnectVariableHeader variableHeader=new MqttConnectVariableHeader("zjj",1,false,false,false,1,false,false,0);
		MqttConnectPayload payload=new MqttConnectPayload("zjj","zjj","zjj","","");
		MqttConnectMessage connectMessage=new MqttConnectMessage(header,variableHeader,payload);
		channel.writeAndFlush(connectMessage);
	}

	public static void publishMess(Channel channel){
		String val="hello world";
		MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,false, MqttQoS.valueOf(1),false,0);
		MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader("zjj",11);
		MqttPublishMessage mqttPublishMessage = null;
		try {
			mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(val.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		channel.writeAndFlush(mqttPublishMessage);
	}

	public static void ping(Channel channel){
		MqttFixedHeader header=new MqttFixedHeader(MqttMessageType.PINGREQ,false, MqttQoS.valueOf(2),false,0);
		MqttMessage message=new MqttMessage(header);
		channel.writeAndFlush(message);
	}
}
