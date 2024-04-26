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

@Debug.Renderer(text = "String.valueOf(this.value) + \"s\"", hasChildren = "false")
final class ShortBinaryTagImpl extends AbstractBinaryTag implements ShortBinaryTag {
  private final short value;

  ShortBinaryTagImpl(final short value) {
    this.value = value;
  }

  @Override
  public short value() {
    return this.value;
  }

  @Override
  public byte byteValue() {
    return (byte) (this.value & 0xff);
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
    final ShortBinaryTagImpl that = (ShortBinaryTagImpl) other;
    return this.value == that.value;
  }

  @Override
  public int hashCode() {
    return Short.hashCode(this.value);
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("value", this.value));
  }
}
