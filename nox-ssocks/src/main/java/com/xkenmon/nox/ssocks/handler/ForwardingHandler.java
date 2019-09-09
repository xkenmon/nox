package com.xkenmon.nox.ssocks.handler;

import com.xkenmon.nox.common.util.ExceptionUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForwardingHandler extends ChannelInboundHandlerAdapter {

  private Channel dest;

  public ForwardingHandler(Channel dest) {
    Objects.requireNonNull(dest);
    this.dest = dest;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    log.debug("write msg to {} - {}", dest.id(), dest.remoteAddress());
    dest.writeAndFlush(msg).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    flushAndClose(dest);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    flushAndClose(dest);
    flushAndClose(ctx.channel());
    log.warn("forwarding handler catch exception: {}", cause.getMessage());
    log.debug(ExceptionUtil.stackTraceToString(cause));
  }

  private void flushAndClose(Channel channel) {
    channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
  }
}
