/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

final class KeySerializer extends TypeAdapter<Key> {
  static final TypeAdapter<Key> INSTANCE = new KeySerializer().nullSafe();

  private KeySerializer() {
  }

  @Override
  public void write(final JsonWriter out, final Key value) throws IOException {
    out.value(value.asString());
  }

  @Override
  public Key read(final JsonReader in) throws IOException {
    return Key.key(in.nextString());
  }
}
