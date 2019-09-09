package com.xkenmon.nox.ssocks.crypto.template;

import com.xkenmon.nox.ssocks.crypto.Cryptor;
import java.util.Objects;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public abstract class AESStreamBlockCryptorTemplate<T extends StreamBlockCipher> implements
    Cryptor {

  private final AESEngine engine = new AESEngine();
  private final T cipher = createCipher(engine);

  private byte[] key;

  public AESStreamBlockCryptorTemplate() {
  }

  public AESStreamBlockCryptorTemplate(byte[] key) {
    this.key = key;
  }

  @Override
  public void init(byte[] iv) {
    ParametersWithIV parameters = new ParametersWithIV(new KeyParameter(key), iv);
    getCipher().init(isEncryptor(), parameters);
  }

  @Override
  public byte[] process(byte[] data) {
    var dest = new byte[data.length];
    getCipher().processBytes(data, 0, data.length, dest, 0);
    return dest;
  }

  @Override
  public byte[] getKey() {
    return key;
  }

  @Override
  public void setKey(byte[] key) {
    Objects.requireNonNull(key);
    this.key = key;
  }

  @Override
  public int getBlockSize() {
    return getCipher().getBlockSize();
  }

  protected abstract boolean isEncryptor();

  protected abstract T createCipher(AESEngine engine);

  protected T getCipher() {
    return cipher;
  }

}
