package com.xkenmon.nox.server.initializer;

import com.xkenmon.nox.common.util.ExceptionUtil;
import com.xkenmon.nox.server.configuration.ServerConfiguration.PortPasswordPair;
import com.xkenmon.nox.server.handler.SSocksAddrRequestHandler;
import com.xkenmon.nox.ssocks.codec.SSocksAddressDecoder;
import com.xkenmon.nox.ssocks.codec.SSocksDecoder;
import com.xkenmon.nox.ssocks.codec.SSocksEncoder;
import com.xkenmon.nox.ssocks.crypto.CryptoFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksServerChannelInitializer extends ChannelInitializer {

  private static final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);

  private Map<Integer, String> portPasswordMap;

  private String method;

  public SSocksServerChannelInitializer(
      Set<PortPasswordPair> portPasswordPairs, String method) {
    portPasswordMap = portPasswordPairs.stream().collect(
        Collectors.toMap(PortPasswordPair::getPort, PortPasswordPair::getPassword));
    this.method = method;
  }

  @Override
  protected void initChannel(Channel ch) {
    var port = ((InetSocketAddress) ch.localAddress()).getPort();
    var password = portPasswordMap.get(port);
    if (null == password) {
      throw new IllegalStateException();
    }

    var encryptor = CryptoFactory.getEncryptor(method, password);
    var decryptor = CryptoFactory.getDecryptor(method, password);

    ch.closeFuture().addListener(future -> CryptoFactory.addCryptor(encryptor, decryptor));

    ch.pipeline().addLast("server-logging", loggingHandler)
        .addLast("ssocks-encoder", new SSocksEncoder(encryptor))
        .addLast("ssocks-decoder", new SSocksDecoder(decryptor))
        .addLast("ssocks-addr-decoder", new SSocksAddressDecoder())
        .addLast("ssocks-addr-handler", new SSocksAddrRequestHandler())
        .addLast("exception-handler", new ChannelInboundHandlerAdapter() {
          @Override
          public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.warn("catch exception: {}: {}", cause.getClass(), cause.getMessage());
            log.debug(ExceptionUtil.stackTraceToString(cause));
            ctx.close();
          }
        });
  }
}
