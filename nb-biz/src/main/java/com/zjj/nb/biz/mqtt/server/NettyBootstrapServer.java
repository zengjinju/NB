package com.zjj.nb.biz.mqtt.server;

import com.zjj.nb.biz.mqtt.client.NettyBootstrapClient;
import com.zjj.nb.biz.util.IPUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyBootstrapServer extends AbstractBootstrapServer {
	private static final Logger log= LoggerFactory.getLogger(NettyBootstrapServer.class);

	private ServerBootstrap serverBootstrap;
	private EventLoopGroup bossGroup;

	private EventLoopGroup workGroup;

	private InitBean serverBean;

	@Override
	public void shutdown() {

	}

	@Override
	public void setServerBean(InitBean serverBean) {
		this.serverBean=serverBean;
	}

	@Override
	public void start() {
		initEventPool();
		serverBootstrap.group(bossGroup, workGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_REUSEADDR, serverBean.isReuseaddr())
				.option(ChannelOption.SO_BACKLOG, serverBean.getBacklog())
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.option(ChannelOption.SO_RCVBUF, serverBean.getRevbuf())
				.childHandler(new ChannelInitializer<SocketChannel>() {
					protected void initChannel(SocketChannel ch) throws Exception {
						initHander(ch.pipeline(),serverBean);
					}
				})
				.childOption(ChannelOption.TCP_NODELAY, serverBean.isTcpNodelay())
				.childOption(ChannelOption.SO_KEEPALIVE, serverBean.isKeepalive())
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		serverBootstrap.bind(IPUtils.getHost(),serverBean.getPort()).addListener((ChannelFutureListener) channelFuture -> {
			if (channelFuture.isSuccess())
				log.info("服务端启动成功【" + IPUtils.getHost() + ":" + serverBean.getPort() + "】");
			else
				log.info("服务端启动失败【" + IPUtils.getHost() + ":" + serverBean.getPort() + "】");
		});
	}

	private void initEventPool() {
		serverBootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(4, new ThreadFactory() {
			private AtomicInteger index = new AtomicInteger(0);

			public Thread newThread(Runnable r) {
				return new Thread(r, "BOSS_" + index.incrementAndGet());
			}
		});
		workGroup = new NioEventLoopGroup(4, new ThreadFactory() {
			private AtomicInteger index = new AtomicInteger(0);

			public Thread newThread(Runnable r) {
				return new Thread(r, "WORK_" + index.incrementAndGet());
			}
		});
	}
}
