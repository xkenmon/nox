package com.xkenmon.nox.ssocks.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SSocksKeyGenerator {

  private static ThreadLocal<MessageDigest> digestThreadLocal = ThreadLocal.withInitial(() -> {
    try {
      return MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  });

  public static byte[] of(@NonNull String password, int byteskenLen) {
    var pass = password.getBytes(StandardCharsets.UTF_8);
    var keys = new byte[32];

    MessageDigest messageDigest = digestThreadLocal.get();
    var i = 0;
    byte[] hash = null;
    byte[] temp = null;
    while (i < keys.length) {
      if (i == 0) {
        hash = messageDigest.digest(pass);
        temp = new byte[hash.length + pass.length];
      } else {
        System.arraycopy(hash, 0, temp, 0, hash.length);
        System.arraycopy(pass, 0, temp, hash.length, pass.length);
        hash = messageDigest.digest(temp);
      }
      System.arraycopy(hash, 0, keys, i, hash.length);
      i += hash.length;
    }
    if (byteskenLen < keys.length) {
      var sliced = new byte[byteskenLen];
      System.arraycopy(keys, 0, sliced, 0, byteskenLen);
      keys = sliced;
    }

    return keys;
  }

}
