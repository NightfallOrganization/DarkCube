/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A binary tag type.
 *
 * @param <T> the tag type
 * @since 4.0.0
 */
public abstract class BinaryTagType<T extends BinaryTag> implements Predicate<BinaryTagType<? extends BinaryTag>> {
  private static final List<BinaryTagType<? extends BinaryTag>> TYPES = new ArrayList<>();

  /**
   * Gets the id.
   *
   * @return the id
   * @since 4.0.0
   */
  public abstract byte id();

  abstract boolean numeric();

  /**
   * Reads a tag.
   *
   * @param input the input
   * @return the tag
   * @throws IOException if an exception was encountered while reading
   * @since 4.0.0
   */
  public abstract @NotNull T read(final @NotNull DataInput input) throws IOException;

  /**
   * Writes a tag.
   *
   * @param tag the tag
   * @param output the output
   * @throws IOException if an exception was encountered while writing
   * @since 4.0.0
   */
  public abstract void write(final @NotNull T tag, final @NotNull DataOutput output) throws IOException;

  @SuppressWarnings("unchecked") // HACK: generics suck
  static <T extends BinaryTag> void writeUntyped(final BinaryTagType<? extends BinaryTag> type, final T tag, final DataOutput output) throws IOException {
    ((BinaryTagType<T>) type).write(tag, output);
  }

  static @NotNull BinaryTagType<? extends BinaryTag> of(final byte id) {
    for (int i = 0; i < TYPES.size(); i++) {
      final BinaryTagType<? extends BinaryTag> type = TYPES.get(i);
      if (type.id() == id) {
        return type;
      }
    }
    throw new IllegalArgumentException(String.valueOf(id));
  }

  static <T extends BinaryTag> @NotNull BinaryTagType<T> register(final Class<T> type, final byte id, final Reader<T> reader, final @Nullable Writer<T> writer) {
    return register(new Impl<>(type, id, reader, writer));
  }

  static <T extends NumberBinaryTag> @NotNull BinaryTagType<T> registerNumeric(final Class<T> type, final byte id, final Reader<T> reader, final Writer<T> writer) {
    return register(new Impl.Numeric<>(type, id, reader, writer));
  }

  private static <T extends BinaryTag, Y extends BinaryTagType<T>> Y register(final Y type) {
    TYPES.add(type);
    return type;
  }

  /**
   * A binary tag reader.
   *
   * @param <T> the tag type
   */
  interface Reader<T extends BinaryTag> {
    @NotNull T read(final @NotNull DataInput input) throws IOException;
  }

  /**
   * A binary tag writer.
   *
   * @param <T> the tag type
   */
  interface Writer<T extends BinaryTag> {
    void write(final @NotNull T tag, final @NotNull DataOutput output) throws IOException;
  }

  @Override
  public boolean test(final BinaryTagType<? extends BinaryTag> that) {
    return this == that || (this.numeric() && that.numeric());
  }

  static class Impl<T extends BinaryTag> extends BinaryTagType<T> {
    final Class<T> type;
    final byte id;
    private final Reader<T> reader;
    private final @Nullable Writer<T> writer;

    Impl(final Class<T> type, final byte id, final Reader<T> reader, final @Nullable Writer<T> writer) {
      this.type = type;
      this.id = id;
      this.reader = reader;
      this.writer = writer;
    }

    @Override
    public final @NotNull T read(final @NotNull DataInput input) throws IOException {
      return this.reader.read(input);
    }

    @Override
    public final void write(final @NotNull T tag, final @NotNull DataOutput output) throws IOException {
      if (this.writer != null) this.writer.write(tag, output);
    }

    @Override
    public final byte id() {
      return this.id;
    }

    @Override
    boolean numeric() {
      return false;
    }

    @Override
    public String toString() {
      return BinaryTagType.class.getSimpleName() + '[' + this.type.getSimpleName() + " " + this.id + "]";
    }

    static class Numeric<T extends BinaryTag> extends Impl<T> {
      Numeric(final Class<T> type, final byte id, final Reader<T> reader, final @Nullable Writer<T> writer) {
        super(type, id, reader, writer);
      }

      @Override
      boolean numeric() {
        return true;
      }

      @Override
      public String toString() {
        return BinaryTagType.class.getSimpleName() + '[' + this.type.getSimpleName() + " " + this.id + " (numeric)]";
      }
    }
  }
}
