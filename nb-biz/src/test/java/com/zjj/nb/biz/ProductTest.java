package com.zjj.nb.biz;

import com.zjj.nb.biz.mqtt.server.InitBean;
import com.zjj.nb.biz.mqtt.server.NettyBootstrapServer;

public class ProductTest {

	public static void main(String[] args){
		InitBean initBean=new InitBean();
		initBean.setKeepalive(true);
		initBean.setTcpNodelay(true);
		initBean.setHeart(120);
		initBean.setBacklog(1024);
		initBean.setRevbuf(10485760);
		initBean.setPort(1884);
		NettyBootstrapServer server=new NettyBootstrapServer();
		server.setServerBean(initBean);
		server.start();
	}
}
