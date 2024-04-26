/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Debug;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Debug.Renderer(text = "\"long[\" + this.value.length + \"]\"", childrenArray = "this.value", hasChildren = "this.value.length > 0")
final class LongArrayBinaryTagImpl extends ArrayBinaryTagImpl implements LongArrayBinaryTag {
  final long[] value;

  LongArrayBinaryTagImpl(final long[] value) {
    this.value = Arrays.copyOf(value, value.length);
  }

  @Override
  public long@NotNull[] value() {
    return Arrays.copyOf(this.value, this.value.length);
  }

  @Override
  public int size() {
    return this.value.length;
  }

  @Override
  public long get(final int index) {
    checkIndex(index, this.value.length);
    return this.value[index];
  }

  @Override
  public PrimitiveIterator.@NotNull OfLong iterator() {
    return new PrimitiveIterator.OfLong() {
      private int index;

      @Override
      public boolean hasNext() {
        return this.index < (LongArrayBinaryTagImpl.this.value.length - 1);
      }

      @Override
      public long nextLong() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        }
        return LongArrayBinaryTagImpl.this.value[this.index++];
      }
    };
  }

  @Override
  public Spliterator.@NotNull OfLong spliterator() {
    return Arrays.spliterator(this.value);
  }

  @Override
  public @NotNull LongStream stream() {
    return Arrays.stream(this.value);
  }

  @Override
  public void forEachLong(final @NotNull LongConsumer action) {
    for (int i = 0, length = this.value.length; i < length; i++) {
      action.accept(this.value[i]);
    }
  }

  // to avoid copying array internally
  static long[] value(final LongArrayBinaryTag tag) {
    return (tag instanceof LongArrayBinaryTagImpl) ? ((LongArrayBinaryTagImpl) tag).value : tag.value();
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final LongArrayBinaryTagImpl that = (LongArrayBinaryTagImpl) other;
    return Arrays.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.value);
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("value", this.value));
  }
}
