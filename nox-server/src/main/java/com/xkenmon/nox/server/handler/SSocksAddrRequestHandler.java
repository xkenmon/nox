package com.xkenmon.nox.server.handler;

import com.xkenmon.nox.common.util.ExceptionUtil;
import com.xkenmon.nox.server.event.PendingDoneEvent;
import com.xkenmon.nox.server.initializer.ClientChannelInitializer;
import com.xkenmon.nox.ssocks.codec.SSocksAddressDecoder;
import com.xkenmon.nox.ssocks.handler.ForwardingHandler;
import com.xkenmon.nox.ssocks.message.SSocksAddressRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksAddrRequestHandler extends SimpleChannelInboundHandler<SSocksAddressRequest> {

  private static final PendingDoneEvent PENDING_DONE_EVT = new PendingDoneEvent();

  private static final Class<? extends SocketChannel> channelClass;

  static {
    if (Epoll.isAvailable()) {
      channelClass = EpollSocketChannel.class;
    } else if (KQueue.isAvailable()) {
      channelClass = KQueueSocketChannel.class;
    } else {
      channelClass = NioSocketChannel.class;
    }
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, SSocksAddressRequest msg) {

    if (msg.getDecoderResult().isFailure()) {
      log.warn("decode failure: ", msg.getDecoderResult().cause());
      ctx.close();
      return;
    }

    var client = new Bootstrap();
    client = client.group(ctx.channel().eventLoop())
        .channel(channelClass)
        .handler(new ClientChannelInitializer(ctx));

    var connectFuture = client.connect(msg.getDestAddr(), msg.getPort());

    ctx.pipeline().addLast("pending-handler", new PendingHandler(connectFuture));

    connectFuture.addListener(future -> {
      if (future.isSuccess()) {
        log.info("connected to {}", connectFuture.channel().remoteAddress());
        ctx.channel().pipeline()
            .addLast("server-forwarder", new ForwardingHandler(connectFuture.channel()));
        ctx.fireUserEventTriggered(PENDING_DONE_EVT);
        ctx.pipeline().remove(SSocksAddressDecoder.class);
        ctx.pipeline().remove(this);
      } else {
        log.warn("can not connect to {}:{}", msg.getDestAddr(), msg.getPort());
        log.warn(future.cause().getMessage());
        log.debug(ExceptionUtil.stackTraceToString(future.cause()));
        ctx.channel().close()
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      }
    });

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.warn(cause.getMessage());
    log.debug(ExceptionUtil.stackTraceToString(cause));
    ctx.close();
  }
}
