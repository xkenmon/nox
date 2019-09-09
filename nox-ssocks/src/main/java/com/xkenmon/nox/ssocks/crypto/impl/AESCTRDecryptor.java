package com.xkenmon.nox.ssocks.crypto.impl;

import com.xkenmon.nox.ssocks.crypto.Decryptor;
import com.xkenmon.nox.ssocks.crypto.template.AESCTRCryptorTemplate;

public class AESCTRDecryptor extends AESCTRCryptorTemplate implements Decryptor {

  public AESCTRDecryptor() {

  }

  public AESCTRDecryptor(byte[] key) {
    super(key);
  }

  @Override
  protected boolean isEncryptor() {
    return false;
  }

  @Override
  public byte[] decrypt(byte[] data) {
    return process(data);
  }
}
