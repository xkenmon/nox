module nox.client {
  requires nox.common;
  requires nox.ssocks;

  requires jcommander;

  requires io.netty.transport;
  requires io.netty.transport.kqueue;
  requires io.netty.transport.epoll;
  requires io.netty.codec.socks;
  requires io.netty.common;
  requires io.netty.handler;

  requires slf4j.api;

  requires static lombok;
}