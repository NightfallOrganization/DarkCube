/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag holding an {@code int}-array value.
 *
 * @since 4.0.0
 * @sinceMinecraft 1.2.1
 */
public interface IntArrayBinaryTag extends ArrayBinaryTag, Iterable<Integer> {
  /**
   * Creates a binary tag holding an {@code int}-array value.
   *
   * @param value the value
   * @return a binary tag
   * @since 4.0.0
   */
  static @NotNull IntArrayBinaryTag of(final int@NotNull... value) {
    return new IntArrayBinaryTagImpl(value);
  }

  @Override
  default @NotNull BinaryTagType<IntArrayBinaryTag> type() {
    return BinaryTagTypes.INT_ARRAY;
  }

  /**
   * Gets the value.
   *
   * <p>The returned array is a copy.</p>
   *
   * @return the value
   * @since 4.0.0
   */
  int@NotNull[] value();

  /**
   * Get the length of the array.
   *
   * @return value size
   * @since 4.2.0
   */
  int size();

  /**
   * Gets the value at {@code index} in this tag.
   *
   * @param index the index in the array
   * @return the int at the index in the array
   * @throws IndexOutOfBoundsException if idx &lt; 0 or &ge; {@link #size()}
   * @since 4.2.0
   */
  int get(final int index);

  /**
   * {@inheritDoc}
   *
   * <p>The returned iterator is immutable.</p>
   *
   * @since 4.2.0
   */
  @Override
  PrimitiveIterator.@NotNull OfInt iterator();

  @Override
  Spliterator.@NotNull OfInt spliterator();

  /**
   * Create a stream whose elements are the elements of this array tag.
   *
   * @return a new stream
   * @since 4.2.0
   */
  @NotNull IntStream stream();

  /**
   * Perform an action for every int in the backing array.
   *
   * @param action the action to perform
   * @since 4.2.0
   */
  void forEachInt(final @NotNull IntConsumer action);
}
