module nox.ssocks {
  exports com.xkenmon.nox.ssocks.handler;
  exports com.xkenmon.nox.ssocks.codec;
  exports com.xkenmon.nox.ssocks.crypto;
  exports com.xkenmon.nox.ssocks.crypto.impl;
  exports com.xkenmon.nox.ssocks.message;
  exports com.xkenmon.nox.ssocks.util;

  requires nox.common;

  requires bcprov.jdk15on;
  requires slf4j.api;

  requires io.netty.common;
  requires io.netty.buffer;
  requires io.netty.handler;
  requires io.netty.codec.socks;
  requires io.netty.transport;
  requires io.netty.codec;

  requires static lombok;
}