package com.xkenmon.nox.ssocks.crypto.impl;

import com.xkenmon.nox.ssocks.crypto.Encryptor;
import com.xkenmon.nox.ssocks.util.SSocksKeyGenerator;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.Test;

@Slf4j
public class AESCFBEncryptorTest {

  @Test
  public void encodeTest() {
    Encryptor encryptor = new AESCFBEncryptor(SSocksKeyGenerator.of("mengxiangkun", 256 / 8));
    var iv = new byte[encryptor.getIvLen()];
    encryptor.init(iv);
    log.info(ByteUtils.toHexString(iv, "", "-"));
    var text = "love you";
    IntStream.rangeClosed(1, 5).forEach(i -> {
      log.info("============={}============", i);
      log.info(ByteUtils
          .toHexString(encryptor.encrypt(text.getBytes(StandardCharsets.UTF_8)), "0x", "-"));
      log.info("===========================");
    });
  }

}