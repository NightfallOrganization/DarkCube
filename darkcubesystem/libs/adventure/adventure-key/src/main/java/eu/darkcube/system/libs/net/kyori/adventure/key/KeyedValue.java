/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.key;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * A {@code T} value with an associated {@link Key}.
 *
 * @param <T> the value type
 * @since 4.0.0
 */
public interface KeyedValue<T> extends Keyed {
  /**
   * Creates a link.
   *
   * @param key the key
   * @param value the value
   * @param <T> the value type
   * @return the keyed
   * @since 4.10.0
   */
  static <T> @NotNull KeyedValue<T> keyedValue(final @NotNull Key key, final @NotNull T value) {
    return new KeyedValueImpl<>(key, requireNonNull(value, "value"));
  }

  /**
   * Creates a link.
   *
   * @param key the key
   * @param value the value
   * @param <T> the value type
   * @return the keyed
   * @since 4.0.0
   * @deprecated for removal since 4.10.0, use {@link #keyedValue(Key, Object)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  static <T> @NotNull KeyedValue<T> of(final @NotNull Key key, final @NotNull T value) {
    return new KeyedValueImpl<>(key, requireNonNull(value, "value"));
  }

  /**
   * Gets the value.
   *
   * @return the value
   * @since 4.0.0
   */
  @NotNull T value();
}
