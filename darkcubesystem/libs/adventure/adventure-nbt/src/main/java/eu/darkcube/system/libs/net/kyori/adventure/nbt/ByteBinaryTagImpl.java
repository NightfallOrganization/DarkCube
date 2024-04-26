/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Debug;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Debug.Renderer(text = "\"0x\" + Integer.toString(this.value, 16)", hasChildren = "false")
final class ByteBinaryTagImpl extends AbstractBinaryTag implements ByteBinaryTag {
  private final byte value;

  ByteBinaryTagImpl(final byte value) {
    this.value = value;
  }

  @Override
  public byte value() {
    return this.value;
  }

  @Override
  public byte byteValue() {
    return this.value;
  }

  @Override
  public double doubleValue() {
    return this.value;
  }

  @Override
  public float floatValue() {
    return this.value;
  }

  @Override
  public int intValue() {
    return this.value;
  }

  @Override
  public long longValue() {
    return this.value;
  }

  @Override
  public short shortValue() {
    return this.value;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final ByteBinaryTagImpl that = (ByteBinaryTagImpl) other;
    return this.value == that.value;
  }

  @Override
  public int hashCode() {
    return Byte.hashCode(this.value);
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("value", this.value));
  }
}
