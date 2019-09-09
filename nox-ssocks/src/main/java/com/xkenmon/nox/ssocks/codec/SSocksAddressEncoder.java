package com.xkenmon.nox.ssocks.codec;

import com.xkenmon.nox.ssocks.message.SSocksAddressRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksAddressEncoder extends MessageToByteEncoder<SSocksAddressRequest> {

  @Override
  protected void encode(ChannelHandlerContext ctx, SSocksAddressRequest msg, ByteBuf out) {
    var addressType = msg.getAddrType();
    var addr = msg.getDestAddr();
    var port = msg.getPort();
    out.writeByte(addressType.byteValue());
    if (Socks5AddressType.DOMAIN.equals(addressType)) {
      out.writeByte(addr.length());
      out.writeCharSequence(addr, CharsetUtil.US_ASCII);
    } else if (Socks5AddressType.IPv4.equals(addressType)) {
      out.writeBytes(NetUtil.createByteArrayFromIpAddressString(addr));
    } else if (Socks5AddressType.IPv6.equals(addressType)) {
      out.writeBytes(NetUtil.createByteArrayFromIpAddressString(addr));
    } else {
      throw new EncoderException("unknow address type");
    }
    out.writeShort(port);
  }
}
