/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag holding a {@code short} value.
 *
 * @since 4.0.0
 */
public interface ShortBinaryTag extends NumberBinaryTag {
  /**
   * Creates a binary tag holding a {@code short} value.
   *
   * @param value the value
   * @return a binary tag
   * @since 4.0.0
   */
  static @NotNull ShortBinaryTag of(final short value) {
    return new ShortBinaryTagImpl(value);
  }

  @Override
  default @NotNull BinaryTagType<ShortBinaryTag> type() {
    return BinaryTagTypes.SHORT;
  }

  /**
   * Gets the value.
   *
   * @return the value
   * @since 4.0.0
   */
  short value();
}
