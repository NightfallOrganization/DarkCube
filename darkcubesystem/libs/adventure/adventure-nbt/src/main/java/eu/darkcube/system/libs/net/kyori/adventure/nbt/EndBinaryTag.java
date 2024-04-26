/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * An end tag.
 *
 * @since 4.0.0
 */
public interface EndBinaryTag extends BinaryTag {
  /**
   * Gets the end tag.
   *
   * @return the end tag
   * @since 4.0.0
   */
  static @NotNull EndBinaryTag get() {
    return EndBinaryTagImpl.INSTANCE;
  }

  @Override
  default @NotNull BinaryTagType<EndBinaryTag> type() {
    return BinaryTagTypes.END;
  }
}
