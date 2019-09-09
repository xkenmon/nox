package com.xkenmon.nox.ssocks.crypto.template;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

public abstract class AESCFBCryptorTemplate extends AESStreamBlockCryptorTemplate<CFBBlockCipher> {

  public AESCFBCryptorTemplate() {
    super();
  }

  public AESCFBCryptorTemplate(byte[] iv) {
    super(iv);
  }

  @Override
  public int getIvLen() {
    return getBlockSize();
  }

  @Override
  protected CFBBlockCipher createCipher(AESEngine engine) {
    return new CFBBlockCipher(engine, engine.getBlockSize() * 8);
  }

}
