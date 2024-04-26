/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson;

/**
 * Defines the expected format for a {@code long} or {@code Long} type when it is serialized.
 *
 * @since 1.3
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public enum LongSerializationPolicy {
  /**
   * This is the "default" serialization policy that will output a {@code Long} object as a JSON
   * number. For example, assume an object has a long field named "f" then the serialized output
   * would be:
   * {@code {"f":123}}
   *
   * <p>A {@code null} value is serialized as {@link JsonNull}.
   */
  DEFAULT() {
    @Override public JsonElement serialize(Long value) {
      if (value == null) {
        return JsonNull.INSTANCE;
      }
      return new JsonPrimitive(value);
    }
  },
  
  /**
   * Serializes a long value as a quoted string. For example, assume an object has a long field 
   * named "f" then the serialized output would be:
   * {@code {"f":"123"}}
   *
   * <p>A {@code null} value is serialized as {@link JsonNull}.
   */
  STRING() {
    @Override public JsonElement serialize(Long value) {
      if (value == null) {
        return JsonNull.INSTANCE;
      }
      return new JsonPrimitive(value.toString());
    }
  };
  
  /**
   * Serialize this {@code value} using this serialization policy.
   *
   * @param value the long value to be serialized into a {@link JsonElement}
   * @return the serialized version of {@code value}
   */
  public abstract JsonElement serialize(Long value);
}
