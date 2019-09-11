package com.xkenmon.nox.client.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientConfiguration {

  @NonNull
  private String remoteAddr;

  @NonNull
  private Integer remotePort;

  @NonNull
  private String localAddr;

  @NonNull
  private Integer localPort;

  @NonNull
  private String password;

  @NonNull
  private String method;

  @NonNull
  private Boolean fastOpen;

  @NonNull
  private Integer timeout;

}
