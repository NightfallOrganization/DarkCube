/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag holding an {@code int} value.
 *
 * @since 4.0.0
 */
public interface IntBinaryTag extends NumberBinaryTag {
  /**
   * Creates a binary tag holding an {@code int} value.
   *
   * @param value the value
   * @return a binary tag
   * @since 4.0.0
   */
  static @NotNull IntBinaryTag of(final int value) {
    return new IntBinaryTagImpl(value);
  }

  @Override
  default @NotNull BinaryTagType<IntBinaryTag> type() {
    return BinaryTagTypes.INT;
  }

  /**
   * Gets the value.
   *
   * @return the value
   * @since 4.0.0
   */
  int value();
}
