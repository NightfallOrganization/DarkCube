/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A numeric binary tag.
 *
 * @since 4.0.0
 */
public interface NumberBinaryTag extends BinaryTag {
  @Override
  @NotNull BinaryTagType<? extends NumberBinaryTag> type();

  /**
   * Gets the value as a {@code byte}.
   *
   * @return the value as a {@code byte}
   * @since 4.0.0
   */
  byte byteValue();

  /**
   * Gets the value as a {@code double}.
   *
   * @return the value as a {@code double}
   * @since 4.0.0
   */
  double doubleValue();

  /**
   * Gets the value as a {@code float}.
   *
   * @return the value as a {@code float}
   * @since 4.0.0
   */
  float floatValue();

  /**
   * Gets the value as a {@code int}.
   *
   * @return the value as a {@code int}
   * @since 4.0.0
   */
  int intValue();

  /**
   * Gets the value as a {@code long}.
   *
   * @return the value as a {@code long}
   * @since 4.0.0
   */
  long longValue();

  /**
   * Gets the value as a {@code short}.
   *
   * @return the value as a {@code short}
   * @since 4.0.0
   */
  short shortValue();
}
