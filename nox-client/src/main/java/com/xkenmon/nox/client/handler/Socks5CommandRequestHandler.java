package com.xkenmon.nox.client.handler;

import com.xkenmon.nox.client.initializer.SSocksClientChannelInitializer;
import com.xkenmon.nox.ssocks.handler.ForwardingHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class Socks5CommandRequestHandler extends
    SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {

  private String serverAddress;

  private Integer serverPort;

  private String password;

  private String method;

  public Socks5CommandRequestHandler(String serverAddress, Integer serverPort,
      String password, String method) {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
    this.password = password;
    this.method = method;
  }

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
  protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) {
    if (msg.type().equals(Socks5CommandType.CONNECT)) {
      var client = new Bootstrap();
      client.group(ctx.channel().eventLoop())
          .channel(channelClass)
          .option(ChannelOption.TCP_NODELAY, true)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .handler(new SSocksClientChannelInitializer(ctx, msg, password, method));
      var forwardConnection = client.connect(serverAddress, serverPort);
      forwardConnection.addListener((ChannelFutureListener) future -> {
        if (future.isSuccess()) {
          log.debug("write success response to client.");
          ctx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS,
              Socks5AddressType.IPv4));
          ctx.pipeline().addLast(new ForwardingHandler(forwardConnection.channel()));
          ctx.pipeline().remove(Socks5CommandRequestDecoder.class);
          ctx.pipeline().remove(Socks5CommandRequestHandler.class);
        } else {
          log.warn("connect to remote shadowsocks server failed.");
          ctx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE,
              Socks5AddressType.IPv4)).addListener(ChannelFutureListener.CLOSE);
        }
      });
    } else {  // TODO: 支持其他命令
      log.info("Unsupported Command Type: {}.", msg.type());
      ctx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.COMMAND_UNSUPPORTED,
          Socks5AddressType.IPv4));
      ctx.close();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.warn("catch msg: {} - {}", cause.getClass().getName(), cause.getMessage());
  }
}
