package com.xkenmon.nox.ssocks.codec;

import com.xkenmon.nox.common.util.BytesUtil;
import com.xkenmon.nox.ssocks.crypto.Encryptor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.security.SecureRandom;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSocksEncoder extends MessageToByteEncoder<ByteBuf> {

  private final Encryptor encryptor;
  private byte[] iv;
  private volatile boolean inited = false;

  public SSocksEncoder(Encryptor encryptor) {
    this(encryptor, null);
    this.iv = new byte[encryptor.getIvLen()];
    Random random = new SecureRandom();
    random.nextBytes(iv);
  }

  public SSocksEncoder(Encryptor encryptor, byte[] iv) {
    this.encryptor = encryptor;
    this.iv = iv;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
    if (!inited) {
      encryptor.init(iv);
      out.writeBytes(iv);
      log.debug("write iv: " + BytesUtil.toHexStrWithSpace(iv));
      inited = true;
    }
    var readable = msg.readableBytes();
    var plain = new byte[readable];
    msg.readBytes(plain);
    log.trace("encode data: [{}]", BytesUtil.toHexStrWithSpace(plain));
    var encryted = encryptor.encrypt(plain);
    log.trace("encoded data: [{}]", BytesUtil.toHexStrWithSpace(encryted));
    out.writeBytes(encryted);
  }

}
