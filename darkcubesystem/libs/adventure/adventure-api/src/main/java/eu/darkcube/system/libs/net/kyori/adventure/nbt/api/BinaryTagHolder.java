/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt.api;

import eu.darkcube.system.libs.net.kyori.adventure.util.Codec;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Holds a compound binary tag.
 *
 * <p>Instead of including an entire NBT implementation in Adventure, it was decided to
 * use this "holder" interface instead. This opens the door for platform specific implementations.</p>
 *
 * <p>See {@code net.kyori.adventure.nbt.impl} for a platform agnostic implementation.</p>
 *
 * @since 4.0.0
 */
public interface BinaryTagHolder {
  /**
   * Encodes {@code nbt} using {@code codec}.
   *
   * @param nbt the binary tag
   * @param codec the codec
   * @param <T> the binary tag type
   * @param <EX> encode exception type
   * @return the encoded binary tag holder
   * @throws EX if an error occurred while encoding the binary tag
   * @since 4.0.0
   */
  static <T, EX extends Exception> @NotNull BinaryTagHolder encode(final @NotNull T nbt, final @NotNull Codec<? super T, String, ?, EX> codec) throws EX {
    return new BinaryTagHolderImpl(codec.encode(nbt));
  }

  /**
   * Creates an encoded binary tag holder.
   *
   * @param string the encoded binary tag value
   * @return the encoded binary tag
   * @since 4.10.0
   */
  static @NotNull BinaryTagHolder binaryTagHolder(final @NotNull String string) {
    return new BinaryTagHolderImpl(string);
  }

  /**
   * Creates an encoded binary tag holder.
   *
   * @param string the encoded binary tag value
   * @return the encoded binary tag
   * @since 4.0.0
   * @deprecated for removal since 4.10.0, use {@link #binaryTagHolder(String)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  static @NotNull BinaryTagHolder of(final @NotNull String string) {
    return new BinaryTagHolderImpl(string);
  }

  /**
   * Gets the raw string value.
   *
   * @return the raw string value
   * @since 4.0.0
   */
  @NotNull String string();

  /**
   * Gets the held value as a binary tag.
   *
   * @param codec the codec
   * @param <T> the binary tag type
   * @param <DX> decode thrown exception type
   * @return the binary tag
   * @throws DX if an error occurred while retrieving the binary tag
   * @since 4.0.0
   */
  <T, DX extends Exception> @NotNull T get(final @NotNull Codec<T, String, DX, ?> codec) throws DX;
}
