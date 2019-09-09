package com.xkenmon.nox.ssocks.crypto;

public interface Cryptor {

  void init(byte[] iv);

  byte[] process(byte[] src);

  byte[] getKey();

  void setKey(byte[] key);

  int getIvLen();

  int getBlockSize();

}
