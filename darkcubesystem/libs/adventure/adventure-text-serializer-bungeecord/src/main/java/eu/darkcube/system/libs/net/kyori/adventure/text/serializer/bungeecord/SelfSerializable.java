/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.bungeecord;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * A representation of a wrapper adapter, generally between and Adventure type and a native type.
 */
interface SelfSerializable {

  /**
   * Write this object to a json writer.
   *
   * @param out writer to write to
   * @throws IOException when any serialization-related error occurs
   */
  void write(JsonWriter out) throws IOException;

  /**
   * Gson adapter factory that will appropriately unwrap wrapped values.
   */
  class AdapterFactory implements TypeAdapterFactory {

    static {
      SelfSerializableTypeAdapter.class.getName(); // pre-load class
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
      if (!SelfSerializable.class.isAssignableFrom(type.getRawType())) {
        return null;
      }

      return new SelfSerializableTypeAdapter<>(type);
    }

    static class SelfSerializableTypeAdapter<T> extends TypeAdapter<T> {
      private final TypeToken<T> type;

      SelfSerializableTypeAdapter(final TypeToken<T> type) {
        this.type = type;
      }

      @Override
      public void write(final JsonWriter out, final T value) throws IOException {
        ((SelfSerializable) value).write(out);
      }

      @Override
      public T read(final JsonReader in) {
        throw new UnsupportedOperationException("Cannot load values of type " + this.type.getType().getTypeName());
      }
    }
  }
}
