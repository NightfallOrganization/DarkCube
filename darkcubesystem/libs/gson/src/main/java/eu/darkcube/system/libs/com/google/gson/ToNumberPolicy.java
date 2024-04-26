/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson;

import eu.darkcube.system.libs.com.google.gson.internal.LazilyParsedNumber;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * An enumeration that defines two standard number reading strategies and a couple of
 * strategies to overcome some historical Gson limitations while deserializing numbers as
 * {@link Object} and {@link Number}.
 *
 * @see ToNumberStrategy
 * @since 2.8.9
 */
public enum ToNumberPolicy implements ToNumberStrategy {

  /**
   * Using this policy will ensure that numbers will be read as {@link Double} values.
   * This is the default strategy used during deserialization of numbers as {@link Object}.
   */
  DOUBLE {
    @Override public Double readNumber(JsonReader in) throws IOException {
      return in.nextDouble();
    }
  },

  /**
   * Using this policy will ensure that numbers will be read as a lazily parsed number backed
   * by a string. This is the default strategy used during deserialization of numbers as
   * {@link Number}.
   */
  LAZILY_PARSED_NUMBER {
    @Override public Number readNumber(JsonReader in) throws IOException {
      return new LazilyParsedNumber(in.nextString());
    }
  },

  /**
   * Using this policy will ensure that numbers will be read as {@link Long} or {@link Double}
   * values depending on how JSON numbers are represented: {@code Long} if the JSON number can
   * be parsed as a {@code Long} value, or otherwise {@code Double} if it can be parsed as a
   * {@code Double} value. If the parsed double-precision number results in a positive or negative
   * infinity ({@link Double#isInfinite()}) or a NaN ({@link Double#isNaN()}) value and the
   * {@code JsonReader} is not {@link JsonReader#isLenient() lenient}, a {@link MalformedJsonException}
   * is thrown.
   */
  LONG_OR_DOUBLE {
    @Override public Number readNumber(JsonReader in) throws IOException, JsonParseException {
      String value = in.nextString();
      try {
        return Long.parseLong(value);
      } catch (NumberFormatException longE) {
        try {
          Double d = Double.valueOf(value);
          if ((d.isInfinite() || d.isNaN()) && !in.isLenient()) {
            throw new MalformedJsonException("JSON forbids NaN and infinities: " + d + "; at path " + in.getPreviousPath());
          }
          return d;
        } catch (NumberFormatException doubleE) {
          throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), doubleE);
        }
      }
    }
  },

  /**
   * Using this policy will ensure that numbers will be read as numbers of arbitrary length
   * using {@link BigDecimal}.
   */
  BIG_DECIMAL {
    @Override public BigDecimal readNumber(JsonReader in) throws IOException {
      String value = in.nextString();
      try {
        return new BigDecimal(value);
      } catch (NumberFormatException e) {
        throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), e);
      }
    }
  }

}
