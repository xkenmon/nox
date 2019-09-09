package com.xkenmon.nox.ssocks.codec;


import com.xkenmon.nox.common.util.BytesUtil;
import com.xkenmon.nox.ssocks.crypto.Decryptor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksDecoder extends ByteToMessageDecoder {

  private boolean inited = false;
  private final Decryptor decryptor;

  public SSocksDecoder(Decryptor decryptor) {
    this.decryptor = decryptor;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (!inited) {
      var iv = new byte[decryptor.getIvLen()];
      in.readBytes(iv);
      decryptor.init(iv);
      log.debug("decoder iv: [{}]", BytesUtil.toHexStrWithSpace(iv));
      inited = true;
    }
    var readable = in.readableBytes();
    var encoded = new byte[readable];
    in.readBytes(encoded);
    var plain = decryptor.decrypt(encoded);

    log.trace("decode data: encoded: \n{}\nplain:\n{}",
        formatBytes(encoded),
        formatBytes(plain));
    out.add(Unpooled.copiedBuffer(plain));
  }

  private String formatBytes(byte[] arr) {
    return ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(arr)).lines().limit(5)
        .collect(Collectors.joining("\n"));
  }
}
