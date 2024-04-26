/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag holding a {@code long}-array value.
 *
 * @since 4.0.0
 * @sinceMinecraft 1.12
 */
public interface LongArrayBinaryTag extends ArrayBinaryTag, Iterable<Long> {
  /**
   * Creates a binary tag holding a {@code long}-array value.
   *
   * @param value the value
   * @return a binary tag
   * @since 4.0.0
   */
  static @NotNull LongArrayBinaryTag of(final long@NotNull... value) {
    return new LongArrayBinaryTagImpl(value);
  }

  @Override
  default @NotNull BinaryTagType<LongArrayBinaryTag> type() {
    return BinaryTagTypes.LONG_ARRAY;
  }

  /**
   * Gets the value.
   *
   * <p>The returned array is a copy.</p>
   *
   * @return the value
   * @since 4.0.0
   */
  long@NotNull[] value();

  /**
   * Gets the length of the array.
   *
   * @return value size
   * @since 4.2.0
   */
  int size();

  /**
   * Gets the value at {@code index} in this tag.
   *
   * @param index the index in the array
   * @return the long at the index in the array
   * @throws IndexOutOfBoundsException if index is &lt; 0 or &ge; {@link #size()}
   * @since 4.2.0
   */
  long get(final int index);

  /**
   * {@inheritDoc}
   *
   * <p>The returned iterator is immutable.</p>
   *
   * @since 4.2.0
   */
  @Override
  PrimitiveIterator.@NotNull OfLong iterator();

  @Override
  Spliterator.@NotNull OfLong spliterator();

  /**
   * Create a stream whose elements are the elements of this array tag.
   *
   * @return a new stream
   * @since 4.2.0
   */
  @NotNull LongStream stream();

  /**
   * Perform an action for every long in the backing array.
   *
   * @param action the action to perform
   * @since 4.2.0
   */
  void forEachLong(final @NotNull LongConsumer action);
}
