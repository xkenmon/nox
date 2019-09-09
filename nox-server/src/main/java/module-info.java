module nox.server {
  requires nox.common;
  requires nox.ssocks;

  requires jcommander;
  requires fastjson;

  requires io.netty.common;
  requires io.netty.handler;
  requires io.netty.transport;
  requires io.netty.transport.epoll;
  requires io.netty.transport.kqueue;

  requires slf4j.api;

  requires static lombok;
}