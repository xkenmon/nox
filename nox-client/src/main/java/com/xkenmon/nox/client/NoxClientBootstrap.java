package com.xkenmon.nox.client;

import com.xkenmon.nox.client.configuration.ClientConfiguration;
import com.xkenmon.nox.client.initializer.Socks5ChannelInitializer;
import com.xkenmon.nox.common.util.ExceptionUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoxClientBootstrap {

  private ClientConfiguration configuration;

  public NoxClientBootstrap(ClientConfiguration configuration) {
    this.configuration = configuration;
  }

  public void start() {
    Class<? extends ServerChannel> channelClass;
    EventLoopGroup bossGroup, workerGroup;
    if (Epoll.isAvailable()) {
      log.info("use epoll socket channel.");
      channelClass = EpollServerSocketChannel.class;
      bossGroup = new EpollEventLoopGroup(2);
      workerGroup = new EpollEventLoopGroup();
    } else if (KQueue.isAvailable()) {
      log.info("use kqueue socket channel.");
      channelClass = KQueueServerSocketChannel.class;
      bossGroup = new KQueueEventLoopGroup(2);
      workerGroup = new KQueueEventLoopGroup();
    } else {
      log.info("use nio socket channel.");
      channelClass = NioServerSocketChannel.class;
      bossGroup = new NioEventLoopGroup(2);
      workerGroup = new NioEventLoopGroup();
    }
    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup)
          .channel(channelClass)
          .option(ChannelOption.SO_BACKLOG, 128)
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, configuration.getTimeout() * 1000)
          .childHandler(
              new Socks5ChannelInitializer(configuration));

      if (Epoll.isAvailable() && configuration.getFastOpen()) {
        log.info("client TCP fast open enabled.");
      }

      ChannelFuture bindFuture = serverBootstrap
          .bind(configuration.getLocalAddr(), configuration.getLocalPort()).sync();
      log.info("server starting...");
      bindFuture = bindFuture.sync();
      log.info("server started at {}:{}.",
          configuration.getLocalAddr(),
          configuration.getLocalPort());
      bindFuture = bindFuture.channel().closeFuture();
      bindFuture.sync();
      log.info("server stopped.");
    } catch (InterruptedException e) {
      log.error("catch interrupted exception: ", e);
      log.debug(ExceptionUtil.stackTraceToString(e));
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

}
