/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.io.IOException;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/*
 * This is a hack.
 */
final class TextColorWrapper {
  final @Nullable TextColor color;
  final @Nullable TextDecoration decoration;
  final boolean reset;

  TextColorWrapper(final @Nullable TextColor color, final @Nullable TextDecoration decoration, final boolean reset) {
    this.color = color;
    this.decoration = decoration;
    this.reset = reset;
  }

  static final class Serializer extends TypeAdapter<TextColorWrapper> {
    static final Serializer INSTANCE = new Serializer();

    private Serializer() {
    }

    @Override
    public void write(final JsonWriter out, final TextColorWrapper value) {
      throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
    }

    @Override
    public TextColorWrapper read(final JsonReader in) throws IOException {
      final String input = in.nextString();
      final TextColor color = TextColorSerializer.fromString(input);
      final TextDecoration decoration = TextDecoration.NAMES.value(input);
      final boolean reset = decoration == null && input.equals("reset");
      if (color == null && decoration == null && !reset) {
        throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath());
      }
      return new TextColorWrapper(color, decoration, reset);
    }
  }
}
