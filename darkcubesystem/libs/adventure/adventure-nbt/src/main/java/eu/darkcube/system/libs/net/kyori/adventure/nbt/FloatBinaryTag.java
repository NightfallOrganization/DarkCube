/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag holding a {@code float} value.
 *
 * @since 4.0.0
 */
public interface FloatBinaryTag extends NumberBinaryTag {
  /**
   * Creates a binary tag holding a {@code float} value.
   *
   * @param value the value
   * @return a binary tag
   * @since 4.0.0
   */
  static @NotNull FloatBinaryTag of(final float value) {
    return new FloatBinaryTagImpl(value);
  }

  @Override
  default @NotNull BinaryTagType<FloatBinaryTag> type() {
    return BinaryTagTypes.FLOAT;
  }

  /**
   * Gets the value.
   *
   * @return the value
   * @since 4.0.0
   */
  float value();
}
