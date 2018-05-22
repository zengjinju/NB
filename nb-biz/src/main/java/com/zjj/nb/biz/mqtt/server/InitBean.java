package com.zjj.nb.biz.mqtt.server;

import com.zjj.nb.biz.mqtt.MqttHandler;
import lombok.Data;

@Data
public class InitBean {

	private int port ;

	private String serverName ;

	private boolean keepalive ;

	private boolean reuseaddr ;


	private boolean tcpNodelay ;

	private int backlog ;

	private  int  sndbuf ;

	private int revbuf ;

	private int heart ;

	private boolean ssl ;

	private String jksFile;

	private String jksStorePassword;

	private String jksCertificatePassword;

	private Class<MqttHandler> mqttHander ;

	private int  initalDelay ;

	private  int period ;

	private int bossThread;

	private int workThread;
}
