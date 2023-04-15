/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag holding a {@code byte} value.
 *
 * @since 4.0.0
 */
public interface ByteBinaryTag extends NumberBinaryTag {
  /**
   * A tag with the value {@code 0}.
   *
   * @since 4.0.0
   */
  ByteBinaryTag ZERO = new ByteBinaryTagImpl((byte) 0);

  /**
   * A tag with the value {@code 1}.
   *
   * @since 4.0.0
   */
  ByteBinaryTag ONE = new ByteBinaryTagImpl((byte) 1);

  /**
   * Creates a binary tag holding a {@code byte} value.
   *
   * @param value the value
   * @return a binary tag
   * @since 4.0.0
   */
  static @NotNull ByteBinaryTag of(final byte value) {
    if (value == 0) {
      return ZERO;
    } else if (value == 1) {
      return ONE;
    } else {
      return new ByteBinaryTagImpl(value);
    }
  }

  @Override
  default @NotNull BinaryTagType<ByteBinaryTag> type() {
    return BinaryTagTypes.BYTE;
  }

  /**
   * Gets the value.
   *
   * @return the value
   * @since 4.0.0
   */
  byte value();
}
