package com.xkenmon.nox.client.handler;

import com.xkenmon.nox.ssocks.util.SSocksAddressCodec;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingWriteQueue;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import java.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksConnectHandler extends ChannelDuplexHandler {

  private Socks5CommandRequest socks5CommandRequest;

  private PendingWriteQueue pendingWriteQueue;

  private boolean done;

  public SSocksConnectHandler(Socks5CommandRequest socks5CommandRequest) {
    this.socks5CommandRequest = socks5CommandRequest;
  }

  @Override
  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
      SocketAddress localAddress, ChannelPromise promise) {
    log.debug("connecting to remote server: {}", remoteAddress);
    promise.addListener(future -> {
      if (future.isSuccess()) {
        log.info("connected to remote server: {}", remoteAddress);
        done = true;
      } else {
        log.error("can't connect to remote server: {}", remoteAddress);
        ctx.disconnect().addListener(future1 -> log.info("disconnected to {}", remoteAddress))
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      }
    });
    ctx.connect(remoteAddress, localAddress, promise);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    log.debug("handler channel active.");
    ctx.writeAndFlush(SSocksAddressCodec.encode(socks5CommandRequest, ctx.alloc()))
        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    log.info("send {} connect request to server: {}:{}",
        socks5CommandRequest.dstAddrType(),
        socks5CommandRequest.dstAddr(),
        socks5CommandRequest.dstPort());
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    if (done) {
      log.debug("write pending write");
      writePendingWriteQueue();
      ctx.write(msg, promise).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    } else {
      addPendingWriteQueue(ctx, msg, promise);
    }
  }

  @Override
  public void flush(ChannelHandlerContext ctx) throws Exception {
    if (done) {
      log.debug("flush pending write");
      writePendingWriteQueue();
      super.flush(ctx);
      ctx.pipeline().remove(this);
    } else {
      log.debug("not flush");
    }
  }

  private void writePendingWriteQueue() {
    if (pendingWriteQueue != null) {
      log.debug("remove and write pending write queue.");
      pendingWriteQueue.removeAndWriteAll()
          .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      pendingWriteQueue = null;
    }
  }

  private void addPendingWriteQueue(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    var writer = this.pendingWriteQueue;
    if (writer == null) {
      pendingWriteQueue = writer = new PendingWriteQueue(ctx);
    }
    writer.add(msg, promise);
    log.debug("add message to pending write queue.");
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.warn("connect handler catch exception: ", cause);
  }
}
