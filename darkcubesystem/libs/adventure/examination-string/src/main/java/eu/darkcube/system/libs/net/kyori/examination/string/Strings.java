/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.examination.string;

import java.util.stream.Stream;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

final class Strings {
  private Strings() {
  }

  static @NotNull String withSuffix(final String string, final char suffix) {
    return string + suffix;
  }

  static @NotNull String wrapIn(final String string, final char wrap) {
    return wrap + string + wrap;
  }

  static int maxLength(final Stream<String> strings) {
    return strings.mapToInt(String::length).max().orElse(0);
  }

  static @NotNull String repeat(final @NotNull String string, final int count) {
    if (count == 0) {
      return "";
    } else if (count == 1) {
      return string;
    }
    final StringBuilder sb = new StringBuilder(string.length() * count);
    for (int i = 0; i < count; ++i) {
      sb.append(string);
    }
    return sb.toString();
  }

  static @NotNull String padEnd(final @NotNull String string, final int minLength, final char padding) {
    return string.length() >= minLength
      ? string
      : String.format("%-" + minLength + "s", padding);
  }
}
