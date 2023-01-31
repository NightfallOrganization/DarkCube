/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.Map;
import java.util.function.Consumer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Common methods between {@link CompoundBinaryTag} and {@link CompoundBinaryTag.Builder}.
 *
 * @param <R> the return type
 * @since 4.0.0
 */
public interface CompoundTagSetter<R> {
  /**
   * Inserts a tag.
   *
   * @param key the key
   * @param tag the tag
   * @return a compound tag
   * @since 4.0.0
   */
  @NotNull R put(final @NotNull String key, final @NotNull BinaryTag tag);

  /**
   * Inserts the tags in {@code tag}, overwriting any that are in {@code this}.
   *
   * @param tag the tag
   * @return a compound tag
   * @since 4.6.0
   */
  @NotNull R put(final @NotNull CompoundBinaryTag tag);

  /**
   * Inserts some tags.
   *
   * @param tags the tags
   * @return a compound tag
   * @since 4.4.0
   */
  @NotNull R put(final @NotNull Map<String, ? extends BinaryTag> tags);

  /**
   * Removes a tag.
   *
   * @param key the key
   * @return a compound tag
   * @since 4.4.0
   */
  default @NotNull R remove(final @NotNull String key) {
    return this.remove(key, null);
  }

  /**
   * Removes a tag.
   *
   * @param key the key
   * @param removed a consumer that accepts the removed tag
   * @return a compound tag
   * @since 4.4.0
   */
  @NotNull R remove(final @NotNull String key, final @Nullable Consumer<? super BinaryTag> removed);

  /**
   * Inserts a boolean.
   *
   * <p>Booleans are stored as a {@link ByteBinaryTag} with a value of {@code 0} for {@code false} and {@code 1} for {@code true}.</p>
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putBoolean(final @NotNull String key, final boolean value) {
    return this.put(key, value ? ByteBinaryTag.ONE : ByteBinaryTag.ZERO);
  }

  /**
   * Inserts a byte.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putByte(final @NotNull String key, final byte value) {
    return this.put(key, ByteBinaryTag.of(value));
  }

  /**
   * Inserts a short.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putShort(final @NotNull String key, final short value) {
    return this.put(key, ShortBinaryTag.of(value));
  }

  /**
   * Inserts an int.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putInt(final @NotNull String key, final int value) {
    return this.put(key, IntBinaryTag.of(value));
  }

  /**
   * Inserts a long.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putLong(final @NotNull String key, final long value) {
    return this.put(key, LongBinaryTag.of(value));
  }

  /**
   * Inserts a float.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putFloat(final @NotNull String key, final float value) {
    return this.put(key, FloatBinaryTag.of(value));
  }

  /**
   * Inserts a double.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putDouble(final @NotNull String key, final double value) {
    return this.put(key, DoubleBinaryTag.of(value));
  }

  /**
   * Inserts an array of bytes.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putByteArray(final @NotNull String key, final byte@NotNull[] value) {
    return this.put(key, ByteArrayBinaryTag.of(value));
  }

  /**
   * Inserts a string.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putString(final @NotNull String key, final @NotNull String value) {
    return this.put(key, StringBinaryTag.of(value));
  }

  /**
   * Inserts an array of ints.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putIntArray(final @NotNull String key, final int@NotNull[] value) {
    return this.put(key, IntArrayBinaryTag.of(value));
  }

  /**
   * Inserts an array of longs.
   *
   * @param key the key
   * @param value the value
   * @return a compound tag
   * @since 4.0.0
   */
  default @NotNull R putLongArray(final @NotNull String key, final long@NotNull[] value) {
    return this.put(key, LongArrayBinaryTag.of(value));
  }
}