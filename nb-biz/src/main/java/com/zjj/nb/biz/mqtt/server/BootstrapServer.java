package com.zjj.nb.biz.mqtt.server;

public interface BootstrapServer {
	void shutdown();

	void setServerBean(InitBean serverBean);

	void start();
}
