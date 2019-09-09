package com.xkenmon.nox.common.util;

public class StringUtil {

  static public boolean isBlank(String s) {
    return s == null || s.isEmpty();
  }

  static public boolean notBlank(String s){
    return s!=null&&!s.isEmpty();
  }

}
