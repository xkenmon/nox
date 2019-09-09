package com.xkenmon.nox.ssocks.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksAddressCodec {

  public static byte[] encode(Socks5CommandRequest request) {
    var buf = Unpooled.buffer();
    encode(request, buf);
    var data = new byte[buf.readableBytes()];
    buf.readBytes(data);
    return data;
  }

  public static ByteBuf encode(Socks5CommandRequest request, ByteBufAllocator allocator) {
    var buf = allocator.buffer();
    encode(request, buf);
    return buf;
  }

  public static void encode(Socks5CommandRequest request, ByteBuf byteBuf) {
    var addressType = request.dstAddrType();
    var addr = request.dstAddr();
    var port = request.dstPort();
    byteBuf.writeByte(addressType.byteValue());
    if (Socks5AddressType.DOMAIN.equals(addressType)) {
      byteBuf.writeByte(addr.length());
      byteBuf.writeCharSequence(addr, CharsetUtil.US_ASCII);
      byteBuf.writeShort(port);
    } else if (Socks5AddressType.IPv4.equals(addressType)) {
      byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(addr));
      byteBuf.writeShort(port);
    } else if (Socks5AddressType.IPv6.equals(addressType)) {
      byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(addr));
      byteBuf.writeShort(port);
    } else {
      log.warn("unknown address type");
    }
  }

}
