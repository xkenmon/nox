package com.xkenmon.nox.client.initializer;

import com.xkenmon.nox.client.configuration.ClientConfiguration;
import com.xkenmon.nox.client.handler.Socks5CommandRequestHandler;
import com.xkenmon.nox.client.handler.Socks5InitialRequestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Socks5ChannelInitializer extends ChannelInitializer<SocketChannel> {


  private Socks5InitialRequestHandler initialRequestHandler;

  private Socks5CommandRequestHandler commandRequestHandler;

  public Socks5ChannelInitializer(ClientConfiguration configuration) {
    initialRequestHandler = new Socks5InitialRequestHandler();
    commandRequestHandler = new Socks5CommandRequestHandler(configuration);
  }


  @Override
  protected void initChannel(SocketChannel ch) {
    ch.pipeline()
        .addLast("logging handler", new LoggingHandler(LogLevel.DEBUG)) // in/out
        .addLast(Socks5ServerEncoder.class.getName(), Socks5ServerEncoder.DEFAULT)  // out
        .addLast(Socks5InitialRequestDecoder.class.getName(),
            new Socks5InitialRequestDecoder())  //  in
        .addLast(Socks5InitialRequestHandler.class.getName(), initialRequestHandler)  //  in
        .addLast(Socks5CommandRequestDecoder.class.getName(),
            new Socks5CommandRequestDecoder())  //  in
        .addLast(Socks5CommandRequestHandler.class.getName(), commandRequestHandler); //  in
    // forwarding handler - in
  }
}
