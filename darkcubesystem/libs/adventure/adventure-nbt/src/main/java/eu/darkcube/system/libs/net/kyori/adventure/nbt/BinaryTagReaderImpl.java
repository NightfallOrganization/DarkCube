/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static eu.darkcube.system.libs.net.kyori.adventure.nbt.IOStreamUtil.closeShield;

@SuppressWarnings("DuplicatedCode")
final class BinaryTagReaderImpl implements BinaryTagIO.Reader {
  private final long maxBytes;
  static final BinaryTagIO.Reader UNLIMITED = new BinaryTagReaderImpl(-1L);
  static final BinaryTagIO.Reader DEFAULT_LIMIT = new BinaryTagReaderImpl(0x20_00a);

  BinaryTagReaderImpl(final long maxBytes) {
    this.maxBytes = maxBytes;
  }

  @Override
  public @NotNull CompoundBinaryTag read(final @NotNull Path path, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final InputStream is = Files.newInputStream(path)) {
      return this.read(is, compression);
    }
  }

  @Override
  public @NotNull CompoundBinaryTag read(final @NotNull InputStream input, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final DataInputStream dis = new DataInputStream(new BufferedInputStream(compression.decompress(closeShield(input))))) {
      return this.read((DataInput) dis);
    }
  }

  @Override
  public @NotNull CompoundBinaryTag read(@NotNull DataInput input) throws IOException {
    if (!(input instanceof TrackingDataInput)) {
      input = new TrackingDataInput(input, this.maxBytes);
    }

    final BinaryTagType<? extends BinaryTag> type = BinaryTagType.of(input.readByte());
    requireCompound(type);
    input.skipBytes(input.readUnsignedShort()); // read empty name
    return BinaryTagTypes.COMPOUND.read(input);
  }

  @Override
  public Map.@NotNull Entry<String, CompoundBinaryTag> readNamed(final @NotNull Path path, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final InputStream is = Files.newInputStream(path)) {
      return this.readNamed(is, compression);
    }
  }

  @Override
  public Map.@NotNull Entry<String, CompoundBinaryTag> readNamed(final @NotNull InputStream input, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final DataInputStream dis = new DataInputStream(new BufferedInputStream(compression.decompress(closeShield(input))))) {
      return this.readNamed((DataInput) dis);
    }
  }

  @Override
  public Map.@NotNull Entry<String, CompoundBinaryTag> readNamed(final @NotNull DataInput input) throws IOException {
    final BinaryTagType<? extends BinaryTag> type = BinaryTagType.of(input.readByte());
    requireCompound(type);
    final String name = input.readUTF();
    return new AbstractMap.SimpleImmutableEntry<>(name, BinaryTagTypes.COMPOUND.read(input));
  }

  private static void requireCompound(final BinaryTagType<? extends BinaryTag> type) throws IOException {
    if (type != BinaryTagTypes.COMPOUND) {
      throw new IOException(String.format("Expected root tag to be a %s, was %s", BinaryTagTypes.COMPOUND, type));
    }
  }
}
