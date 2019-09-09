package com.xkenmon.nox.ssocks.crypto.impl;

import com.xkenmon.nox.ssocks.crypto.Decryptor;
import com.xkenmon.nox.ssocks.crypto.template.AESCFBCryptorTemplate;

public class AESCFBDecryptor extends AESCFBCryptorTemplate implements Decryptor {

  public AESCFBDecryptor() {
  }

  public AESCFBDecryptor(byte[] key) {
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
