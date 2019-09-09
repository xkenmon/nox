package com.xkenmon.nox.ssocks.message;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSocksAddressRequest {

  public SSocksAddressRequest(Socks5AddressType addrType, String destAddr, int port) {
    this.addrType = addrType;
    this.destAddr = destAddr;
    this.port = port;
    this.decoderResult = DecoderResult.SUCCESS;
  }

  private Socks5AddressType addrType;

  private String destAddr;

  private int port;

  private DecoderResult decoderResult = DecoderResult.UNFINISHED;
}
