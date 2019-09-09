package com.xkenmon.nox.common.util;

public class BytesUtil {

  private static final char[] HEX_CHARS =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String toHexStrWithSpace(byte[] bytes) {
    var chars = toHex(bytes);
    var len = chars.length + (chars.length >> 1) - 1;
    if (len<=0){
      return "";
    }
    var newChars = new char[len];
    int c = 0;
    int idx = 0;
    for (int i = 0; i < len; i++) {
      if (c == 2) {
        newChars[i] = ' ';
        c = 0;
      } else {
        newChars[i] = chars[idx++];
        c++;
      }
    }
    return new String(newChars);
  }

  public static String toHexStr(byte[] bytes) {
    return new String(toHex(bytes));
  }

  public static char[] toHex(byte[] bytes) {
    char[] chars = new char[bytes.length << 1];
    for (int i = 0; i < chars.length; i = i + 2) {
      byte b = bytes[i / 2];
      chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
      chars[i + 1] = HEX_CHARS[b & 0xf];
    }
    return chars;
  }

}
