package com.xkenmon.nox.server.configuration;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ServerConfiguration {

  @JSONField(name = "listen_address")
  private String listenAddr = "0.0.0.0";

  @JSONField(name = "port_password")
  private Set<PortPasswordPair> portPasswordPairs = new HashSet<>();

  @JSONField(name = "method")
  private String method = "aes-256-cfb";

  @JSONField(name = "timeout")
  private Integer timeoutInSecond = 600;

  @JSONField(name = "fast_open")
  private Integer fastOpen = -1;

  @JSONField(name = "debug")
  private Integer debug = 1;

  @Data
  @AllArgsConstructor
  public static class PortPasswordPair {

    @JSONField(name = "port")
    private Integer port;

    @JSONField(name = "password")
    private String password;
  }
}
