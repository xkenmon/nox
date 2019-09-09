package com.xkenmon.nox.ssocks.codec;

import com.xkenmon.nox.ssocks.codec.SSocksAddressDecoder.State;
import com.xkenmon.nox.ssocks.message.SSocksAddressRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.util.List;

public class SSocksAddressDecoder extends ReplayingDecoder<State> {

  enum State {
    INIT,
    READ_TYPE,
    SUCCESS,
    FAILURE,
  }

  private Socks5AddressType addressType;

  public SSocksAddressDecoder() {
    super(State.INIT);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    try {
      switch (state()) {
        case INIT: {
          addressType = Socks5AddressType.valueOf(in.readByte());
          checkpoint(State.READ_TYPE);
        }
        case READ_TYPE: {
          if (Socks5AddressType.DOMAIN.equals(addressType)) {
            var len = in.readByte();
            var domain = in.readCharSequence(len, CharsetUtil.US_ASCII);
            var port = in.readShort();
            checkpoint(State.SUCCESS);
            out.add(new SSocksAddressRequest(addressType, domain.toString(), port));
          } else if (Socks5AddressType.IPv6.equals(addressType)) {
            var ipBytes = new byte[16];
            var ip = NetUtil.bytesToIpAddress(ipBytes);
            var port = in.readShort();
            checkpoint(State.SUCCESS);
            out.add(new SSocksAddressRequest(addressType, ip, port));
          } else if (Socks5AddressType.IPv4.equals(addressType)) {
            var ip = NetUtil.intToIpAddress(in.readInt());
            var port = in.readShort();
            checkpoint(State.SUCCESS);
            out.add(new SSocksAddressRequest(addressType, ip, port));
          } else {
            throw new DecoderException("unknown address type: " + addressType);
          }
        }
        case SUCCESS:
          var len = actualReadableBytes();
          if (len > 0) {
            out.add(in.readRetainedSlice(actualReadableBytes()));
          }
        case FAILURE:
          in.skipBytes(actualReadableBytes());
          break;
      }
    } catch (Exception e) {
      fail(out, e);
    }
  }

  private void fail(List<Object> out, Throwable cause) {
    if (!(cause instanceof DecoderException)) {
      cause = new DecoderException(cause);
    }
    checkpoint(State.FAILURE);

    var req = new SSocksAddressRequest();
    req.setDecoderResult(DecoderResult.failure(cause));
    out.add(req);
  }
}
