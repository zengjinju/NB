package com.zjj.nb.biz.mqtt.client;

import com.zjj.nb.biz.util.IPUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyBootstrapClient extends AbstractBootstrapClient {
	private static final Logger logger = LoggerFactory.getLogger(NettyBootstrapClient.class);
	private static final Integer CPU = Runtime.getRuntime().availableProcessors();
	private static final AtomicInteger atomic = new AtomicInteger(0);
	private ConnectOptions connectOptions;
	/**
	 * 启动辅助类
	 */
	private Bootstrap bootstrap;

	private NioEventLoopGroup eventLoopGroup;

	public NettyBootstrapClient(ConnectOptions connectOptions) {
		this.connectOptions = connectOptions;
	}

	@Override
	public void shutdown() {
		if (eventLoopGroup != null) {
			try {
				//优雅关闭
				eventLoopGroup.shutdownGracefully().sync();
			} catch (Exception e) {
				logger.error("客户端关闭资源失败【" + IPUtils.getHost() + ":" + connectOptions.getPort() + "】");
			}
		}
	}

	@Override
	public void initEventPool() {
		bootstrap = new Bootstrap();
		eventLoopGroup = new NioEventLoopGroup(CPU, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("BOOS_" + atomic.getAndIncrement());
				return t;
			}
		});
	}

	@Override
	public Channel start() {
		initEventPool();
		bootstrap.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, connectOptions.isTcpNodelay())
				.option(ChannelOption.SO_KEEPALIVE, connectOptions.isKeepalive())
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.option(ChannelOption.SO_SNDBUF, connectOptions.getSndbuf())
				.option(ChannelOption.SO_RCVBUF, connectOptions.getRevbuf())
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						initHandler(channel.pipeline(), connectOptions, new DefaultMqHandler(new ClientMqttService(),NettyBootstrapClient.this));
					}
				});
		try {

		Channel channel =bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort()).sync().channel();
		logger.info("客户端启动成功【"+connectOptions.getServerIp()+":"+connectOptions.getPort()+"】");
		return channel;
		} catch (Exception e) {
			logger.error("connect to channel fail", e);
		}
		return null;
	}

	public void dubboConnect(){
		ChannelFuture connect = bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort());
	}
}
