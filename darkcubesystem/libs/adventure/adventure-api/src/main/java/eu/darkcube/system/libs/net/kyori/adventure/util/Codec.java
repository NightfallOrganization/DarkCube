/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A combination encoder and decoder.
 *
 * @param <D> the decoded type
 * @param <E> the encoded type
 * @param <DX> the decode exception type
 * @param <EX> the encode exception type
 * @since 4.0.0
 */
public interface Codec<D, E, DX extends Throwable, EX extends Throwable> {
  /**
   * Creates a codec.
   *
   * @param decoder the decoder
   * @param encoder the encoder
   * @param <D> the decoded type
   * @param <E> the encoded type
   * @param <DX> the decode exception type
   * @param <EX> the encode exception type
   * @return a codec
   * @since 4.10.0
   */
  static <D, E, DX extends Throwable, EX extends Throwable> @NotNull Codec<D, E, DX, EX> codec(final @NotNull Decoder<D, E, DX> decoder, final @NotNull Encoder<D, E, EX> encoder) {
    return new Codec<D, E, DX, EX>() {
      @Override
      public @NotNull D decode(final @NotNull E encoded) throws DX {
        return decoder.decode(encoded);
      }

      @Override
      public @NotNull E encode(final @NotNull D decoded) throws EX {
        return encoder.encode(decoded);
      }
    };
  }

  /**
   * Creates a codec.
   *
   * @param decoder the decoder
   * @param encoder the encoder
   * @param <D> the decoded type
   * @param <E> the encoded type
   * @param <DX> the decode exception type
   * @param <EX> the encode exception type
   * @return a codec
   * @since 4.0.0
   * @deprecated for removal since 4.10.0, use {@link #codec(Codec.Decoder, Codec.Encoder)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  static <D, E, DX extends Throwable, EX extends Throwable> @NotNull Codec<D, E, DX, EX> of(final @NotNull Decoder<D, E, DX> decoder, final @NotNull Encoder<D, E, EX> encoder) {
    return new Codec<D, E, DX, EX>() {
      @Override
      public @NotNull D decode(final @NotNull E encoded) throws DX {
        return decoder.decode(encoded);
      }

      @Override
      public @NotNull E encode(final @NotNull D decoded) throws EX {
        return encoder.encode(decoded);
      }
    };
  }

  /**
   * Decodes.
   *
   * @param encoded the encoded input
   * @return the decoded value
   * @throws DX if an exception is encountered while decoding
   * @since 4.0.0
   */
  @NotNull D decode(final @NotNull E encoded) throws DX;

  /**
   * A decoder.
   *
   * @param <D> the decoded type
   * @param <E> the encoded type
   * @param <X> the exception type
   * @since 4.0.0
   */
  interface Decoder<D, E, X extends Throwable> {
    /**
     * Decodes.
     *
     * @param encoded the encoded input
     * @return the decoded value
     * @throws X if an exception is encountered while decoding
     * @since 4.0.0
     */
    @NotNull D decode(final @NotNull E encoded) throws X;
  }

  /**
   * Encodes.
   *
   * @param decoded the decoded value
   * @return the encoded output
   * @throws EX if an exception is encountered while encoding
   * @since 4.0.0
   */
  @NotNull E encode(final @NotNull D decoded) throws EX;

  /**
   * An encoder.
   *
   * @param <D> the decoded type
   * @param <E> the encoded type
   * @param <X> the exception type
   * @since 4.0.0
   */
  interface Encoder<D, E, X extends Throwable> {
    /**
     * Encodes.
     *
     * @param decoded the decoded value
     * @return the encoded output
     * @throws X if an exception is encountered while encoding
     * @since 4.0.0
     */
    @NotNull E encode(final @NotNull D decoded) throws X;
  }
}
