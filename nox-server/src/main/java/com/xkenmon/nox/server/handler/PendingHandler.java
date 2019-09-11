package com.xkenmon.nox.server.handler;

import com.xkenmon.nox.common.util.ExceptionUtil;
import com.xkenmon.nox.server.event.PendingDoneEvent;
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
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof PendingDoneEvent) {
      log.debug("receive pending done event.");
      if (clientConnectFuture.isSuccess()) {
        writeQueue.forEach(clientConnectFuture.channel()::write);
        clientConnectFuture.channel().flush();
        log.debug("flush pending data");
      } else if (clientConnectFuture.cause() != null) {
        log.warn("connect failure, not flush data.");
      }
      log.debug("remove pending handler.");
      ctx.pipeline().remove(this);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.warn("pending handler has thrown an exception: {}", cause.getMessage());
    log.debug(ExceptionUtil.stackTraceToString(cause));
    ctx.flush();
    ctx.close();
  }
}
