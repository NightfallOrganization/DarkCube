/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.io.IOException;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.text.BlockNBTComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.BlockNBTComponent.Pos;

final class BlockNBTComponentPosSerializer extends TypeAdapter<Pos> {
  static final TypeAdapter<BlockNBTComponent.Pos> INSTANCE = new BlockNBTComponentPosSerializer().nullSafe();

  private BlockNBTComponentPosSerializer() {
  }

  @Override
  public BlockNBTComponent.Pos read(final JsonReader in) throws IOException {
    final String string = in.nextString();
    try {
      return BlockNBTComponent.Pos.fromString(string);
    } catch (final IllegalArgumentException ex) {
      throw new JsonParseException("Don't know how to turn " + string + " into a Position");
    }
  }

  @Override
  public void write(final JsonWriter out, final BlockNBTComponent.Pos value) throws IOException {
    out.value(value.asString());
  }
}
