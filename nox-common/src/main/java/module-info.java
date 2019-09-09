module nox.common {
  exports com.xkenmon.nox.common;
  exports com.xkenmon.nox.common.configuration;
  exports com.xkenmon.nox.common.util;

  requires jcommander;
  requires io.netty.common;
  requires org.apache.logging.log4j.core;
  requires org.apache.logging.log4j;

  requires static lombok;
  requires org.mapstruct.processor;
}