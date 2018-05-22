package com.zjj.nb.biz.mqtt.client;

import lombok.Data;

@Data
public class SendMqttMessage {
	private String Topic;

	private byte[] payload;

	private int qos;

	private boolean retained;

	private boolean dup;

	private int messageId;


	private long timestamp;
}
