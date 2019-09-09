package com.xkenmon.nox.ssocks.crypto.template;

import com.xkenmon.nox.ssocks.crypto.Cryptor;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.G3413CTRBlockCipher;

public abstract class AESCTRCryptorTemplate extends
    AESStreamBlockCryptorTemplate<G3413CTRBlockCipher> implements
    Cryptor {

  public AESCTRCryptorTemplate() {
    super();
  }

  public AESCTRCryptorTemplate(byte[] key) {
    super(key);
  }

  @Override
  public int getIvLen() {
    return getBlockSize() / 2;
  }

  @Override
  protected G3413CTRBlockCipher createCipher(AESEngine engine) {
    return new G3413CTRBlockCipher(engine, engine.getBlockSize() * 8);
  }
}
