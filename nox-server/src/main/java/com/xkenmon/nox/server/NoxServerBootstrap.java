package com.xkenmon.nox.server;

import com.xkenmon.nox.common.util.ExceptionUtil;
import com.xkenmon.nox.server.configuration.ServerConfiguration;
import com.xkenmon.nox.server.initializer.SSocksServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoxServerBootstrap {

  private ServerConfiguration configuration;

  public NoxServerBootstrap(ServerConfiguration configuration) {
    this.configuration = configuration;
  }

  public void start() {
    Class<? extends ServerChannel> channelClass;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
    EventLoopGroup forwardGroup;
    var os = System.getProperty("os.name").toLowerCase();
    if (os.contains("linux") && Epoll.isAvailable()) {
      log.info("use epoll socket channel.");
      channelClass = EpollServerSocketChannel.class;
      bossGroup = new EpollEventLoopGroup(2);
      workerGroup = new EpollEventLoopGroup();
      forwardGroup = new EpollEventLoopGroup();
    } else if (os.contains("mac")) {
      log.info("use kqueue socket channel.");
      channelClass = KQueueServerSocketChannel.class;
      bossGroup = new KQueueEventLoopGroup(2);
      workerGroup = new KQueueEventLoopGroup();
      forwardGroup = new KQueueEventLoopGroup();
    } else {
      log.info("use nio socket channel.");
      channelClass = NioServerSocketChannel.class;
      bossGroup = new NioEventLoopGroup(2);
      workerGroup = new NioEventLoopGroup();
      forwardGroup = new NioEventLoopGroup();
    }
    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup)
          .channel(channelClass)
          .option(ChannelOption.SO_BACKLOG, 128)
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3 * 1000)
          .childHandler(new SSocksServerChannelInitializer(
              configuration.getPortPasswordPairs(),
              configuration.getMethod()));

      log.info("server starting...");
      var pairs = configuration.getPortPasswordPairs();
      CountDownLatch bindLatch = new CountDownLatch(pairs.size());
      CountDownLatch closeLatch = new CountDownLatch(pairs.size());
      for (var entry : pairs) {
        serverBootstrap.bind(configuration.getListenAddr(), entry.getPort()).addListener(future -> {
          if (future.isSuccess()) {
            log.info("bind to {}:{}", configuration.getListenAddr(), entry.getPort());
            bindLatch.countDown();
          } else {
            log.error("unable bind to {}:{}", configuration.getListenAddr(), entry.getPort());
            log.error(future.cause().getMessage());
            log.debug(ExceptionUtil.stackTraceToString(future.cause()));
            System.exit(1);
          }
        }).channel().closeFuture().addListener(future -> closeLatch.countDown());
      }
      bindLatch.await();
      log.info("server started");
      closeLatch.await();
      log.info("server stopped.");
    } catch (InterruptedException e) {
      log.error("catch interrupted exception: ", e);
    } finally {
      forwardGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
