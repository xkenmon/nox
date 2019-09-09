package com.xkenmon.nox.ssocks.crypto;

import com.xkenmon.nox.common.util.BytesUtil;
import com.xkenmon.nox.ssocks.util.SSocksKeyGenerator;
import java.util.stream.IntStream;
import org.junit.Test;

public class SSocksKeyGeneratorTest {

  @Test
  public void gen() {
    IntStream.rangeClosed(0, 5).forEach(i -> {
      var data = SSocksKeyGenerator.of("mengxiangkun", 256 / 8);
      System.out.println("key len: " + data.length);
      System.out.println(BytesUtil.toHexStrWithSpace(data));
    });
  }
}