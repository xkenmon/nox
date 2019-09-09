package com.xkenmon.nox.ssocks.crypto.impl;

import com.xkenmon.nox.ssocks.crypto.Encryptor;
import com.xkenmon.nox.ssocks.crypto.template.AESCFBCryptorTemplate;

public class AESCFBEncryptor extends AESCFBCryptorTemplate implements Encryptor {

  public AESCFBEncryptor() {
    super();
  }

  public AESCFBEncryptor(byte[] key) {
    super(key);
  }

  @Override
  protected boolean isEncryptor() {
    return true;
  }

  @Override
  public byte[] encrypt(byte[] data) {
    return process(data);
  }
}
