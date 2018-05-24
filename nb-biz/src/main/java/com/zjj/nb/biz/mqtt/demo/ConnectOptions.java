package com.zjj.nb.biz.mqtt.demo;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class ConnectOptions {
	private String accessKey;

	private String secretKey;

	private String clientId;

	private String[] serverURIs;

	private String userName;

	private String password;

	private int keepAliveInterval;

	private Boolean automaticReconnect;

	private Boolean cleanSession;

	private String publishTopic;

	private String[] subscribeTopics;

	private int[] qos;

	private Boolean subResult=false;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String[] getServerURIs() {
		return serverURIs;
	}

	public void setServerURIs(String[] serverURIs) {
		this.serverURIs = serverURIs;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public Boolean getAutomaticReconnect() {
		return automaticReconnect;
	}

	public void setAutomaticReconnect(Boolean automaticReconnect) {
		this.automaticReconnect = automaticReconnect;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(Boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPublishTopic() {
		return publishTopic;
	}

	public void setPublishTopic(String publishTopic) {
		this.publishTopic = publishTopic;
	}

	public String[] getSubscribeTopics() {
		return subscribeTopics;
	}

	public void setSubscribeTopics(String[] subscribeTopics) {
		this.subscribeTopics = subscribeTopics;
	}

	public int[] getQos() {
		return qos;
	}

	public void setQos(int[] qos) {
		this.qos = qos;
	}

	public Boolean getSubResult() {
		return subResult;
	}

	public void setSubResult(Boolean subResult) {
		this.subResult = subResult;
	}

	public  MqttConnectOptions convert2MqttOptions() {
		MqttConnectOptions connectOptions = new MqttConnectOptions();
		connectOptions.setUserName(this.getUserName());
		connectOptions.setPassword(this.getPassword().toCharArray());
		connectOptions.setKeepAliveInterval(this.getKeepAliveInterval());
		connectOptions.setCleanSession(this.getCleanSession());
		connectOptions.setServerURIs(this.getServerURIs());
		connectOptions.setAutomaticReconnect(this.getAutomaticReconnect());
		return connectOptions;
	}

	public void checkConnectOptions() {
		if (this.getUserName() == null || "".equals(this.getUserName())) {
			throw new RuntimeException("用户名不能为空");
		}
		if (this.getPassword() == null || "".equals(this.getPassword())) {
			throw new RuntimeException("用户密码不能为空");
		}
		if (this.getServerURIs() == null || this.getServerURIs().length == 0) {
			throw new RuntimeException("Broker地址不能为空");
		}
	}

}
