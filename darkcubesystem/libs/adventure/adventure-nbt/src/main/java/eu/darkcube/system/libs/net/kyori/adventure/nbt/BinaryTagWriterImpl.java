/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static eu.darkcube.system.libs.net.kyori.adventure.nbt.IOStreamUtil.closeShield;

final class BinaryTagWriterImpl implements BinaryTagIO.Writer {
  static final BinaryTagIO.Writer INSTANCE = new BinaryTagWriterImpl();

  @Override
  public void write(final @NotNull CompoundBinaryTag tag, final @NotNull Path path, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final OutputStream os = Files.newOutputStream(path)) {
      this.write(tag, os, compression);
    }
  }

  @Override
  public void write(final @NotNull CompoundBinaryTag tag, final @NotNull OutputStream output, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(compression.compress(closeShield(output))))) {
      this.write(tag, (DataOutput) dos);
    }
  }

  @Override
  public void write(final @NotNull CompoundBinaryTag tag, final @NotNull DataOutput output) throws IOException {
    output.writeByte(BinaryTagTypes.COMPOUND.id());
    output.writeUTF(""); // write empty name
    BinaryTagTypes.COMPOUND.write(tag, output);
  }

  @Override
  public void writeNamed(final Map.@NotNull Entry<String, CompoundBinaryTag> tag, final @NotNull Path path, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final OutputStream os = Files.newOutputStream(path)) {
      this.writeNamed(tag, os, compression);
    }
  }

  @Override
  public void writeNamed(final Map.@NotNull Entry<String, CompoundBinaryTag> tag, final @NotNull OutputStream output, final BinaryTagIO.@NotNull Compression compression) throws IOException {
    try (final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(compression.compress(closeShield(output))))) {
      this.writeNamed(tag, (DataOutput) dos);
    }
  }

  @Override
  public void writeNamed(final Map.@NotNull Entry<String, CompoundBinaryTag> tag, final @NotNull DataOutput output) throws IOException {
    output.writeByte(BinaryTagTypes.COMPOUND.id());
    output.writeUTF(tag.getKey());
    BinaryTagTypes.COMPOUND.write(tag.getValue(), output);
  }
}
