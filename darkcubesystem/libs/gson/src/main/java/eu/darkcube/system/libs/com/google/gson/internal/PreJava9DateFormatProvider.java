/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.com.google.gson.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Provides DateFormats for US locale with patterns which were the default ones before Java 9.
 */
public class PreJava9DateFormatProvider {

  /**
   * Returns the same DateFormat as {@code DateFormat.getDateInstance(style, Locale.US)} in Java 8 or below.
   */
  public static DateFormat getUSDateFormat(int style) {
    return new SimpleDateFormat(getDateFormatPattern(style), Locale.US);
  }

  /**
   * Returns the same DateFormat as {@code DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US)}
   * in Java 8 or below.
   */
  public static DateFormat getUSDateTimeFormat(int dateStyle, int timeStyle) {
    String pattern = getDatePartOfDateTimePattern(dateStyle) + " " + getTimePartOfDateTimePattern(timeStyle);
    return new SimpleDateFormat(pattern, Locale.US);
  }

  private static String getDateFormatPattern(int style) {
    switch (style) {
    case DateFormat.SHORT:
      return "M/d/yy";
    case DateFormat.MEDIUM:
      return "MMM d, y";
    case DateFormat.LONG:
      return "MMMM d, y";
    case DateFormat.FULL:
      return "EEEE, MMMM d, y";
    default:
      throw new IllegalArgumentException("Unknown DateFormat style: " + style);
    }
  }

  private static String getDatePartOfDateTimePattern(int dateStyle) {
    switch (dateStyle) {
    case DateFormat.SHORT:
      return "M/d/yy";
    case DateFormat.MEDIUM:
      return "MMM d, yyyy";
    case DateFormat.LONG:
      return "MMMM d, yyyy";
    case DateFormat.FULL:
      return "EEEE, MMMM d, yyyy";
    default:
      throw new IllegalArgumentException("Unknown DateFormat style: " + dateStyle);
    }
  }

  private static String getTimePartOfDateTimePattern(int timeStyle) {
    switch (timeStyle) {
    case DateFormat.SHORT:
      return "h:mm a";
    case DateFormat.MEDIUM:
      return "h:mm:ss a";
    case DateFormat.FULL:
    case DateFormat.LONG:
      return "h:mm:ss a z";
    default:
      throw new IllegalArgumentException("Unknown DateFormat style: " + timeStyle);
    }
  }
}
