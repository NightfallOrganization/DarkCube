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
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Debug;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Debug.Renderer(text = "\"int[\" + this.value.length + \"]\"", childrenArray = "this.value", hasChildren = "this.value.length > 0")
final class IntArrayBinaryTagImpl extends ArrayBinaryTagImpl implements IntArrayBinaryTag {
  final int[] value;

  IntArrayBinaryTagImpl(final int... value) {
    this.value = Arrays.copyOf(value, value.length);
  }

  @Override
  public int@NotNull[] value() {
    return Arrays.copyOf(this.value, this.value.length);
  }

  @Override
  public int size() {
    return this.value.length;
  }

  @Override
  public int get(final int index) {
    checkIndex(index, this.value.length);
    return this.value[index];
  }

  @Override
  public PrimitiveIterator.@NotNull OfInt iterator() {
    return new PrimitiveIterator.OfInt() {
      private int index;

      @Override
      public boolean hasNext() {
        return this.index < (IntArrayBinaryTagImpl.this.value.length - 1);
      }

      @Override
      public int nextInt() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        }
        return IntArrayBinaryTagImpl.this.value[this.index++];
      }
    };
  }

  @Override
  public Spliterator.@NotNull OfInt spliterator() {
    return Arrays.spliterator(this.value);
  }

  @Override
  public @NotNull IntStream stream() {
    return Arrays.stream(this.value);
  }

  @Override
  public void forEachInt(final @NotNull IntConsumer action) {
    for (int i = 0, length = this.value.length; i < length; i++) {
      action.accept(this.value[i]);
    }
  }

  // to avoid copying array internally
  static int[] value(final IntArrayBinaryTag tag) {
    return (tag instanceof IntArrayBinaryTagImpl) ? ((IntArrayBinaryTagImpl) tag).value : tag.value();
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final IntArrayBinaryTagImpl that = (IntArrayBinaryTagImpl) other;
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
