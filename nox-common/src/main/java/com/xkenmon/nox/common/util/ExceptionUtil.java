package com.xkenmon.nox.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {

  public static String stackTraceToString(Throwable cause) {
    var writer = new PrintWriter(new StringWriter());
    cause.printStackTrace(writer);
    return writer.toString();
  }

}
