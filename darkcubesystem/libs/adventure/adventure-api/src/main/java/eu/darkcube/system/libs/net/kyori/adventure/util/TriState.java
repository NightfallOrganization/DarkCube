/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.function.BooleanSupplier;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Similar to a {@code boolean} but with three states.
 *
 * @since 4.8.0
 */
public enum TriState {
  /**
   * State describing the absence of a value.
   *
   * @since 4.8.0
   */
  NOT_SET,
  /**
   * State describing a {@code false} value.
   *
   * @since 4.8.0
   */
  FALSE,
  /**
   * State describing a {@code true} value.
   *
   * @since 4.8.0
   */
  TRUE;

  /**
   * Converts this tri-state back into a {@link Boolean}.
   *
   * @return the boolean representing this tri-state. {@link #NOT_SET} will be represented by {@code null}.
   * @since 4.10.0
   */
  public @Nullable Boolean toBoolean() {
    switch (this) {
      case TRUE: return Boolean.TRUE;
      case FALSE: return Boolean.FALSE;
      default: return null;
    }
  }

  /**
   * Converts this tri-state back into a {@code boolean}.
   *
   * <p>As the {@link #NOT_SET} state cannot be represented by the boolean type, this
   * method maps the {@link #NOT_SET} state to other passed boolean value.
   * This method may hence also be viewed as an equivalent to {@link
   * java.util.Optional#orElse(Object)}.</p>
   *
   * @param other the boolean value that should be returned if this tri-state is {@link #NOT_SET}.
   * @return the boolean representing the tri-state or the boolean passed if this state is {@link #NOT_SET}.
   * @since 4.10.0
   */
  public boolean toBooleanOrElse(final boolean other) {
    switch (this) {
      case TRUE: return true;
      case FALSE: return false;
      default: return other;
    }
  }

  /**
   * Converts this tri-state back into a {@code boolean}.
   *
   * <p>As the {@link #NOT_SET} state cannot be represented by the boolean type, this
   * method maps the {@link #NOT_SET} state to the suppliers result.
   * This method may hence also be viewed as an equivalent to {@link
   * java.util.Optional#orElseGet(java.util.function.Supplier)}.</p>
   *
   * @param supplier the supplier that will be executed to produce the value that should be returned if this tri-state is {@link #NOT_SET}.
   * @return the boolean representing the tri-state or the result of the passed supplier if this state is {@link #NOT_SET}.
   * @since 4.10.0
   */
  public boolean toBooleanOrElseGet(final @NotNull BooleanSupplier supplier) {
    switch (this) {
      case TRUE: return true;
      case FALSE: return false;
      default: return supplier.getAsBoolean();
    }
  }

  /**
   * Gets a state from a {@code boolean}.
   *
   * @param value the boolean
   * @return a tri-state
   * @since 4.8.0
   */
  public static @NotNull TriState byBoolean(final boolean value) {
    return value ? TRUE : FALSE;
  }

  /**
   * Gets a state from a {@link Boolean}.
   *
   * @param value the boolean
   * @return a tri-state
   * @since 4.8.0
   */
  public static @NotNull TriState byBoolean(final @Nullable Boolean value) {
    return value == null ? NOT_SET : byBoolean(value.booleanValue());
  }
}
