package com.xkenmon.nox.ssocks.crypto.impl;

import static org.junit.Assert.assertArrayEquals;

import com.xkenmon.nox.ssocks.crypto.CryptoFactory;
import com.xkenmon.nox.ssocks.crypto.Decryptor;
import com.xkenmon.nox.ssocks.crypto.Encryptor;
import com.xkenmon.nox.ssocks.util.SSocksKeyGenerator;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import org.junit.Test;

public class AESCFBDecryptorTest {

  @Test
  public void decrypt() {
    var key = SSocksKeyGenerator.of("mengxiangkun", 256 / 8);
    Decryptor decryptor = CryptoFactory.getAESCFBDecryptor(key);
    Encryptor encryptor = CryptoFactory.getAESCFBEncryptor(key);
    var iv = new byte[decryptor.getIvLen()];
    encryptor.init(iv);
    decryptor.init(iv);

    var text = "nihao".getBytes(CharsetUtil.UTF_8);
    var encoded = encryptor.encrypt(text);
    var decoded = decryptor.decrypt(encoded);
    System.out.println(ByteBufUtil.hexDump(text));
    System.out.println(ByteBufUtil.hexDump(encoded));
    assertArrayEquals(text, decoded);
    encoded = encryptor.encrypt(text);
    decoded = decryptor.decrypt(encoded);
    System.out.println(ByteBufUtil.hexDump(text));
    System.out.println(ByteBufUtil.hexDump(encoded));
    assertArrayEquals(decoded,text);
    encoded = encryptor.encrypt(text);
    decoded = decryptor.decrypt(encoded);
    System.out.println(ByteBufUtil.hexDump(text));
    System.out.println(ByteBufUtil.hexDump(encoded));
    assertArrayEquals(decoded,text);
  }
}