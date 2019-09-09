package com.xkenmon.nox.ssocks.crypto;

import com.xkenmon.nox.ssocks.crypto.impl.AESCFBDecryptor;
import com.xkenmon.nox.ssocks.crypto.impl.AESCFBEncryptor;
import com.xkenmon.nox.ssocks.crypto.impl.AESCTRDecryptor;
import com.xkenmon.nox.ssocks.crypto.impl.AESCTREncryptor;
import com.xkenmon.nox.ssocks.util.SSocksKeyGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoFactory {

  private static final Queue<Cryptor> aescfbEncryptorQueue = new ConcurrentLinkedQueue<>();
  private static final Queue<Cryptor> aescfbDecryptorQueue = new ConcurrentLinkedQueue<>();

  private static final Queue<Cryptor> aesctrEncryptorQueue = new ConcurrentLinkedQueue<>();
  private static final Queue<Cryptor> aesctrDecryptorQueue = new ConcurrentLinkedQueue<>();

  private static final Map<Class<? extends Cryptor>, Queue<Cryptor>> queueMap = new HashMap<>() {
    {
      put(AESCFBEncryptor.class, aescfbEncryptorQueue);
      put(AESCFBDecryptor.class, aescfbDecryptorQueue);

      put(AESCTREncryptor.class, aesctrEncryptorQueue);
      put(AESCTRDecryptor.class, aesctrDecryptorQueue);
    }
  };

  private static final Map<Class<? extends Cryptor>, Supplier<? extends Cryptor>> createMap = new HashMap<>() {
    {
      put(AESCFBEncryptor.class, AESCFBEncryptor::new);
      put(AESCFBDecryptor.class, AESCFBDecryptor::new);
      put(AESCTREncryptor.class, AESCTREncryptor::new);
      put(AESCTRDecryptor.class, AESCTRDecryptor::new);
    }
  };

  private static AtomicInteger count = new AtomicInteger();

  public static void addCryptor(Cryptor... cryptors) {
    for (var cryptor : cryptors) {
      var queue = queueMap.get(cryptor.getClass());
      if (queue == null) {
        throw new IllegalArgumentException("unknow crytor class type.");
      }
      queue.add(cryptor);
    }
  }

  public static Decryptor getDecryptor(String method, String password) {
    switch (method) {
      case "aes-256-cfb": {
        var key = SSocksKeyGenerator.of(password, 256 / 8);
        return getAESCFBDecryptor(key);
      }
      case "aes-192-cfb": {
        var key = SSocksKeyGenerator.of(password, 192 / 8);
        return getAESCFBDecryptor(key);
      }
      case "aes-128-cfb": {
        var key = SSocksKeyGenerator.of(password, 128 / 8);
        return getAESCFBDecryptor(key);
      }
      case "aes-256-ctr": {
        var key = SSocksKeyGenerator.of(password, 256 / 8);
        return getAESCTRDecryptor(key);
      }
      case "aes-192-ctr": {
        var key = SSocksKeyGenerator.of(password, 192 / 8);
        return getAESCTRDecryptor(key);
      }
      case "aes-128-ctr": {
        var key = SSocksKeyGenerator.of(password, 128 / 8);
        return getAESCTRDecryptor(key);
      }
      default:
        throw new IllegalArgumentException("no such crypto method: " + method);
    }
  }

  public static Encryptor getEncryptor(String method, String password) {
    switch (method) {
      case "aes-256-cfb": {
        var key = SSocksKeyGenerator.of(password, 256 / 8);
        return getAESCFBEncryptor(key);
      }
      case "aes-192-cfb": {
        var key = SSocksKeyGenerator.of(password, 192 / 8);
        return getAESCFBEncryptor(key);
      }
      case "aes-128-cfb": {
        var key = SSocksKeyGenerator.of(password, 128 / 8);
        return getAESCFBEncryptor(key);
      }
      case "aes-256-ctr": {
        var key = SSocksKeyGenerator.of(password, 256 / 8);
        return getAESCTREncryptor(key);
      }
      case "aes-192-ctr": {
        var key = SSocksKeyGenerator.of(password, 192 / 8);
        return getAESCTREncryptor(key);
      }
      case "aes-128-ctr": {
        var key = SSocksKeyGenerator.of(password, 128 / 8);
        return getAESCTREncryptor(key);
      }
      default:
        throw new IllegalArgumentException("no such crypto method: " + method);
    }
  }


  public static AESCFBDecryptor getAESCFBDecryptor(byte[] key) {
    return (AESCFBDecryptor) getCryptor(AESCFBDecryptor.class, key);
  }

  public static AESCFBEncryptor getAESCFBEncryptor(byte[] key) {
    return (AESCFBEncryptor) getCryptor(AESCFBEncryptor.class, key);
  }

  public static AESCTRDecryptor getAESCTRDecryptor(byte[] key) {
    return (AESCTRDecryptor) getCryptor(AESCTRDecryptor.class, key);
  }

  public static AESCTREncryptor getAESCTREncryptor(byte[] key) {
    return (AESCTREncryptor) getCryptor(AESCTREncryptor.class, key);
  }

  public static Cryptor getCryptor(Class<? extends Cryptor> type, byte[] key) {
    var cryptor = queueMap.get(type).poll();
    if (cryptor == null) {
      cryptor = createInstance(type);
    }
    cryptor.setKey(key);
    return cryptor;
  }

  private static Cryptor createInstance(@NonNull Class<? extends Cryptor> type) {
    var cryptor = createMap.getOrDefault(type, () -> null).get();
    if (cryptor == null) {
      log.warn("unsupported crypto type: {}", type);
    } else {
      count.incrementAndGet();
      log.debug("current cryptors count: {}", count);
    }
    return cryptor;
  }

}
