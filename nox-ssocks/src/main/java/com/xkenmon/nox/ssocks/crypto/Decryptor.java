package com.xkenmon.nox.ssocks.crypto;

// Cryptor
public interface Decryptor extends Cryptor {

  byte[] decrypt(byte[] data);

}
