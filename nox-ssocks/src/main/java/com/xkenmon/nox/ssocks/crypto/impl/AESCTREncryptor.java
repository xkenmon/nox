package com.xkenmon.nox.ssocks.crypto.impl;

import com.xkenmon.nox.ssocks.crypto.Encryptor;
import com.xkenmon.nox.ssocks.crypto.template.AESCTRCryptorTemplate;

public class AESCTREncryptor extends AESCTRCryptorTemplate implements Encryptor {

  public AESCTREncryptor(){
    super();
  }

  public AESCTREncryptor(byte[] key){
    super(key);
  }

  @Override
  public byte[] encrypt(byte[] data) {
    return process(data);
  }

  @Override
  protected boolean isEncryptor() {
    return true;
  }
}
