package com.xkenmon.nox.client.initializer;

import com.xkenmon.nox.client.handler.SSocksConnectHandler;
import com.xkenmon.nox.ssocks.codec.SSocksDecoder;
import com.xkenmon.nox.ssocks.codec.SSocksEncoder;
import com.xkenmon.nox.ssocks.crypto.CryptoFactory;
import com.xkenmon.nox.ssocks.crypto.Decryptor;
import com.xkenmon.nox.ssocks.crypto.Encryptor;
import com.xkenmon.nox.ssocks.handler.ForwardingHandler;
import com.xkenmon.nox.ssocks.util.SSocksKeyGenerator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksClientChannelInitializer extends ChannelInitializer<SocketChannel> {

  private static final SecureRandom random = new SecureRandom();

  private final Socks5CommandRequest commandRequest;

  private ChannelHandlerContext forwardingContext;

  private byte[] key = SSocksKeyGenerator.of("mengxiangkun", 256 / 8);

  private String password;

  private String method;

  public SSocksClientChannelInitializer(ChannelHandlerContext forwardingContext,
      Socks5CommandRequest commandRequest, String password, String method) {
    this.forwardingContext = forwardingContext;
    this.commandRequest = commandRequest;
    this.method = method;
    this.password = password;
  }

  @Override
  protected void initChannel(SocketChannel ch) {

    Decryptor decryptor = CryptoFactory.getDecryptor(method, password);
    Encryptor encryptor = CryptoFactory.getEncryptor(method, password);

    var iv = new byte[encryptor.getIvLen()];
    random.nextBytes(iv);

    SSocksEncoder encoder = new SSocksEncoder(encryptor, iv);
    SSocksDecoder decoder = new SSocksDecoder(decryptor);

    ch.closeFuture().addListener(future -> CryptoFactory.addCryptor(decryptor, encryptor));

    ch.pipeline()
        .addLast("logging-client", new LoggingHandler(LogLevel.DEBUG))  //  in/out
        .addLast("ssocks-encoder", encoder) //  out
        .addLast("ssocks-connect", new SSocksConnectHandler(commandRequest))  // in/out
        .addLast("ssocks-decoder", decoder) //  in
        .addLast(new ForwardingHandler(forwardingContext.channel())); //  in
  }
}
