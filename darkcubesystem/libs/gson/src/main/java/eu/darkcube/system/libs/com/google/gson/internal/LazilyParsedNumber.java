/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.com.google.gson.internal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;

/**
 * This class holds a number value that is lazily converted to a specific number type
 *
 * @author Inderjeet Singh
 */
@SuppressWarnings("serial") // ignore warning about missing serialVersionUID
public final class LazilyParsedNumber extends Number {
  private final String value;

  /** @param value must not be null */
  public LazilyParsedNumber(String value) {
    this.value = value;
  }

  @Override
  public int intValue() {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      try {
        return (int) Long.parseLong(value);
      } catch (NumberFormatException nfe) {
        return new BigDecimal(value).intValue();
      }
    }
  }

  @Override
  public long longValue() {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      return new BigDecimal(value).longValue();
    }
  }

  @Override
  public float floatValue() {
    return Float.parseFloat(value);
  }

  @Override
  public double doubleValue() {
    return Double.parseDouble(value);
  }

  @Override
  public String toString() {
    return value;
  }

  /**
   * If somebody is unlucky enough to have to serialize one of these, serialize
   * it as a BigDecimal so that they won't need Gson on the other side to
   * deserialize it.
   */
  private Object writeReplace() throws ObjectStreamException {
    return new BigDecimal(value);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    // Don't permit directly deserializing this class; writeReplace() should have written a replacement
    throw new InvalidObjectException("Deserialization is unsupported");
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof LazilyParsedNumber) {
      LazilyParsedNumber other = (LazilyParsedNumber) obj;
      return value == other.value || value.equals(other.value);
    }
    return false;
  }
}
