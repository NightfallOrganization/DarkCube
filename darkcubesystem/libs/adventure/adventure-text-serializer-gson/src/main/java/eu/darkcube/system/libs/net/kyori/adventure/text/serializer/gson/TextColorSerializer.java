/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import java.util.Locale;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class TextColorSerializer extends TypeAdapter<TextColor> {
  static final TypeAdapter<TextColor> INSTANCE = new TextColorSerializer(false).nullSafe();
  static final TypeAdapter<TextColor> DOWNSAMPLE_COLOR = new TextColorSerializer(true).nullSafe();

  private final boolean downsampleColor;

  private TextColorSerializer(final boolean downsampleColor) {
    this.downsampleColor = downsampleColor;
  }

  @Override
  public void write(final JsonWriter out, final TextColor value) throws IOException {
    if (value instanceof NamedTextColor) {
      out.value(NamedTextColor.NAMES.key((NamedTextColor) value));
    } else if (this.downsampleColor) {
      out.value(NamedTextColor.NAMES.key(NamedTextColor.nearestTo(value)));
    } else {
      out.value(asUpperCaseHexString(value));
    }
  }

  private static String asUpperCaseHexString(final TextColor color) {
    return String.format(Locale.ROOT, "#%06X", color.value()); // to be consistent with vanilla
  }

  @Override
  public @Nullable TextColor read(final JsonReader in) throws IOException {
    final @Nullable TextColor color = fromString(in.nextString());
    if (color == null) return null;

    return this.downsampleColor ? NamedTextColor.nearestTo(color) : color;
  }

  static @Nullable TextColor fromString(final @NotNull String value) {
    if (value.startsWith("#")) {
      return TextColor.fromHexString(value);
    } else {
      return NamedTextColor.NAMES.value(value);
    }
  }
}
