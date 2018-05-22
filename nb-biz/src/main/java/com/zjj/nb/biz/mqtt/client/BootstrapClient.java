package com.zjj.nb.biz.mqtt.client;

import io.netty.channel.Channel;

public interface BootstrapClient {
	void shutdown();

	void initEventPool();

	Channel start();
}
