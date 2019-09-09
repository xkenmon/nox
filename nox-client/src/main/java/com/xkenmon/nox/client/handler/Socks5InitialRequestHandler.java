package com.xkenmon.nox.client.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class Socks5InitialRequestHandler extends
    SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialRequest msg) {
    if (msg.decoderResult().isFailure()) {
      log.debug("Decode Socks5 Initial Request Failed.");
      log.debug("Failed Cause: ", msg.decoderResult().cause());
    } else if (!msg.decoderResult().isFinished()) {
      log.trace("Received Socks5 Initial Request But Not Finished Yet.");
    } else {
      switch (msg.version()) {
        case SOCKS5:
          // TODO: log client and server supported auth method.
          log.debug("Received Socks5 Initial Request.");
          //TODO: add the server supported auth method list.
          DefaultSocks5InitialResponse response =
              new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH);
          ctx.writeAndFlush(response);
          log.debug("Use 'NO_AUTH' Auth Method.");

          ctx.channel().pipeline().remove(this);
          ctx.channel().pipeline().remove(Socks5InitialRequestDecoder.class);

          // TODO: Add a handler if adding auth
          break;
        case SOCKS4a:
          log.info("Unsupported Version Socks4(a).");
          break;
        case UNKNOWN:
          log.info(String.format("Unknown Socks Version: '0x%h'.", msg.version().byteValue()));
          break;
      }
    }
  }
}
