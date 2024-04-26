/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal.bind;

import eu.darkcube.system.libs.com.google.gson.annotations.JsonAdapter;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonDeserializer;
import eu.darkcube.system.libs.com.google.gson.JsonSerializer;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.TypeAdapterFactory;
import eu.darkcube.system.libs.com.google.gson.internal.ConstructorConstructor;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;

/**
 * Given a type T, looks for the annotation {@link JsonAdapter} and uses an instance of the
 * specified class as the default type adapter.
 *
 * @since 2.3
 */
public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;

  public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
    this.constructorConstructor = constructorConstructor;
  }

  @SuppressWarnings("unchecked") // this is not safe; requires that user has specified correct adapter class for @JsonAdapter
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
    Class<? super T> rawType = targetType.getRawType();
    JsonAdapter annotation = rawType.getAnnotation(JsonAdapter.class);
    if (annotation == null) {
      return null;
    }
    return (TypeAdapter<T>) getTypeAdapter(constructorConstructor, gson, targetType, annotation);
  }

  TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson,
      TypeToken<?> type, JsonAdapter annotation) {
    Object instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();

    TypeAdapter<?> typeAdapter;
    boolean nullSafe = annotation.nullSafe();
    if (instance instanceof TypeAdapter) {
      typeAdapter = (TypeAdapter<?>) instance;
    } else if (instance instanceof TypeAdapterFactory) {
      typeAdapter = ((TypeAdapterFactory) instance).create(gson, type);
    } else if (instance instanceof JsonSerializer || instance instanceof JsonDeserializer) {
      JsonSerializer<?> serializer = instance instanceof JsonSerializer
          ? (JsonSerializer<?>) instance
          : null;
      JsonDeserializer<?> deserializer = instance instanceof JsonDeserializer
          ? (JsonDeserializer<?>) instance
          : null;

      @SuppressWarnings({ "unchecked", "rawtypes" })
      TypeAdapter<?> tempAdapter = new TreeTypeAdapter(serializer, deserializer, gson, type, null, nullSafe);
      typeAdapter = tempAdapter;

      nullSafe = false;
    } else {
      throw new IllegalArgumentException("Invalid attempt to bind an instance of "
          + instance.getClass().getName() + " as a @JsonAdapter for " + type.toString()
          + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory,"
          + " JsonSerializer or JsonDeserializer.");
    }

    if (typeAdapter != null && nullSafe) {
      typeAdapter = typeAdapter.nullSafe();
    }

    return typeAdapter;
  }
}
