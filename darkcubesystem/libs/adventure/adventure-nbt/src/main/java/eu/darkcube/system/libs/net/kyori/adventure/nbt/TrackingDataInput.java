/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.io.DataInput;
import java.io.IOException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class TrackingDataInput implements DataInput, BinaryTagScope {
  private static final int MAX_DEPTH = 512;
  private final DataInput input;
  private final long maxLength;
  private long counter;
  private int depth;

  TrackingDataInput(final DataInput input, final long maxLength) {
    this.input = input;
    this.maxLength = maxLength;
  }

  public static BinaryTagScope enter(final DataInput input) throws IOException {
    if (input instanceof TrackingDataInput) {
      return ((TrackingDataInput) input).enter();
    } else {
      return NoOp.INSTANCE;
    }
  }

  public static BinaryTagScope enter(final DataInput input, final long expectedSize) throws IOException {
    if (input instanceof TrackingDataInput) {
      return ((TrackingDataInput) input).enter(expectedSize);
    } else {
      return NoOp.INSTANCE;
    }
  }

  public DataInput input() {
    return this.input;
  }

  // enter a nesting level that pre-allocates storage
  public TrackingDataInput enter(final long expectedSize) throws IOException {
    if (this.depth++ > MAX_DEPTH) {
      throw new IOException("NBT read exceeded maximum depth of " + MAX_DEPTH);
    }

    this.ensureMaxLength(expectedSize);
    return this;
  }

  public TrackingDataInput enter() throws IOException {
    return this.enter(0);
  }

  public void exit() throws IOException {
    this.depth--;
    this.ensureMaxLength(0);
  }

  private void ensureMaxLength(final long expected) throws IOException {
    if (this.maxLength > 0 && this.counter + expected > this.maxLength) {
      throw new IOException("The read NBT was longer than the maximum allowed size of " + this.maxLength + " bytes!");
    }
  }

  @Override
  public void readFully(final byte@NotNull[] array) throws IOException {
    this.counter += array.length;
    this.input.readFully(array);
  }

  @Override
  public void readFully(final byte@NotNull[] array, final int off, final int len) throws IOException {
    this.counter += len;
    this.input.readFully(array, off, len);
  }

  @Override
  public int skipBytes(final int n) throws IOException {
    return this.input.skipBytes(n);
  }

  @Override
  public boolean readBoolean() throws IOException {
    this.counter++;
    return this.input.readBoolean();
  }

  @Override
  public byte readByte() throws IOException {
    this.counter++;
    return this.input.readByte();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    this.counter++;
    return this.input.readUnsignedByte();
  }

  @Override
  public short readShort() throws IOException {
    this.counter += (Short.SIZE / Byte.SIZE);
    return this.input.readShort();
  }

  @Override
  public int readUnsignedShort() throws IOException {
    this.counter += (Short.SIZE / Byte.SIZE);
    return this.input.readUnsignedShort();
  }

  @Override
  public char readChar() throws IOException {
    this.counter += (Character.SIZE / Byte.SIZE);
    return this.input.readChar();
  }

  @Override
  public int readInt() throws IOException {
    this.counter += (Integer.SIZE / Byte.SIZE);
    return this.input.readInt();
  }

  @Override
  public long readLong() throws IOException {
    this.counter += (Long.SIZE / Byte.SIZE);
    return this.input.readLong();
  }

  @Override
  public float readFloat() throws IOException {
    this.counter += (Float.SIZE / Byte.SIZE);
    return this.input.readFloat();
  }

  @Override
  public double readDouble() throws IOException {
    this.counter += (Double.SIZE / Byte.SIZE);
    return this.input.readDouble();
  }

  @Override
  public @Nullable String readLine() throws IOException {
    final @Nullable String result = this.input.readLine();
    if (result != null) {
      this.counter += result.length() + 1;
    }
    return result;
  }

  @Override
  public @NotNull String readUTF() throws IOException {
    final String result = this.input.readUTF();
    this.counter += (result.length() * 2L) + 2; // not entirely accurate, but the closest we can get without doing implementation details
    return result;
  }

  @Override
  public void close() throws IOException {
    this.exit();
  }

}
