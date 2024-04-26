/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag holding a {@code byte}-array value.
 *
 * @since 4.0.0
 */
public interface ByteArrayBinaryTag extends ArrayBinaryTag, Iterable<Byte> {
  /**
   * Creates a binary tag holding a {@code byte}-array value.
   *
   * @param value the value
   * @return a binary tag
   * @since 4.0.0
   */
  static @NotNull ByteArrayBinaryTag of(final byte@NotNull... value) {
    return new ByteArrayBinaryTagImpl(value);
  }

  @Override
  default @NotNull BinaryTagType<ByteArrayBinaryTag> type() {
    return BinaryTagTypes.BYTE_ARRAY;
  }

  /**
   * Gets the value.
   *
   * <p>The returned array is a copy.</p>
   *
   * @return the value
   * @since 4.0.0
   */
  byte@NotNull[] value();

  /**
   * Get the size of the array.
   *
   * @return array size
   * @since 4.2.0
   */
  int size();

  /**
   * Gets the value at {@code index} in this tag.
   *
   * @param index the index in the array
   * @return the byte at the index in the array
   * @throws IndexOutOfBoundsException if index is &lt; 0 or &ge; {@link #size()}
   * @since 4.2.0
   */
  byte get(final int index);
}
