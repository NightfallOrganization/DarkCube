/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.legacyimpl;

import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A legacy {@link HoverEvent} serializer.
 *
 * @since 4.3.0
 */
public interface NBTLegacyHoverEventSerializer extends LegacyHoverEventSerializer {
  /**
   * Gets the legacy {@link HoverEvent} serializer.
   *
   * @return a legacy {@link HoverEvent} serializer
   * @since 4.3.0
   */
  static @NotNull LegacyHoverEventSerializer get() {
    return NBTLegacyHoverEventSerializerImpl.INSTANCE;
  }
}
