package com.zjj.nb.biz.mqtt.demo;

public interface MqttMessageArrivedCallback {

	/**
	 * 接收到消息后的处理逻辑
	 * @param options
	 */
	void callBack(byte[] payload);
}
