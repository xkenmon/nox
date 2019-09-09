package com.xkenmon.nox.server.initializer;

import com.xkenmon.nox.ssocks.handler.ForwardingHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

  private final ChannelHandlerContext dest;

  private static final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);

  public ClientChannelInitializer(ChannelHandlerContext dest) {
    this.dest = dest;
  }

  @Override
  protected void initChannel(SocketChannel ch) {
    ch.pipeline()
        .addLast("client-logging", loggingHandler)
        .addLast("client-forwarder", new ForwardingHandler(dest.channel()));
  }
}
