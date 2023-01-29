/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Debug;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Debug.Renderer(text = "\"byte[\" + this.value.length + \"]\"", childrenArray = "this.value", hasChildren = "this.value.length > 0")
final class ByteArrayBinaryTagImpl extends ArrayBinaryTagImpl implements ByteArrayBinaryTag {
  final byte[] value;

  ByteArrayBinaryTagImpl(final byte[] value) {
    this.value = Arrays.copyOf(value, value.length);
  }

  @Override
  public byte@NotNull[] value() {
    return Arrays.copyOf(this.value, this.value.length);
  }

  @Override
  public int size() {
    return this.value.length;
  }

  @Override
  public byte get(final int index) {
    checkIndex(index, this.value.length);
    return this.value[index];
  }

  // to avoid copying array internally
  static byte[] value(final ByteArrayBinaryTag tag) {
    return (tag instanceof ByteArrayBinaryTagImpl) ? ((ByteArrayBinaryTagImpl) tag).value : tag.value();
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final ByteArrayBinaryTagImpl that = (ByteArrayBinaryTagImpl) other;
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

  @Override
  public Iterator<Byte> iterator() {
    return new Iterator<Byte>() {
      private int index;

      @Override
      public boolean hasNext() {
        return this.index < ByteArrayBinaryTagImpl.this.value.length - 1;
      }

      @Override
      public Byte next() {
        return ByteArrayBinaryTagImpl.this.value[this.index++];
      }
    };
  }
}
