/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;

final class TextDecorationSerializer {
  static final TypeAdapter<TextDecoration>
          INSTANCE = IndexedSerializer.strict("text decoration", TextDecoration.NAMES);

  private TextDecorationSerializer() {
  }
}