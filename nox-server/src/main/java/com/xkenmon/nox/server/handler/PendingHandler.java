package com.xkenmon.nox.server.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PendingHandler extends ChannelInboundHandlerAdapter {

  private List<Object> writeQueue = new LinkedList<>();

  private ChannelFuture clientConnectFuture;

  public PendingHandler(ChannelFuture clientConnectFuture) {
    this.clientConnectFuture = clientConnectFuture;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    writeQueue.add(msg);
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    if (!clientConnectFuture.isDone()) {
      throw new IllegalStateException("should be removed after the connection is complete");
    }
    if (clientConnectFuture.isSuccess()) {
      writeQueue.forEach(clientConnectFuture.channel()::write);
      clientConnectFuture.channel().flush();
      log.debug("flush peding data");
    } else {
      log.debug("connect failure, not flush data.");
      log.debug("removed pending handler.");
    }
    super.handlerRemoved(ctx);
  }
}
