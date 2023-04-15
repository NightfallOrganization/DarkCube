/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson;

/**
 * A class representing a JSON {@code null} value.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @since 1.2
 */
public final class JsonNull extends JsonElement {
  /**
   * Singleton for {@code JsonNull}.
   *
   * @since 1.8
   */
  public static final JsonNull INSTANCE = new JsonNull();

  /**
   * Creates a new {@code JsonNull} object.
   *
   * @deprecated Deprecated since Gson version 1.8, use {@link #INSTANCE} instead.
   */
  @Deprecated
  public JsonNull() {
    // Do nothing
  }

  /**
   * Returns the same instance since it is an immutable value.
   *
   * @since 2.8.2
   */
  @Override
  public JsonNull deepCopy() {
    return INSTANCE;
  }

  /**
   * All instances of {@code JsonNull} have the same hash code since they are indistinguishable.
   */
  @Override
  public int hashCode() {
    return JsonNull.class.hashCode();
  }

  /**
   * All instances of {@code JsonNull} are considered equal.
   */
  @Override
  public boolean equals(Object other) {
    return other instanceof JsonNull;
  }
}
