package com.xkenmon.nox.ssocks.crypto;

public interface Encryptor extends Cryptor{

  byte[] encrypt(byte[] data);

}
