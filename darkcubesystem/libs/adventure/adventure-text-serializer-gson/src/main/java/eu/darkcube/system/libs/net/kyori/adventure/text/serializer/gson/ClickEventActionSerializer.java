/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent.Action;

final class ClickEventActionSerializer {
  static final TypeAdapter<Action> INSTANCE = IndexedSerializer.lenient("click action", ClickEvent.Action.NAMES);

  private ClickEventActionSerializer() {
  }
}
